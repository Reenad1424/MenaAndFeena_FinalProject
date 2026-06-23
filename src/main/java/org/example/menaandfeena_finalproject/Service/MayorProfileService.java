package org.example.menaandfeena_finalproject.Service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.MayorProfileInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.*;
import org.example.menaandfeena_finalproject.Model.*;
import org.example.menaandfeena_finalproject.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MayorProfileService {
    private static final Logger log = LoggerFactory.getLogger(MayorProfileService.class);

    private final MayorProfileRepository mayorProfileRepository;
    private final UserRepository userRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final IssueReportRepository issueReportRepository;
    private final EventRepository eventRepository;
    private final InitiativeRepository initiativeRepository;
    private final ReviewRepository reviewRepository;
    private final OpenAIService openAIService;
    private final PdfService pdfService;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

    public List<MayorProfile> getAllMayorProfiles() {
        return mayorProfileRepository.findAll();
    }

    public void addMayorProfile(MayorProfileInDTO dto) {
        User user = userRepository.findUserById(dto.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }

        Neighborhood neighborhood = neighborhoodRepository.findNeighborhoodById(dto.getNeighborhoodId());
        if (neighborhood == null) {
            throw new ApiException("Neighborhood not found");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);

        MayorProfile currentActiveMayor =
                mayorProfileRepository.findTopByNeighborhoodIdAndStatusOrderByStartDateDesc(
                        neighborhood.getId(),
                        "ACTIVE"
                );

        if (currentActiveMayor != null
                && currentActiveMayor.getUser() != null
                && !currentActiveMayor.getUser().getId().equals(user.getId())) {
            User oldMayor = currentActiveMayor.getUser();
            oldMayor.setStatus("RESIDENT");
            oldMayor.setMayorActive(false);
            oldMayor.setMayorEndDate(startDate);
            userRepository.save(oldMayor);

            currentActiveMayor.setStatus("INACTIVE");
            currentActiveMayor.setEndDate(startDate);
            mayorProfileRepository.save(currentActiveMayor);
        }

        MayorProfile mayorProfile = new MayorProfile();
        mayorProfile.setStatus("ACTIVE");
        mayorProfile.setStartDate(startDate);
        mayorProfile.setEndDate(endDate);
        mayorProfile.setUser(user);
        mayorProfile.setNeighborhood(neighborhood);

        user.setStatus("MAYOR");
        user.setMayorActive(true);
        user.setMayorStartDate(startDate);
        user.setMayorEndDate(endDate);
        userRepository.save(user);

        MayorProfile savedMayorProfile = mayorProfileRepository.save(mayorProfile);
    }

    public void updateMayorProfile(Integer id, MayorProfileInDTO dto) {

        MayorProfile oldMayorProfile = mayorProfileRepository.findMayorProfileById(id);

        if (oldMayorProfile == null) {
            throw new ApiException("Mayor profile not found");
        }

        User user = userRepository.findUserById(dto.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }

        Neighborhood neighborhood = neighborhoodRepository.findNeighborhoodById(dto.getNeighborhoodId());
        if (neighborhood == null) {
            throw new ApiException("Neighborhood not found");
        }

        User previousMayor = oldMayorProfile.getUser();

        oldMayorProfile.setUser(user);
        oldMayorProfile.setNeighborhood(neighborhood);

        if ("ACTIVE".equals(oldMayorProfile.getStatus())) {
            if (previousMayor != null && !previousMayor.getId().equals(user.getId())) {
                previousMayor.setStatus("RESIDENT");
                previousMayor.setMayorActive(false);
                previousMayor.setMayorEndDate(LocalDate.now());
                userRepository.save(previousMayor);
            }

            user.setStatus("MAYOR");
            user.setMayorActive(true);
            user.setMayorStartDate(oldMayorProfile.getStartDate());
            user.setMayorEndDate(oldMayorProfile.getEndDate());
            userRepository.save(user);
        }

        mayorProfileRepository.save(oldMayorProfile);
    }

    public void deleteMayorProfile(Integer id) {

        MayorProfile mayorProfile = mayorProfileRepository.findMayorProfileById(id);

        if (mayorProfile == null) {
            throw new ApiException("Mayor profile not found");
        }

        if ("ACTIVE".equals(mayorProfile.getStatus()) && mayorProfile.getUser() != null) {
            User mayor = mayorProfile.getUser();
            mayor.setStatus("RESIDENT");
            mayor.setMayorActive(false);
            mayor.setMayorEndDate(LocalDate.now());
            userRepository.save(mayor);
        }

        mayorProfileRepository.delete(mayorProfile);
    }


    //Reenad
    // ANALYTICS (profile)
    public MayorAnalyticsDTO getMayorAnalytics(Integer mayorId) {

        User mayor = validateMayor(mayorId);

        MayorAnalyticsDTO dto = new MayorAnalyticsDTO();

        dto.setBasicInfo(buildMayorBasicInfo(mayor));

        List<MayorReportCardDTO> reports = List.of(
                new MayorReportCardDTO(
                        "تحليل رضا السكان",
                        "تحليل بيانات رضا السكان بناءً على البلاغات والتقييمات"
                ),
                new MayorReportCardDTO(
                        "تقرير أداء الحي",
                        "قياس سرعة الاستجابة وجودة الخدمات في الحي"
                ),
                new MayorReportCardDTO(
                        "التقرير الأسبوعي",
                        "ملخص أسبوعي شامل لنشاط الحي والبلاغات"
                )
        );

        dto.setReports(reports);
        dto.setTotalReports(reports.size());

        return dto;
    }

    // REPORTS (URGENT / NON-URGENT /PERIODIC)
    public MayorReportsDTO getMayorReports(Integer mayorId) {

        User mayor = validateMayor(mayorId);

        if (mayor.getNeighborhood() == null) {
            throw new ApiException("Mayor is not assigned to a neighborhood");
        }

        Integer neighborhoodId = mayor.getNeighborhood().getId();

        List<IssueReport> allReports =
                issueReportRepository.findByReporter_Neighborhood_Id(neighborhoodId);

        List<IssueReportDTO> urgent = new ArrayList<>();
        List<IssueReportDTO> nonUrgent = new ArrayList<>();
        List<IssueReportDTO> periodic = new ArrayList<>();

        for (IssueReport r : allReports) {

            IssueReportDTO dto = new IssueReportDTO();
            dto.setId(r.getId());
            dto.setTitle(r.getTitle());
            dto.setCategory(r.getCategory());
            dto.setPriority(r.getPriority());
            dto.setStatus(r.getStatus());
            dto.setCreatedAt(r.getCreatedAt());

            switch (r.getPriority()) {
                case "URGENT" -> urgent.add(dto);
                case "NON_URGENT" -> nonUrgent.add(dto);
                case "PERIODIC" -> periodic.add(dto);
            }
        }

        MayorReportsDTO result = new MayorReportsDTO();

        result.setUrgentReports(urgent);
        result.setNonUrgentReports(nonUrgent);
        result.setPeriodicReports(periodic);
        return result;
    }

    private MayorBasicProfileDTO buildMayorBasicInfo(User mayor) {

        MayorBasicProfileDTO dto = new MayorBasicProfileDTO();

        MayorProfile profile =
                mayorProfileRepository.findTopByUserIdOrderByStartDateDesc(
                        mayor.getId()
                );

        dto.setMayorId(mayor.getId());
        dto.setFullName(mayor.getFullName());
        dto.setNationalId(mayor.getNationalId());

        dto.setNeighborhoodName(
                mayor.getNeighborhood() != null
                        ? mayor.getNeighborhood().getName()
                        : "غير مرتبط بحي"
        );

        if (profile != null) {
            dto.setStatus(profile.getStatus());
            dto.setStartDate(profile.getStartDate());
            dto.setEndDate(profile.getEndDate());
        } else {
            dto.setStatus("INACTIVE");
            dto.setStartDate(null);
            dto.setEndDate(null);
        }

        return dto;
    }

// =========================
// 1- WEEKLY REPORT
// =========================

    public void sendWeeklyReport(Integer mayorId) {

        User mayor = validateMayor(mayorId);

        Integer neighborhoodId = mayor.getNeighborhood().getId();

        List<IssueReport> reports =
                issueReportRepository.findByReportNeighborhood_Id(neighborhoodId);

        long total = 0;
        long completed = 0;
        long progress = 0;
        long urgent = 0;

        for (IssueReport r : reports) {

            total++;

            if ("COMPLETED".equals(r.getStatus())) {
                completed++;
            }

            if ("IN_PROGRESS".equals(r.getStatus())) {
                progress++;
            }

            if ("URGENT".equals(r.getPriority())) {
                urgent++;
            }
        }

        String data =
                "اجمالي البلاغات: " + total +
                        "\nتم حلها: " + completed +
                        "\nقيد المعالجة: " + progress +
                        "\nعاجلة: " + urgent;

        String ai =
                openAIService.askAI(
                        "أنت محلل تقارير بلدية، اكتب ملخص إداري احترافي مختصر وواضح" + PdfReportSupport.AI_PLAIN_ARABIC,
                        "حلل البيانات التالية:\n" + data
                );
        StringBuilder rows = new StringBuilder();

        for (IssueReport r : reports) {
            rows.append("<tr>")
                    .append("<td>").append(r.getTitle()).append("</td>")
                    .append("<td>").append(r.getDetectedStreetName() != null ? r.getDetectedStreetName() : "غير محدد").append("</td>")
                    .append("<td>").append(r.getStatus()).append("</td>")
                    .append("</tr>");
        }

        File pdf = pdfService.createPdf(
                generateFileName("weekly-report.pdf"),
                weeklyReportHtml(total, progress, completed, urgent, rows.toString(), ai)
        );

        emailService.sendEmailWithAttachments(
                mayor.getEmail(),
                "التقرير الأسبوعي للحي",
                "",
                pdf
        );
    }


// =========================
// 2- PERFORMANCE REPORT
// =========================
public void sendPerformanceReport(Integer mayorId) {

    User mayor = validateMayor(mayorId);

    Integer neighborhoodId = mayor.getNeighborhood().getId();

    List<IssueReport> reports =
            issueReportRepository.findByReportNeighborhood_Id(neighborhoodId);

    List<Event> events =
            eventRepository.findByNeighborhood_Id(neighborhoodId);

    List<Initiative> initiatives =
            initiativeRepository.findByNeighborhood_Id(neighborhoodId);

    double lighting = score(reports, "LIGHTING");
    double cleanliness = score(reports, "CLEANLINESS");
    double parks = score(reports, "PARKS");
    double infrastructure = scoreInfrastructure(reports);

    long completedReports =
            reports.stream()
                    .filter(r -> "COMPLETED".equals(r.getStatus()))
                    .count();

    double responseSpeed =
            reports.isEmpty()
                    ? 0
                    : (completedReports * 100.0 / reports.size());

    double socialParticipation =
            Math.min(
                    100,
                    (events.size() * 5) + (initiatives.size() * 5)
            );

    double overall =
            (
                    lighting +
                            cleanliness +
                            parks +
                            infrastructure +
                            responseSpeed +
                            socialParticipation
            ) / 6;

    String ai =
            openAIService.askAI(
                    "أنت خبير تقييم أداء الأحياء" + PdfReportSupport.AI_PLAIN_ARABIC,
                    """
                    قيم أداء الحي بناءً على البيانات التالية:

                    جودة الإنارة: %s
                    مستوى النظافة: %s
                    جودة الحدائق: %s
                    البنية التحتية: %s
                    سرعة معالجة البلاغات: %s
                    المشاركة المجتمعية: %s
                    التقييم العام: %s

                    اكتب ملخصاً إدارياً احترافياً من 3 أسطر.
                    """
                            .formatted(
                                    lighting,
                                    cleanliness,
                                    parks,
                                    infrastructure,
                                    responseSpeed,
                                    socialParticipation,
                                    overall
                            )
            );

    File pdf =
            pdfService.createPdf(
                    generateFileName("performance.pdf"),
                    performanceReportHtml(
                            overall,
                            lighting,
                            cleanliness,
                            parks,
                            infrastructure,
                            responseSpeed,
                            socialParticipation,
                            ai
                    )
            );

    emailService.sendEmailWithAttachments(
            mayor.getEmail(),
            "تقرير أداء الحي",
            "",
            pdf
    );
}


// =========================
// 3- SATISFACTION REPORT
// =========================

    public void sendSatisfactionReport(Integer mayorId) {

        User mayor = validateMayor(mayorId);

        Integer neighborhoodId = mayor.getNeighborhood().getId();

        List<Review> reviews =
                reviewRepository.findByUser_Neighborhood_Id(neighborhoodId);

        List<IssueReport> reports =
                issueReportRepository.findByReportNeighborhood_Id(neighborhoodId);

        double avg =
                reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0);

        double satisfaction =
                avg * 20;

        double cleanlinessSatisfaction =
                score(reports, "CLEANLINESS");

        double lightingSatisfaction =
                score(reports, "LIGHTING");

        double parksSatisfaction =
                score(reports, "PARKS");

        double infrastructureSatisfaction =
                scoreInfrastructure(reports);

        List<Review> eventReviews =
                reviews.stream()
                        .filter(r -> r.getEvent() != null)
                        .toList();

        double eventsSatisfaction =
                eventReviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(avg) * 20;

        long completedReports =
                reports.stream()
                        .filter(r -> "COMPLETED".equals(r.getStatus()))
                        .count();

        double reportsSatisfaction =
                reports.isEmpty()
                        ? 0
                        : (completedReports * 100.0 / reports.size());

        String ai =
                openAIService.askAI(
                        "أنت محلل رضا السكان" + PdfReportSupport.AI_PLAIN_ARABIC,
                        """
                        حلل رضا السكان بناءً على البيانات التالية:
    
                        نسبة الرضا العامة: %s
                        الرضا عن النظافة: %s
                        الرضا عن الإنارة: %s
                        الرضا عن الحدائق: %s
                        الرضا عن الفعاليات: %s
                        الرضا عن معالجة البلاغات: %s
                        الرضا عن البنية التحتية: %s
    
                        اكتب ملخصاً إدارياً قصيراً وواضحاً من 3 اسطر .
                        """
                                .formatted(
                                        satisfaction,
                                        cleanlinessSatisfaction,
                                        lightingSatisfaction,
                                        parksSatisfaction,
                                        eventsSatisfaction,
                                        reportsSatisfaction,
                                        infrastructureSatisfaction
                                )
                );

        File pdf =
                pdfService.createPdf(
                        generateFileName("satisfaction.pdf"),
                        satisfactionReportHtml(
                                satisfaction,
                                cleanlinessSatisfaction,
                                lightingSatisfaction,
                                parksSatisfaction,
                                eventsSatisfaction,
                                reportsSatisfaction,
                                infrastructureSatisfaction,
                                ai
                        )
                );

        emailService.sendEmailWithAttachments(
                mayor.getEmail(),
                "تقرير رضا السكان",
                "",
                pdf
        );
    }

    public MayorInitiativeSuggestionDTO getInitiativeSuggestions(Integer mayorId) {

        User mayor = validateMayor(mayorId);

        if (mayor.getNeighborhood() == null) {
            throw new ApiException("Mayor is not assigned to a neighborhood");
        }

        Integer neighborhoodId = mayor.getNeighborhood().getId();

        List<IssueReport> reports =
                issueReportRepository.findByReportNeighborhood_Id(neighborhoodId);

        List<Review> reviews =
                reviewRepository.findByUser_Neighborhood_Id(neighborhoodId);

        List<Event> events =
                eventRepository.findByNeighborhood_Id(neighborhoodId);

        List<Initiative> initiatives =
                initiativeRepository.findByNeighborhood_Id(neighborhoodId);

        double averageRating =
                reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0);

        long urgentReports =
                reports.stream()
                        .filter(report -> "URGENT".equals(report.getPriority()))
                        .count();

        long openReports =
                reports.stream()
                        .filter(report -> !"COMPLETED".equals(report.getStatus()))
                        .count();

        StringBuilder issueSummary = new StringBuilder();
        for (IssueReport report : reports) {
            issueSummary.append("- ")
                    .append(report.getTitle())
                    .append(" | Category: ")
                    .append(report.getCategory())
                    .append(" | Priority: ")
                    .append(report.getPriority())
                    .append(" | Status: ")
                    .append(report.getStatus())
                    .append("\n");
        }

        StringBuilder recentEvents = new StringBuilder();
        for (Event event : events) {
            recentEvents.append("- ")
                    .append(event.getTitle())
                    .append(" | Status: ")
                    .append(event.getStatus())
                    .append(" | Date: ")
                    .append(event.getDate())
                    .append("\n");
        }

        StringBuilder recentInitiatives = new StringBuilder();
        for (Initiative initiative : initiatives) {
            recentInitiatives.append("- ")
                    .append(initiative.getTitle())
                    .append(" | Category: ")
                    .append(initiative.getCategory())
                    .append(" | Status: ")
                    .append(initiative.getStatus())
                    .append(" | Date: ")
                    .append(initiative.getDate())
                    .append("\n");
        }

        String suggestionsJson = openAIService.askAI(
                """
                You are an AI advisor for a neighborhood mayor.
                Suggest practical initiatives that improve resident happiness, appreciation, and trust in the mayor.
                Rules:
                - Return valid JSON only. No markdown, no code fences, no explanation outside JSON.
                - All text values must be written in Arabic.
                - Use only the provided neighborhood data.
                - Focus on initiatives the mayor can realistically create in the app.
                - Include appreciation/community ideas, not only maintenance fixes.
                - Do not invent exact statistics.
                - Keep it concise and actionable.
                Return exactly this JSON shape:
                {
                  "overallInsight": "فقرة عربية قصيرة",
                  "suggestedInitiatives": [
                    {
                      "name": "اسم المبادرة بالعربية",
                      "goal": "هدف المبادرة بالعربية",
                      "reason": "سبب الاقتراح بالعربية",
                      "targetResidents": "الفئة المستهدفة بالعربية"
                    }
                  ],
                  "appreciationIdea": "فكرة تقدير قصيرة بالعربية"
                }
                Return exactly 3 suggested initiatives.
                """,
                """
                Neighborhood: %s
                Average rating: %.2f/5
                Total issue reports: %s
                Open issue reports: %s
                Urgent issue reports: %s
                Total events: %s
                Total initiatives: %s

                Issue reports:
                %s

                Events:
                %s

                Initiatives:
                %s
                """.formatted(
                        mayor.getNeighborhood().getName(),
                        averageRating,
                        reports.size(),
                        openReports,
                        urgentReports,
                        events.size(),
                        initiatives.size(),
                        issueSummary,
                        recentEvents,
                        recentInitiatives
                )
        );

        if (suggestionsJson == null || suggestionsJson.isBlank() || "ERROR_FALLBACK".equals(suggestionsJson)) {
            return buildFallbackInitiativeSuggestions(mayor.getNeighborhood().getName(), openReports, urgentReports, averageRating);
        }

        try {
            String cleanJson = suggestionsJson.trim();
            if (cleanJson.startsWith("```")) {
                cleanJson = cleanJson
                        .replaceFirst("^```json", "")
                        .replaceFirst("^```", "")
                        .replaceFirst("```$", "")
                        .trim();
            }

            MayorInitiativeSuggestionDTO dto = new ObjectMapper().readValue(cleanJson, MayorInitiativeSuggestionDTO.class);
            if (dto.getSuggestedInitiatives() == null || dto.getSuggestedInitiatives().isEmpty()) {
                return buildFallbackInitiativeSuggestions(mayor.getNeighborhood().getName(), openReports, urgentReports, averageRating);
            }
            return dto;
        } catch (Exception e) {
            log.warn("AI initiative suggestions returned invalid JSON for mayor id {}", mayorId);
            return buildFallbackInitiativeSuggestions(mayor.getNeighborhood().getName(), openReports, urgentReports, averageRating);
        }
    }

    private MayorInitiativeSuggestionDTO buildFallbackInitiativeSuggestions(String neighborhoodName,
                                                                           long openReports,
                                                                           long urgentReports,
                                                                           double averageRating) {
        String insight = "يحتاج حي " + neighborhoodName + " إلى مبادرات تجمع بين تحسين الخدمات وتعزيز شعور السكان بالتقدير والانتماء.";
        if (urgentReports > 0) {
            insight = "توجد بلاغات عاجلة في حي " + neighborhoodName + "، لذلك الأفضل إطلاق مبادرات عملية تعالج الأولويات وتزيد ثقة السكان.";
        } else if (openReports > 0) {
            insight = "توجد بلاغات مفتوحة في حي " + neighborhoodName + "، ويمكن دعم رضا السكان عبر مبادرات متابعة واضحة وتواصل مباشر.";
        } else if (averageRating >= 4) {
            insight = "مؤشرات الرضا في حي " + neighborhoodName + " جيدة، ويمكن البناء عليها بمبادرات تقدير ومشاركة مجتمعية.";
        }

        return new MayorInitiativeSuggestionDTO(
                insight,
                Arrays.asList(
                        new MayorInitiativeSuggestionDTO.SuggestedInitiativeDTO(
                                "جولة تحسين مرافق الحي",
                                "متابعة الملاحظات الخدمية وتحسين الأماكن الأكثر استخداماً",
                                "تساعد السكان على رؤية أثر سريع وملموس لعمل العمدة",
                                "الأسر وكبار السن ومستخدمي المرافق اليومية"
                        ),
                        new MayorInitiativeSuggestionDTO.SuggestedInitiativeDTO(
                                "لقاء سكان الحي الشهري",
                                "فتح مساحة مباشرة لسماع المقترحات وترتيب الأولويات",
                                "يعزز الثقة ويجعل السكان يشعرون أن صوتهم مسموع",
                                "جميع سكان الحي"
                        ),
                        new MayorInitiativeSuggestionDTO.SuggestedInitiativeDTO(
                                "مبادرة شكر المتطوعين والجيران المتعاونين",
                                "تقدير السكان الذين يساهمون في خدمة الحي",
                                "التقدير العلني يزيد المشاركة والانتماء",
                                "المتطوعون والأسر والشباب"
                        )
                ),
                "إرسال رسالة شكر شهرية باسم العمدة تذكر إنجازات السكان ومساهماتهم في تحسين الحي."
        );
    }


    // SCORE
    private double score(List<IssueReport> reports, String category) {

        long count =
                reports.stream()
                        .filter(r -> category.equals(r.getCategory()))
                        .count();

        return Math.max(
                0,
                Math.min(100, 100 - (count * 5))
        );
    }

    private double scoreInfrastructure(List<IssueReport> reports) {

        long count =
                reports.stream()
                        .filter(r ->
                                "ROADS".equals(r.getCategory()) ||
                                        "WATER_AND_SEWAGE".equals(r.getCategory())
                        )
                        .count();

        return Math.max(
                0,
                Math.min(100, 100 - (count * 5))
        );
    }

    // VALIDATION
    private User validateMayor(Integer id) {

        User user = userRepository.findUserById(id);

        if (user == null ||
                user.getStatus() == null ||
                !"MAYOR".equalsIgnoreCase(user.getStatus())) {

            throw new RuntimeException("Not Mayor");
        }

        if (user.getNeighborhood() == null) {
            throw new RuntimeException("Mayor has no neighborhood");
        }

        return user;
    }


    // FILE NAME GENERATOR
    private String generateFileName(String base) {
        return System.currentTimeMillis() + "-" + base;
    }

    private String nowStamp() {
        return java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // صف مؤشر واحد في تقارير الأداء/الرضا (عنوان + قيمة نسبة مئوية).
    private record MetricRow(String label, double value) {}

    // قالب موحّد لتقارير المؤشرات (الأداء والرضا): غلاف + مؤشر عام + بطاقات + تحليل ذكي.
    // مبني بالربط النصي لتفادي مشاكل علامة % داخل قوالب .formatted.
    private String metricReportHtml(String reportTitle, String reportSubtitle, String overallLabel,
                                    double overallValue, String ratingLabel,
                                    java.util.List<MetricRow> metrics, String ai) {
        String logo = PdfReportSupport.logoBase64();
        String logoTag = logo.isEmpty() ? "" : "<img class=\"logo\" src=\"" + logo + "\" alt=\"logo\" />";
        String overallColor = PdfReportSupport.percentColor(overallValue);

        StringBuilder cards = new StringBuilder();
        cards.append("<table class=\"metrics\">");
        for (int i = 0; i < metrics.size(); i++) {
            if (i % 3 == 0) {
                cards.append("<tr>");
            }
            MetricRow m = metrics.get(i);
            String c = PdfReportSupport.percentColor(m.value());
            long w = Math.round(Math.max(0, Math.min(100, m.value())));
            cards.append("<td><div class=\"metric\">")
                    .append("<div class=\"mt\">").append(PdfReportSupport.esc(m.label())).append("</div>")
                    .append("<div class=\"mv\" style=\"color:").append(c).append(";\">").append(Math.round(m.value())).append("%</div>")
                    .append("<div class=\"bar\"><div class=\"fill\" style=\"width:").append(w).append("%;background:").append(c).append(";\"></div></div>")
                    .append("</div></td>");
            if (i % 3 == 2) {
                cards.append("</tr>");
            }
        }
        int rem = metrics.size() % 3;
        if (rem != 0) {
            for (int k = rem; k < 3; k++) {
                cards.append("<td></td>");
            }
            cards.append("</tr>");
        }
        cards.append("</table>");

        return "<!DOCTYPE html><html lang=\"ar\" dir=\"rtl\"><head><meta charset=\"UTF-8\"/><style>"
                + PdfReportSupport.css() + "</style></head><body>"
                + "<div class=\"cover\">" + logoTag
                + "<div class=\"platform\">منصة منا وفينا</div><div class=\"bar\"></div>"
                + "<div class=\"rtitle\">" + PdfReportSupport.esc(reportTitle) + "</div>"
                + "<div class=\"rsub\">" + PdfReportSupport.esc(reportSubtitle) + "</div>"
                + "<div class=\"rmeta\">تاريخ الإصدار: " + PdfReportSupport.esc(nowStamp()) + "</div></div>"
                + "<div class=\"page-break\"></div>"
                + "<div class=\"section\"><div class=\"section-title\">المؤشر العام</div>"
                + "<div class=\"overall\"><div class=\"ot\">" + PdfReportSupport.esc(overallLabel) + "</div>"
                + "<div class=\"ov\" style=\"color:" + overallColor + ";\">" + Math.round(overallValue) + "%</div>"
                + "<div class=\"ob\">" + PdfReportSupport.esc(ratingLabel) + "</div></div></div>"
                + "<div class=\"section\"><div class=\"section-title\">المؤشرات التفصيلية</div>" + cards + "</div>"
                + "<div class=\"page-break\"></div>"
                + "<div class=\"section\"><div class=\"section-title\">التحليل التنفيذي الذكي</div>"
                + "<div class=\"ai\">" + PdfReportSupport.aiHtml(ai) + "</div></div>"
                + "</body></html>";
    }

    // HTML TEMPLATES
    private String weeklyReportHtml(
            long total,
            long progress,
            long completed,
            long urgent,
            String rows,
            String ai
    ) {

        if (rows == null || rows.isBlank()) {
            rows = """
        <tr>
        <td>لا توجد بلاغات</td>
        <td>غير محدد</td>
        <td>لا يوجد</td>
        </tr>
        """;
        }

        String logo = PdfReportSupport.logoBase64();
        String logoTag = logo.isEmpty() ? "" : "<img class=\"logo\" src=\"" + logo + "\" alt=\"logo\" />";

        return "<!DOCTYPE html><html lang=\"ar\" dir=\"rtl\"><head><meta charset=\"UTF-8\"/><style>"
                + PdfReportSupport.css() + "</style></head><body>"
                + "<div class=\"cover\">" + logoTag
                + "<div class=\"platform\">منصة منا وفينا</div><div class=\"bar\"></div>"
                + "<div class=\"rtitle\">التقرير الأسبوعي للبلاغات</div>"
                + "<div class=\"rsub\">عرض بلاغات سكان الحي خلال هذا الأسبوع</div>"
                + "<div class=\"rmeta\">تاريخ الإصدار: " + PdfReportSupport.esc(nowStamp()) + "</div></div>"
                + "<div class=\"page-break\"></div>"
                + "<div class=\"section\"><div class=\"section-title\">لوحة المؤشرات الأسبوعية</div>"
                + "<table class=\"cards\"><tr>"
                + "<td><div class=\"card\"><div class=\"num\">" + total + "</div><div class=\"lbl\">إجمالي البلاغات</div></div></td>"
                + "<td><div class=\"card\"><div class=\"num\">" + progress + "</div><div class=\"lbl\">قيد المعالجة</div></div></td>"
                + "<td><div class=\"card green\"><div class=\"num\">" + completed + "</div><div class=\"lbl\">تم حلها</div></div></td>"
                + "<td><div class=\"card urgent\"><div class=\"num\">" + urgent + "</div><div class=\"lbl\">عاجلة</div></div></td>"
                + "</tr></table></div>"
                + "<div class=\"section\"><div class=\"section-title\">تفاصيل البلاغات</div>"
                + "<table class=\"data\"><thead><tr><th>نوع البلاغ</th><th>الموقع</th><th>الحالة</th></tr></thead><tbody>"
                + rows + "</tbody></table></div>"
                + "<div class=\"page-break\"></div>"
                + "<div class=\"section\"><div class=\"section-title\">التحليل التنفيذي الذكي</div>"
                + "<div class=\"ai\">" + PdfReportSupport.aiHtml(ai) + "</div></div>"
                + "</body></html>";
    }
    private String performanceReportHtml(
            double overall,
            double lighting,
            double cleanliness,
            double parks,
            double infrastructure,
            double responseSpeed,
            double socialParticipation,
            String ai
    ) {

        String rating =
                overall >= 80 ? "ممتاز" :
                        overall >= 60 ? "جيد" :
                        overall >= 30 ? "يحتاج تحسين" :
                        "ضعيف";

        return metricReportHtml(
                "تقرير تقييم أداء الحي",
                "تحليل شامل لأداء الحي بناءً على البلاغات والخدمات ورضا السكان",
                "التقييم العام للحي",
                overall,
                rating,
                java.util.List.of(
                        new MetricRow("جودة الإنارة", lighting),
                        new MetricRow("مستوى النظافة", cleanliness),
                        new MetricRow("جودة الحدائق", parks),
                        new MetricRow("البنية التحتية", infrastructure),
                        new MetricRow("سرعة معالجة البلاغات", responseSpeed),
                        new MetricRow("المشاركة المجتمعية", socialParticipation)
                ),
                ai
        );
    }

    private String satisfactionReportHtml(
            double satisfaction,
            double cleanliness,
            double lighting,
            double parks,
            double events,
            double reports,
            double infrastructure,
            String ai
    ) {

        String status =
                satisfaction >= 80 ? "رضا مرتفع" :
                        satisfaction >= 60 ? "رضا متوسط" :
                        satisfaction >= 30 ? "يحتاج تحسين" :
                        "رضا منخفض";

        return metricReportHtml(
                "تقرير تحليل رضا السكان",
                "يعرض هذا التقرير نسبة رضا سكان الحي عن الخدمات الأساسية",
                "نسبة رضا السكان العامة",
                satisfaction,
                status,
                java.util.List.of(
                        new MetricRow("الرضا عن النظافة", cleanliness),
                        new MetricRow("الرضا عن الإنارة", lighting),
                        new MetricRow("الرضا عن الحدائق", parks),
                        new MetricRow("الرضا عن الفعاليات", events),
                        new MetricRow("الرضا عن معالجة البلاغات", reports),
                        new MetricRow("الرضا عن البنية التحتية", infrastructure)
                ),
                ai
        );
    }

    private double totalReportsRate(List<IssueReport> reports) {

        if (reports.isEmpty()) {
            return 0;
        }

        long completed = reports.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .count();

        return (completed * 100.0) / reports.size();
    }

    private String percentColor(double value) {

        if (value < 30) {
            return "#d32f2f"; // أحمر
        }

        if (value < 60) {
            return "#f9a825"; // أصفر
        }

        return "#2e7d32"; // أخضر
    }

    private String percentBg(double value) {

        if (value < 30) {
            return "#ffebee";
        }

        if (value < 60) {
            return "#fff8e1";
        }

        return "#e8f5e9";
    }

    public void resendMayorAppointmentEmail(Integer mayorId) {

        User mayor = userRepository.findUserById(mayorId);

        if (mayor == null) {
            throw new ApiException("Mayor not found");
        }

        MayorProfile profile =
                mayorProfileRepository.findTopByUserIdAndStatusOrderByStartDateDesc(
                        mayorId,
                        "ACTIVE"
                );

        if (profile == null) {
            throw new ApiException("Active mayor profile not found");
        }

        emailService.sendMayorAppointmentEmail(
                mayor,
                profile,
                0
        );
    }

}

