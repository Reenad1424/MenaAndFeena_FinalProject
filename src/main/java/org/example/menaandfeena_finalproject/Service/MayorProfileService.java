package org.example.menaandfeena_finalproject.Service;


import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.MayorProfileInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.*;
import org.example.menaandfeena_finalproject.Model.*;
import org.example.menaandfeena_finalproject.Repository.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MayorProfileService {
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

        mayorProfileRepository.save(mayorProfile);
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
                        "أنت محلل تقارير بلدية، اكتب ملخص إداري احترافي مختصر وواضح",
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
                    "أنت خبير تقييم أداء الأحياء",
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
                        "أنت محلل رضا السكان",
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

        return """
<!DOCTYPE html>
<html dir="rtl" lang="ar">
<head>
<meta charset="UTF-8" />

<style>
* {
    font-family: "Noto Naskh Arabic";
    box-sizing: border-box;
}

body {
    direction: rtl;
    text-align: right;
    background: #ffffff;
    color: #333333;
    font-size: 14px;
    padding: 25px;
}

.title {
    color: #2e7d32;
    font-size: 32px;
    font-weight: bold;
    text-align: center;
    margin-bottom: 10px;
}

.subtitle {
    color: #777777;
    font-size: 14px;
    margin-bottom: 25px;
    text-align:center;
}

.line {
    height: 1px;
    background: #e5dfcf;
    margin: 20px 0;
}

.stats {
    width: 100%%;
    border-spacing: 12px;
}

.stat-card {
    border: 1px solid #eadfcb;
    border-radius: 14px;
    padding: 18px;
    text-align: center;
    background: #fffdf8;
}

.stat-number {
    color: #2e7d32;
    font-size: 28px;
    font-weight: bold;
}

.stat-label {
    color: #555555;
    font-size: 13px;
}

.section-title {
    color: #2e7d32;
    font-size: 22px;
    font-weight: bold;
    margin: 20px 0 12px;
}

table {
    width: 100%%;
    border-collapse: collapse;
    margin-top: 10px;
}

th {
    background: #2e7d32;
    color: white;
    padding: 12px;
    font-size: 14px;
    text-align: center;
}

td {
    border-bottom: 1px solid #eadfcb;
    padding: 12px;
    text-align: center;
}

.ai-box {
    background: #eef8ef;
    border-right: 5px solid #2e7d32;
    border-radius: 14px;
    padding: 18px;
    line-height: 2;
    margin-top: 10px;
}
</style>
</head>

<body>

<div class="title">التقرير الأسبوعي للبلاغات</div>
<div class="subtitle">عرض بلاغات سكان الحي خلال هذا الأسبوع</div>

<div class="line"></div>

<table class="stats">
<tr>
<td class="stat-card">
<div class="stat-number">%d</div>
<div class="stat-label">إجمالي البلاغات</div>
</td>

<td class="stat-card">
<div class="stat-number">%d</div>
<div class="stat-label">بلاغات قيد المعالجة</div>
</td>

<td class="stat-card">
<div class="stat-number">%d</div>
<div class="stat-label">بلاغات تم حلها</div>
</td>

<td class="stat-card">
<div class="stat-number">%d</div>
<div class="stat-label">بلاغات عاجلة</div>
</td>
</tr>
</table>

<div class="section-title">تفاصيل البلاغات</div>

<table>
<tr>
<th>نوع البلاغ</th>
<th>الموقع</th>
<th>الحالة</th>
</tr>
%s
</table>

<div class="section-title">تحليل الذكاء الاصطناعي</div>

<div class="ai-box">
%s
</div>

</body>
</html>
"""
                .formatted(
                        total,
                        progress,
                        completed,
                        urgent,
                        rows,
                        ai
                );
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

        String overallColor = percentColor(overall);
        String overallBg = percentBg(overall);

        String lightingColor = percentColor(lighting);
        String cleanlinessColor = percentColor(cleanliness);
        String parksColor = percentColor(parks);
        String infrastructureColor = percentColor(infrastructure);
        String responseSpeedColor = percentColor(responseSpeed);
        String socialColor = percentColor(socialParticipation);

        return """
<!DOCTYPE html>
<html dir="rtl" lang="ar">
<head>
<meta charset="UTF-8" />

<style>
* {
    font-family: "Noto Naskh Arabic";
    box-sizing: border-box;
}

body {
    direction: rtl;
    text-align: right;
    background: #ffffff;
    color: #333333;
    font-size: 14px;
    padding: 25px;
}

.title {
    color: #2e7d32;
    font-size: 32px;
    font-weight: bold;
    text-align: center;
}

.subtitle {
    color: #777777;
    text-align: center;
    margin-bottom: 25px;
}

.line {
    height: 1px;
    background: #e5dfcf;
    margin: 20px 0;
}

.overall-card {
    border-radius: 18px;
    padding: 25px;
    text-align: center;
    margin-bottom: 25px;
}

.overall-title {
    color: #2e7d32;
    font-size: 22px;
    font-weight: bold;
}

.overall-number {
    font-size: 52px;
    font-weight: bold;
}

.badge {
    display: inline-block;
    background: white;
    border-radius: 12px;
    padding: 4px 16px;
    font-weight: bold;
}

.grid {
    width: 100%%;
    border-spacing: 14px;
}

.metric {
    border: 1px solid #eadfcb;
    border-radius: 14px;
    padding: 15px;
    background: #fffdf8;
}

.metric-title {
    color: #2e7d32;
    font-size: 16px;
    font-weight: bold;
}

.metric-value {
    font-size: 24px;
    font-weight: bold;
    margin: 8px 0;
}

.bar {
    width: 100%%;
    height: 9px;
    background: #e7e0d0;
    border-radius: 10px;
}

.fill {
    height: 9px;
    border-radius: 10px;
}

.ai-box {
    border: 1px solid #eadfcb;
    border-radius: 15px;
    padding: 18px;
    margin-top: 25px;
    line-height: 2;
}
</style>
</head>

<body>

<div class="title">تقرير تقييم أداء الحي</div>
<div class="subtitle">تحليل شامل لأداء الحي بناءً على البلاغات والخدمات ورضا السكان</div>

<div class="line"></div>

<div class="overall-card" style="background:%s;">
<div class="overall-title">التقييم العام للحي</div>
<div class="overall-number" style="color:%s;">%.0f%%</div>
<div class="badge" style="color:%s;">%s</div>
</div>

<table class="grid">
<tr>
<td class="metric">
<div class="metric-title">جودة الإنارة</div>
<div class="metric-value" style="color:%s;">%.0f%%</div>
<div class="bar"><div class="fill" style="width: %.0f%%; background:%s;"></div></div>
</td>

<td class="metric">
<div class="metric-title">مستوى النظافة</div>
<div class="metric-value" style="color:%s;">%.0f%%</div>
<div class="bar"><div class="fill" style="width: %.0f%%; background:%s;"></div></div>
</td>

<td class="metric">
<div class="metric-title">جودة الحدائق</div>
<div class="metric-value" style="color:%s;">%.0f%%</div>
<div class="bar"><div class="fill" style="width: %.0f%%; background:%s;"></div></div>
</td>
</tr>

<tr>
<td class="metric">
<div class="metric-title">البنية التحتية</div>
<div class="metric-value" style="color:%s;">%.0f%%</div>
<div class="bar"><div class="fill" style="width: %.0f%%; background:%s;"></div></div>
</td>

<td class="metric">
<div class="metric-title">سرعة معالجة البلاغات</div>
<div class="metric-value" style="color:%s;">%.0f%%</div>
<div class="bar"><div class="fill" style="width: %.0f%%; background:%s;"></div></div>
</td>

<td class="metric">
<div class="metric-title">المشاركة المجتمعية</div>
<div class="metric-value" style="color:%s;">%.0f%%</div>
<div class="bar"><div class="fill" style="width: %.0f%%; background:%s;"></div></div>
</td>
</tr>
</table>

<div class="ai-box">
<h2 style="color:#2e7d32;">ملخص الذكاء الاصطناعي</h2>
%s
</div>

</body>
</html>
"""
                .formatted(
                        overallBg,
                        overallColor,
                        overall,
                        overallColor,
                        rating,

                        lightingColor, lighting, lighting, lightingColor,
                        cleanlinessColor, cleanliness, cleanliness, cleanlinessColor,
                        parksColor, parks, parks, parksColor,

                        infrastructureColor, infrastructure, infrastructure, infrastructureColor,
                        responseSpeedColor, responseSpeed, responseSpeed, responseSpeedColor,
                        socialColor, socialParticipation, socialParticipation, socialColor,

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

        String overallColor = percentColor(satisfaction);
        String overallBg = percentBg(satisfaction);

        String cleanlinessColor = percentColor(cleanliness);
        String lightingColor = percentColor(lighting);
        String parksColor = percentColor(parks);
        String eventsColor = percentColor(events);
        String reportsColor = percentColor(reports);
        String infrastructureColor = percentColor(infrastructure);

        return """
<!DOCTYPE html>
<html dir="rtl" lang="ar">
<head>
<meta charset="UTF-8" />

<style>
* {
    font-family: "Noto Naskh Arabic";
    box-sizing: border-box;
}

body {
    direction: rtl;
    text-align: right;
    background: #ffffff;
    color: #333333;
    font-size: 14px;
    padding: 25px;
}

.title {
    color: #2e7d32;
    font-size: 32px;
    font-weight: bold;
    text-align: center;
}

.subtitle {
    color: #777777;
    text-align: center;
    margin-bottom: 25px;
}

.line {
    height: 1px;
    background: #e5dfcf;
    margin: 20px 0;
}

.overall-card {
    border-radius: 18px;
    padding: 25px;
    text-align: center;
    margin-bottom: 25px;
}

.overall-title {
    color: #2e7d32;
    font-size: 22px;
    font-weight: bold;
}

.overall-number {
    font-size: 52px;
    font-weight: bold;
}

.badge {
    display: inline-block;
    background: white;
    border-radius: 12px;
    padding: 4px 16px;
    font-weight: bold;
}

.grid {
    width: 100%%;
    border-spacing: 14px;
}

.metric {
    border: 1px solid #eadfcb;
    border-radius: 14px;
    padding: 15px;
    background: #fffdf8;
}

.metric-title {
    color: #2e7d32;
    font-size: 16px;
    font-weight: bold;
}

.metric-value {
    font-size: 24px;
    font-weight: bold;
    margin: 8px 0;
}

.bar {
    width: 100%%;
    height: 9px;
    background: #e7e0d0;
    border-radius: 10px;
}

.fill {
    height: 9px;
    border-radius: 10px;
}

.ai-box {
    border: 1px solid #eadfcb;
    border-radius: 15px;
    padding: 18px;
    margin-top: 25px;
    line-height: 2;
}
</style>
</head>

<body>

<div class="title">تقرير تحليل رضا السكان</div>
<div class="subtitle">يعرض هذا التقرير نسبة رضا سكان الحي عن الخدمات الأساسية</div>

<div class="line"></div>

<div class="overall-card" style="background:%s;">
<div class="overall-title">نسبة رضا السكان العامة</div>
<div class="overall-number" style="color:%s;">%.0f%%</div>
<div class="badge" style="color:%s;">%s</div>
</div>

<table class="grid">
<tr>
<td class="metric">
<div class="metric-title">الرضا عن النظافة</div>
<div class="metric-value" style="color:%s;">%.0f%%</div>
<div class="bar"><div class="fill" style="width: %.0f%%; background:%s;"></div></div>
</td>

<td class="metric">
<div class="metric-title">الرضا عن الإنارة</div>
<div class="metric-value" style="color:%s;">%.0f%%</div>
<div class="bar"><div class="fill" style="width: %.0f%%; background:%s;"></div></div>
</td>

<td class="metric">
<div class="metric-title">الرضا عن الحدائق</div>
<div class="metric-value" style="color:%s;">%.0f%%</div>
<div class="bar"><div class="fill" style="width: %.0f%%; background:%s;"></div></div>
</td>
</tr>

<tr>
<td class="metric">
<div class="metric-title">الرضا عن الفعاليات</div>
<div class="metric-value" style="color:%s;">%.0f%%</div>
<div class="bar"><div class="fill" style="width: %.0f%%; background:%s;"></div></div>
</td>

<td class="metric">
<div class="metric-title">الرضا عن معالجة البلاغات</div>
<div class="metric-value" style="color:%s;">%.0f%%</div>
<div class="bar"><div class="fill" style="width: %.0f%%; background:%s;"></div></div>
</td>

<td class="metric">
<div class="metric-title">الرضا عن البنية التحتية</div>
<div class="metric-value" style="color:%s;">%.0f%%</div>
<div class="bar"><div class="fill" style="width: %.0f%%; background:%s;"></div></div>
</td>
</tr>
</table>

<div class="ai-box">
<h2 style="color:#2e7d32;">ملخص الذكاء الاصطناعي</h2>
%s
</div>

</body>
</html>
"""
                .formatted(
                        overallBg,
                        overallColor,
                        satisfaction,
                        overallColor,
                        status,

                        cleanlinessColor, cleanliness, cleanliness, cleanlinessColor,
                        lightingColor, lighting, lighting, lightingColor,
                        parksColor, parks, parks, parksColor,

                        eventsColor, events, events, eventsColor,
                        reportsColor, reports, reports, reportsColor,
                        infrastructureColor, infrastructure, infrastructure, infrastructureColor,

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
    }private String percentColor(double value) {

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
