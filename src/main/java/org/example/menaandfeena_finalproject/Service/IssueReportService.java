package org.example.menaandfeena_finalproject.Service;

import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.IssueReportInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.IssueReportOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.IssueReportSummaryOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.IssueReportTimelineOutDTO;
import org.example.menaandfeena_finalproject.Model.IssueReport;
import org.example.menaandfeena_finalproject.Model.IssueReportTimeline;
import org.example.menaandfeena_finalproject.Model.MayorProfile;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.IssueReportRepository;
import org.example.menaandfeena_finalproject.Repository.IssueReportTimelineRepository;
import org.example.menaandfeena_finalproject.Repository.MayorProfileRepository;
import org.example.menaandfeena_finalproject.Repository.NeighborhoodRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IssueReportService {
    private static final DateTimeFormatter ISSUE_REPORT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

    private final IssueReportRepository issueReportRepository;
    private final UserRepository userRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final MayorProfileRepository mayorProfileRepository;
    private final OpenAIService openAIService;
    private final NominatimService nominatimService;
    private final EmailService emailService;
    private final IssueReportTimelineRepository issueReportTimelineRepository;

    // يقرأ مسار مجلد الرفع من application.properties حتى نغيره بسهولة حسب بيئة التشغيل.
    @Value("${app.upload.dir}")
    private String uploadDir;

    public List<IssueReportOutDTO> getAll() {
        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();

        for (IssueReport issueReport : issueReportRepository.findAll()) {
            issueReportOutDTOS.add(mapToOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }


    public IssueReportOutDTO createIssueReport(Integer reporterId, IssueReportInDTO issueReportInDTO) {
        User reporter = userRepository.findUserById(reporterId);

        if (reporter == null) {
            throw new ApiException("Reporter not found");
        }
        if (reporter.getNeighborhood() == null) {
            throw new ApiException("Reporter must belong to a neighborhood to create issue report");
        }

        IssueReport issueReport = new IssueReport();
        issueReport.setDescription(issueReportInDTO.getDescription());
        issueReport.setReportedStreetName(issueReportInDTO.getReportedStreetName());
        issueReport.setLatitude(issueReportInDTO.getLatitude());
        issueReport.setLongitude(issueReportInDTO.getLongitude());
        Map<String, String> detectedLocation = nominatimService.detectLocationFromCoordinates(issueReportInDTO.getLatitude(), issueReportInDTO.getLongitude());
        issueReport.setDetectedDistrictName(detectedLocation.getOrDefault("district", reporter.getNeighborhood().getName()));
        issueReport.setDetectedStreetName(detectedLocation.getOrDefault("street", "Unknown street"));

        String systemPrompt = """
                You classify municipal/community issue reports and generate a short Arabic title.
                Return only this format with no explanation:
                TITLE|CATEGORY|PRIORITY
                The title must be short and Arabic.
                Choose exactly one category from:
                LIGHTING, ROADS, CLEANLINESS, VISUAL_POLLUTION, PARKS, WATER_AND_SEWAGE, ANIMALS, SAFETY, OTHER.
                Choose exactly one priority from:
                URGENT, NON_URGENT, PERIODIC.
                Examples:
                Input: يوجد عمود إنارة لا يعمل منذ أسبوع
                Output: إنارة معطلة في الشارع|LIGHTING|URGENT
                Input: توجد أكياس نفايات متراكمة بجانب الحديقة
                Output: تراكم نفايات قرب الحديقة|CLEANLINESS|NON_URGENT
                """;

        String userContent = "Description: " + issueReportInDTO.getDescription();
        String aiResult = openAIService.askAI(systemPrompt, userContent);

        // Safe fallback values in case OpenAI is down or returns an unexpected format.
        String title = "بلاغ جديد";
        String category = "OTHER";
        String priority = "NON_URGENT";
        // نتتبّع إن نجح تصنيف الذكاء الاصطناعي فعلاً حتى نضيف خطوة AI_CLASSIFIED فقط عند النجاح.
        boolean aiClassified = false;

        if (aiResult != null) {
            // Expected AI response format: TITLE|CATEGORY|PRIORITY, for example إنارة معطلة في الشارع|LIGHTING|URGENT.
            String[] aiParts = aiResult.trim().split("\\|");
            if (aiParts.length == 3) {
                String aiTitle = aiParts[0].trim();
                String aiCategory = aiParts[1].trim().toUpperCase().replace(" ", "");
                String aiPriority = aiParts[2].trim().toUpperCase().replace(" ", "");
                if (aiCategory.matches("LIGHTING|ROADS|CLEANLINESS|VISUAL_POLLUTION|PARKS|WATER_AND_SEWAGE|ANIMALS|SAFETY|OTHER")
                        && aiPriority.matches("URGENT|NON_URGENT|PERIODIC")
                        && !aiTitle.isBlank()) {
                    title = aiTitle;
                    category = aiCategory;
                    priority = aiPriority;
                    aiClassified = true;
                }
            }
        }

        issueReport.setTitle(title);
        issueReport.setPriority(priority);
        issueReport.setCategory(category);
        issueReport.setStatus("OPEN");
        issueReport.setReporter(reporter);
        issueReport.setReportNeighborhood(reporter.getNeighborhood());

        IssueReport savedIssueReport = issueReportRepository.save(issueReport);

        // أول خطوة في سجل التتبّع: إنشاء البلاغ. ثم خطوة تصنيف الذكاء الاصطناعي (فقط عند نجاح التصنيف).
        LocalDateTime createdBase = LocalDateTime.now();
        addTimelineEntry(savedIssueReport, "REPORT_CREATED", "تم إنشاء البلاغ", "تم استلام البلاغ من المستخدم", createdBase);
        if (aiClassified) {
            // نزيد الوقت بمقدار ميكروثانية بسيط حتى تأتي خطوة التصنيف بعد خطوة الإنشاء في الترتيب.
            addTimelineEntry(savedIssueReport, "AI_CLASSIFIED", "تم تصنيفه بواسطة AI", "تم تصنيف البلاغ وتحديد الأولوية", createdBase.plusNanos(1_000_000));
        }

        return mapToOutDTO(savedIssueReport);
    }



    public void delete(Integer id) {
        IssueReport issueReport = issueReportRepository.findIssueReportById(id);
        if (issueReport == null) throw new ApiException("Issue report not found");
        issueReportRepository.delete(issueReport);
    }

    // Abdullah
    public IssueReportOutDTO getIssueReportById(Integer id) {
        IssueReport issueReport = issueReportRepository.findIssueReportById(id);

        if (issueReport == null) {
            throw new ApiException("Issue report not found");
        }

        return mapToOutDTO(issueReport);
    }

    public List<IssueReportOutDTO> getIssueReportsByStatus(String status) {
        if (!status.matches("OPEN|IN_PROGRESS|COMPLETED")) {
            throw new ApiException("Status must be OPEN, IN_PROGRESS or COMPLETED only");
        }

        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findIssueReportsByStatus(status)) {
            issueReportOutDTOS.add(mapToOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }

    public List<IssueReportOutDTO> getIssueReportsByPriority(String priority) {
        if (!priority.matches("URGENT|NON_URGENT|PERIODIC")) {
            throw new ApiException("Priority must be URGENT, NON_URGENT or PERIODIC only");
        }

        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findIssueReportsByPriority(priority)) {
            issueReportOutDTOS.add(mapToOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }

    public List<IssueReportOutDTO> getIssueReportsByCategory(String category) {
        if (!category.matches("LIGHTING|ROADS|CLEANLINESS|VISUAL_POLLUTION|PARKS|WATER_AND_SEWAGE|ANIMALS|SAFETY|OTHER")) {
            throw new ApiException("Category must be LIGHTING, ROADS, CLEANLINESS, VISUAL_POLLUTION, PARKS, WATER_AND_SEWAGE, ANIMALS, SAFETY or OTHER only");
        }

        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findIssueReportsByCategory(category)) {
            issueReportOutDTOS.add(mapToOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }

    public List<IssueReportOutDTO> getMyIssueReports(Integer userId) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findIssueReportsByReporterId(userId)) {
            issueReportOutDTOS.add(mapToOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }

    public List<IssueReportOutDTO> getIssueReportsByNeighborhood(Integer neighborhoodId) {
        Neighborhood neighborhood = neighborhoodRepository.findNeighborhoodById(neighborhoodId);

        if (neighborhood == null) {
            throw new ApiException("Neighborhood not found");
        }

        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findIssueReportsByReportNeighborhoodId(neighborhoodId)) {
            issueReportOutDTOS.add(mapToOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }

    public List<IssueReportOutDTO> searchIssueReports(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new ApiException("Keyword cannot be blank");
        }

        List<IssueReportOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.searchIssueReports(keyword)) {
            issueReportOutDTOS.add(mapToOutDTO(issueReport));
        }

        return issueReportOutDTOS;
    }

    public List<IssueReportSummaryOutDTO> getUserNeighborhoodReports(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        List<IssueReportSummaryOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findIssueReportsByReportNeighborhoodId(user.getNeighborhood().getId())) {
            issueReportOutDTOS.add(mapToSummaryOutDTO(issueReport));
        }
        return issueReportOutDTOS;
    }

    public List<IssueReportSummaryOutDTO> getUserMyReports(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        List<IssueReportSummaryOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findByReporterIdAndReportNeighborhood_Id(userId, user.getNeighborhood().getId())) {
            issueReportOutDTOS.add(mapToSummaryOutDTO(issueReport));
        }
        return issueReportOutDTOS;
    }

    public IssueReportOutDTO getUserIssueReportById(Integer userId, Integer reportId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        IssueReport issueReport = issueReportRepository.findIssueReportById(reportId);
        if (issueReport == null) {
            throw new ApiException("Issue report not found");
        }
        if (issueReport.getReportNeighborhood() == null
                || !issueReport.getReportNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Issue report is outside your neighborhood");
        }
        return mapToOutDTO(issueReport);
    }

    // رفع صورة البلاغ يحفظ الملف داخل مجلد uploads فقط، ولا نخزن bytes أو blob داخل MySQL.
    // الفرق هنا أن البلاغ يملك صورة واحدة فقط، لذلك نحدث imageUrl داخل نفس سجل IssueReport.
    public IssueReportOutDTO uploadIssueReportImage(Integer userId, Integer reportId, MultipartFile image) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        IssueReport issueReport = issueReportRepository.findIssueReportById(reportId);
        if (issueReport == null) {
            throw new ApiException("Issue report not found");
        }
        if (issueReport.getReportNeighborhood() == null
                || !issueReport.getReportNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Issue report is outside your neighborhood");
        }
        if (issueReport.getReporter() == null
                || !issueReport.getReporter().getId().equals(userId)) {
            throw new ApiException("Only the report owner can upload image");
        }
        // نتحقق من الملف قبل الحفظ: لا يكون فارغاً، لا يتجاوز 5MB، ونقبل فقط صيغ الصور المدعومة.
        if (image == null || image.isEmpty()) {
            throw new ApiException("Image file cannot be empty");
        }
        if (image.getSize() > 5 * 1024 * 1024) {
            throw new ApiException("Image file size must not exceed 5MB");
        }

        String contentType = image.getContentType();
        String extension;
        if ("image/jpeg".equals(contentType)) {
            extension = "jpg";
        } else if ("image/png".equals(contentType)) {
            extension = "png";
        } else if ("image/webp".equals(contentType)) {
            extension = "webp";
        } else {
            throw new ApiException("Image content type must be image/jpeg, image/png, or image/webp");
        }

        try {
            // يتم إنشاء اسم فريد للملف ثم نحفظ رابطه فقط في قاعدة البيانات مثل /uploads/issue-reports/...
            Path issueReportUploadDir = Paths.get(uploadDir, "issue-reports").toAbsolutePath().normalize();
            Files.createDirectories(issueReportUploadDir);
            String filename = "issue-report-" + reportId + "-" + UUID.randomUUID() + "." + extension;
            Path filePath = issueReportUploadDir.resolve(filename).normalize();
            Files.copy(image.getInputStream(), filePath);

            issueReport.setImageUrl("/uploads/issue-reports/" + filename);
            return mapToOutDTO(issueReportRepository.save(issueReport));
        } catch (IOException e) {
            throw new ApiException("Could not upload issue report image");
        }
    }

    public List<IssueReportSummaryOutDTO> getUserIssueReportsByStatus(Integer userId, String status) {
        if (!status.matches("OPEN|IN_PROGRESS|COMPLETED")) {
            throw new ApiException("Status must be OPEN, IN_PROGRESS or COMPLETED only");
        }
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        List<IssueReportSummaryOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findByReportNeighborhood_IdAndStatus(user.getNeighborhood().getId(), status)) {
            issueReportOutDTOS.add(mapToSummaryOutDTO(issueReport));
        }
        return issueReportOutDTOS;
    }

    public List<IssueReportSummaryOutDTO> getUserIssueReportsByPriority(Integer userId, String priority) {
        if (!priority.matches("URGENT|NON_URGENT|PERIODIC")) {
            throw new ApiException("Priority must be URGENT, NON_URGENT or PERIODIC only");
        }
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        List<IssueReportSummaryOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findByReportNeighborhood_IdAndPriority(user.getNeighborhood().getId(), priority)) {
            issueReportOutDTOS.add(mapToSummaryOutDTO(issueReport));
        }
        return issueReportOutDTOS;
    }

    public List<IssueReportSummaryOutDTO> getUserIssueReportsByCategory(Integer userId, String category) {
        if (!category.matches("LIGHTING|ROADS|CLEANLINESS|VISUAL_POLLUTION|PARKS|WATER_AND_SEWAGE|ANIMALS|SAFETY|OTHER")) {
            throw new ApiException("Category must be LIGHTING, ROADS, CLEANLINESS, VISUAL_POLLUTION, PARKS, WATER_AND_SEWAGE, ANIMALS, SAFETY or OTHER only");
        }
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        List<IssueReportSummaryOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.findByReportNeighborhood_IdAndCategory(user.getNeighborhood().getId(), category)) {
            issueReportOutDTOS.add(mapToSummaryOutDTO(issueReport));
        }
        return issueReportOutDTOS;
    }

    public List<IssueReportSummaryOutDTO> searchUserIssueReports(Integer userId, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new ApiException("Keyword cannot be blank");
        }
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        List<IssueReportSummaryOutDTO> issueReportOutDTOS = new ArrayList<>();
        for (IssueReport issueReport : issueReportRepository.searchIssueReportsByNeighborhood(user.getNeighborhood().getId(), keyword)) {
            issueReportOutDTOS.add(mapToSummaryOutDTO(issueReport));
        }
        return issueReportOutDTOS;
    }

    public void startProgress(Integer reportId, Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }
        if (user.getStatus() == null || !"MAYOR".equalsIgnoreCase(user.getStatus())) {
            throw new ApiException("Only mayor can update issue report status");
        }
        MayorProfile mayorProfile = mayorProfileRepository.findTopByUserIdAndStatusOrderByStartDateDesc(userId, "ACTIVE");
        if (mayorProfile == null) {
            throw new ApiException("Active mayor profile not found");
        }
        if (mayorProfile.getNeighborhood() == null
                || !mayorProfile.getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Mayor profile must belong to the user's neighborhood");
        }

        IssueReport report = issueReportRepository.findIssueReportById(reportId);

        if (report == null) {
            throw new ApiException("Issue report not found");
        }
        if (report.getReportNeighborhood() == null
                || !report.getReportNeighborhood().getId().equals(mayorProfile.getNeighborhood().getId())) {
            throw new ApiException("Issue report is outside your neighborhood");
        }

        if (report.getStatus().equals("COMPLETED")) {
            throw new ApiException("Completed reports cannot be moved to in progress");
        }

        if (!report.getStatus().equals("OPEN")) {
            throw new ApiException("Only open reports can be moved to in progress");
        }

        report.setStatus("IN_PROGRESS");
        issueReportRepository.save(report);

        // خطوة التتبّع: تحوّل البلاغ إلى قيد المعالجة.
        addTimelineEntry(report, "IN_PROGRESS", "قيد المعالجة", "تم تحويل البلاغ للفريق المختص وجاري العمل على الحل", LocalDateTime.now());
    }

    public void completeReport(Integer reportId, Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }
        if (user.getStatus() == null || !"MAYOR".equalsIgnoreCase(user.getStatus())) {
            throw new ApiException("Only mayor can update issue report status");
        }
        MayorProfile mayorProfile = mayorProfileRepository.findTopByUserIdAndStatusOrderByStartDateDesc(userId, "ACTIVE");
        if (mayorProfile == null) {
            throw new ApiException("Active mayor profile not found");
        }
        if (mayorProfile.getNeighborhood() == null
                || !mayorProfile.getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Mayor profile must belong to the user's neighborhood");
        }

        IssueReport report = issueReportRepository.findIssueReportById(reportId);

        if (report == null) {
            throw new ApiException("Issue report not found");
        }
        if (report.getReportNeighborhood() == null
                || !report.getReportNeighborhood().getId().equals(mayorProfile.getNeighborhood().getId())) {
            throw new ApiException("Issue report is outside your neighborhood");
        }

        if (report.getStatus().equals("COMPLETED")) {
            throw new ApiException("Issue report is already completed");
        }

        if (!report.getStatus().equals("IN_PROGRESS")) {
            throw new ApiException("Only in progress reports can be completed");
        }

        report.setStatus("COMPLETED");
        issueReportRepository.save(report);

        // خطوة التتبّع: تم حل البلاغ وإغلاقه.
        addTimelineEntry(report, "COMPLETED", "تم الحل", "تم إغلاق البلاغ بعد حل المشكلة", LocalDateTime.now());
    }

    // يولد تقرير PDF للعمدة بشكل مباشر من بيانات البلاغات الحالية، لذلك لا نحتاج Entity أو جدول جديد.
    // التقرير لا يُخزن في قاعدة البيانات لأنه قابل للتغير مع كل بلاغ جديد، فيتم بناؤه لحظة الطلب فقط.
    public byte[] generateMayorIssueReportPdf(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }
        if (user.getStatus() == null || !"MAYOR".equalsIgnoreCase(user.getStatus())) {
            throw new ApiException("Only mayor can generate issue report PDF");
        }
        MayorProfile mayorProfile = mayorProfileRepository.findTopByUserIdAndStatusOrderByStartDateDesc(userId, "ACTIVE");
        if (mayorProfile == null) {
            throw new ApiException("Active mayor profile not found");
        }

        if (mayorProfile.getNeighborhood() == null
                || !mayorProfile.getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Mayor profile must belong to the user's neighborhood");
        }

        Neighborhood neighborhood = mayorProfile.getNeighborhood();
        List<IssueReport> reports = issueReportRepository.findIssueReportsByReportNeighborhoodId(neighborhood.getId());

        // نحسب الإحصائيات من بلاغات نفس الحي فقط: الحالات، الأولويات، أكثر تصنيف، وأكثر شارع/حي فرعي تكراراً.
        int totalReports = reports.size();
        int openReports = 0;
        int inProgressReports = 0;
        int completedReports = 0;
        int urgentReports = 0;
        int nonUrgentReports = 0;
        int periodicReports = 0;
        Map<String, Integer> categoryCounts = new HashMap<>();
        Map<String, Integer> streetCounts = new HashMap<>();
        Map<String, Integer> districtCounts = new HashMap<>();

        for (IssueReport report : reports) {
            if ("OPEN".equals(report.getStatus())) {
                openReports++;
            } else if ("IN_PROGRESS".equals(report.getStatus())) {
                inProgressReports++;
            } else if ("COMPLETED".equals(report.getStatus())) {
                completedReports++;
            }

            if ("URGENT".equals(report.getPriority())) {
                urgentReports++;
            } else if ("NON_URGENT".equals(report.getPriority())) {
                nonUrgentReports++;
            } else if ("PERIODIC".equals(report.getPriority())) {
                periodicReports++;
            }

            if (report.getCategory() != null && !report.getCategory().isBlank()) {
                categoryCounts.put(report.getCategory(), categoryCounts.getOrDefault(report.getCategory(), 0) + 1);
            }
            if (report.getDetectedStreetName() != null && !report.getDetectedStreetName().isBlank()) {
                streetCounts.put(report.getDetectedStreetName(), streetCounts.getOrDefault(report.getDetectedStreetName(), 0) + 1);
            }
            if (report.getDetectedDistrictName() != null && !report.getDetectedDistrictName().isBlank()) {
                districtCounts.put(report.getDetectedDistrictName(), districtCounts.getOrDefault(report.getDetectedDistrictName(), 0) + 1);
            }
        }

        String mostCommonCategory = mostCommonValue(categoryCounts);
        String mostAffectedStreet = mostCommonValue(streetCounts);
        String mostAffectedDistrict = mostCommonValue(districtCounts);

        // نرسل للذكاء الاصطناعي ملخصاً رقمياً واتجاهات البلاغات فقط.
        // الذكاء الاصطناعي لا يغير البلاغات ولا يحفظ أي شيء؛ دوره فقط كتابة تحليل عربي مختصر للتقرير.
        String systemPrompt = """
                أنت مساعد ذكي لعمدة الحي. حلل إحصائيات بلاغات الحي واكتب تقريراً عربياً واضحاً.
                يجب أن يحتوي التحليل على:
                1. ملخص تنفيذي
                2. أكبر مشكلة تؤثر على الحي
                3. أكثر منطقة متأثرة
                4. توصيات عاجلة
                5. اقتراحات تحسين
                اكتب باللغة العربية فقط، بطول تقريبي بين 150 و250 كلمة.
                لا تضف JSON ولا جداول، فقط نص تحليلي منظم.
                اكتب عنوان كل قسم في سطر مستقل ينتهي بنقطتين، ثم فقرته تحته.
                """ + PdfReportSupport.AI_PLAIN_ARABIC;

        String userContent = """
                اسم الحي: %s
                إجمالي البلاغات: %d
                المفتوحة: %d
                قيد المعالجة: %d
                المكتملة: %d
                العاجلة: %d
                غير العاجلة: %d
                الدورية: %d
                أكثر تصنيف متكرر: %s
                أكثر شارع متضرر: %s
                أكثر حي فرعي متضرر: %s
                عدد البلاغات حسب التصنيف: %s
                عدد البلاغات حسب الشارع: %s
                عدد البلاغات حسب الحي الفرعي: %s
                """.formatted(
                neighborhood.getName(),
                totalReports,
                openReports,
                inProgressReports,
                completedReports,
                urgentReports,
                nonUrgentReports,
                periodicReports,
                mostCommonCategory,
                mostAffectedStreet,
                mostAffectedDistrict,
                categoryCounts,
                streetCounts,
                districtCounts
        );

        String aiAnalysis = openAIService.askAI(systemPrompt, userContent);
        if (aiAnalysis == null || aiAnalysis.isBlank() || "ERROR_FALLBACK".equals(aiAnalysis)) {
            // إذا فشل الذكاء الاصطناعي أو رجع رد غير صالح، نبني ملخصاً عربياً بسيطاً من نفس الإحصائيات.
            aiAnalysis = buildFallbackMayorAnalysis(neighborhood.getName(), totalReports, openReports, inProgressReports, completedReports, urgentReports, mostCommonCategory, mostAffectedStreet, mostAffectedDistrict);
        }

        byte[] pdfBytes;
        try {
            // OpenHTMLtoPDF يحول HTML إلى PDF. نستخدم دعم RTL وملف الخط العربي حتى تظهر العربية باتجاهها الصحيح.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.useUnicodeBidiSplitter(new ICUBidiSplitter.ICUBidiSplitterFactory());
            builder.useUnicodeBidiReorderer(new ICUBidiReorderer());
            builder.defaultTextDirection(PdfRendererBuilder.TextDirection.RTL);
            builder.useFont(() -> {
                try {
                    return new ClassPathResource("fonts/NotoNaskhArabic-Regular.ttf").getInputStream();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, "Noto Naskh Arabic");
            builder.withHtmlContent(buildMayorReportHtml(neighborhood.getName(), totalReports, openReports, inProgressReports, completedReports, urgentReports, nonUrgentReports, periodicReports, mostCommonCategory, mostAffectedStreet, mostAffectedDistrict, aiAnalysis, reports), null);
            builder.toStream(outputStream);
            builder.run();
            pdfBytes = outputStream.toByteArray();
        } catch (Exception e) {
            throw new ApiException("Could not generate mayor issue report PDF");
        }

        return pdfBytes;
    }

    // هذا الميثود مخصص للإرسال بالبريد فقط، ويستخدم نفس ميثود توليد التقرير حتى يكون ملف التحميل وملف الإيميل بنفس المحتوى.
    public void sendMayorIssueReportPdfEmail(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ApiException("Mayor email is required");
        }

        byte[] pdfBytes = generateMayorIssueReportPdf(userId);
        emailService.sendEmailWithAttachment(
                user.getEmail(),
                "تقرير بلاغات الحي الذكي",
                "مرفق تقرير بلاغات الحي الذكي الخاص بحيك.",
                pdfBytes,
                "Neighborhood-Issue-Report.pdf"
        );
    }

    private String mostCommonValue(Map<String, Integer> values) {
        String result = "لا يوجد";
        int max = 0;
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            if (entry.getValue() > max) {
                result = entry.getKey();
                max = entry.getValue();
            }
        }
        return result;
    }

    private String buildFallbackMayorAnalysis(String neighborhoodName, int totalReports, int openReports, int inProgressReports, int completedReports, int urgentReports, String mostCommonCategory, String mostAffectedStreet, String mostAffectedDistrict) {
        return "يوضح التقرير أن حي " + neighborhoodName + " لديه " + totalReports + " بلاغاً مسجلاً، منها "
                + openReports + " بلاغاً مفتوحاً و" + inProgressReports + " بلاغاً قيد المعالجة و"
                + completedReports + " بلاغاً مكتملًا. أكثر تصنيف متكرر هو " + mostCommonCategory
                + "، وأكثر شارع متضرر هو " + mostAffectedStreet + "، وأكثر حي فرعي متأثر هو "
                + mostAffectedDistrict + ". عدد البلاغات العاجلة هو " + urgentReports
                + "، لذلك يوصى بترتيبها أولاً ومتابعة المواقع الأكثر تكراراً بشكل مباشر. كما يفضل رفع وتيرة المتابعة الميدانية، توزيع فرق الصيانة حسب كثافة البلاغات، ومراجعة البلاغات المفتوحة يومياً حتى لا تتأخر المعالجة. هذا الملخص تم توليده تلقائياً لأن خدمة الذكاء الاصطناعي لم تُرجع تحليلاً صالحاً.";
    }

    private String statusAr(String status) {
        if ("OPEN".equals(status)) return "مفتوح";
        if ("IN_PROGRESS".equals(status)) return "قيد المعالجة";
        if ("COMPLETED".equals(status)) return "مكتمل";
        return status;
    }

    private String priorityAr(String priority) {
        if ("URGENT".equals(priority)) return "عاجلة";
        if ("NON_URGENT".equals(priority)) return "غير عاجلة";
        if ("PERIODIC".equals(priority)) return "دورية";
        return priority;
    }

    private String buildMayorReportHtml(String neighborhoodName, int totalReports, int openReports, int inProgressReports, int completedReports, int urgentReports, int nonUrgentReports, int periodicReports, String mostCommonCategory, String mostAffectedStreet, String mostAffectedDistrict, String aiAnalysis, List<IssueReport> reports) {
        String generatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
        String logo = PdfReportSupport.logoBase64();
        String logoTag = logo.isEmpty() ? "" : ("<img class=\"logo\" src=\"" + logo + "\" alt=\"logo\" />");

        // صفوف جدول البلاغات التفصيلي — يستمر عبر الصفحات دون قطع الصف الواحد.
        StringBuilder rows = new StringBuilder();
        if (reports == null || reports.isEmpty()) {
            rows.append("<tr><td colspan=\"7\" style=\"text-align:center;color:#9ca3af;\">لا توجد بلاغات مسجلة</td></tr>");
        } else {
            for (IssueReport r : reports) {
                String created = r.getCreatedAt() == null ? "—" : r.getCreatedAt().format(ISSUE_REPORT_DATE_FORMAT);
                rows.append("<tr>")
                        .append("<td>").append(PdfReportSupport.esc(r.getId())).append("</td>")
                        .append("<td>").append(PdfReportSupport.esc(r.getTitle())).append("</td>")
                        .append("<td>").append(PdfReportSupport.esc(r.getCategory())).append("</td>")
                        .append("<td>").append(PdfReportSupport.esc(priorityAr(r.getPriority()))).append("</td>")
                        .append("<td>").append(PdfReportSupport.esc(statusAr(r.getStatus()))).append("</td>")
                        .append("<td>").append(PdfReportSupport.esc(r.getDetectedStreetName())).append("</td>")
                        .append("<td>").append(PdfReportSupport.esc(created)).append("</td>")
                        .append("</tr>");
            }
        }

        return """
<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
<meta charset="UTF-8" />
<style>%s</style>
</head>
<body>

<div class="cover">
    %s
    <div class="platform">منصة منا وفينا</div>
    <div class="bar"></div>
    <div class="rtitle">تقرير بلاغات الحي</div>
    <div class="rsub">تقرير رسمي لإدارة ومتابعة بلاغات الحي</div>
    <div class="rmeta">الحي: %s<br/>تاريخ الإصدار: %s</div>
</div>

<div class="page-break"></div>

<div class="section">
    <div class="section-title">لوحة المؤشرات التنفيذية</div>
    <table class="cards">
        <tr>
            <td><div class="card"><div class="num">%d</div><div class="lbl">إجمالي البلاغات</div></div></td>
            <td><div class="card amber"><div class="num">%d</div><div class="lbl">مفتوحة</div></div></td>
            <td><div class="card"><div class="num">%d</div><div class="lbl">قيد المعالجة</div></div></td>
        </tr>
        <tr>
            <td><div class="card green"><div class="num">%d</div><div class="lbl">مكتملة</div></div></td>
            <td><div class="card urgent"><div class="num">%d</div><div class="lbl">عاجلة</div></div></td>
            <td></td>
        </tr>
    </table>
</div>

<div class="section">
    <div class="section-title">تحليل الأولويات والمناطق الأكثر تأثراً</div>
    <table class="facts">
        <tr><th>البلاغات العاجلة</th><td>%d</td></tr>
        <tr><th>البلاغات غير العاجلة</th><td>%d</td></tr>
        <tr><th>البلاغات الدورية</th><td>%d</td></tr>
        <tr><th>أكثر تصنيف متكرر</th><td>%s</td></tr>
        <tr><th>أكثر شارع متضرر</th><td>%s</td></tr>
        <tr><th>أكثر حي فرعي متضرر</th><td>%s</td></tr>
    </table>
</div>

<div class="page-break"></div>

<div class="section">
    <div class="section-title">التحليل التنفيذي الذكي</div>
    <div class="ai">%s</div>
</div>

<div class="page-break"></div>

<div class="section">
    <div class="section-title">سجل البلاغات التفصيلي</div>
    <table class="data">
        <thead>
            <tr>
                <th>رقم البلاغ</th>
                <th>العنوان</th>
                <th>التصنيف</th>
                <th>الأولوية</th>
                <th>الحالة</th>
                <th>الشارع</th>
                <th>تاريخ الإنشاء</th>
            </tr>
        </thead>
        <tbody>%s</tbody>
    </table>
</div>

</body>
</html>
""".formatted(
                PdfReportSupport.css(),
                logoTag,
                PdfReportSupport.esc(neighborhoodName),
                PdfReportSupport.esc(generatedAt),
                totalReports,
                openReports,
                inProgressReports,
                completedReports,
                urgentReports,
                urgentReports,
                nonUrgentReports,
                periodicReports,
                PdfReportSupport.esc(mostCommonCategory),
                PdfReportSupport.esc(mostAffectedStreet),
                PdfReportSupport.esc(mostAffectedDistrict),
                PdfReportSupport.aiHtml(aiAnalysis),
                rows.toString()
        );
    }

    private IssueReportOutDTO mapToOutDTO(IssueReport issueReport) {
        Integer reporterId = issueReport.getReporter() == null ? null : issueReport.getReporter().getId();
        String reporterName = issueReport.getReporter() == null ? null : issueReport.getReporter().getFullName();
        Integer reportNeighborhoodId = issueReport.getReportNeighborhood() == null ? null : issueReport.getReportNeighborhood().getId();
        String reportNeighborhoodName = issueReport.getReportNeighborhood() == null ? null : issueReport.getReportNeighborhood().getName();
        String createdAt = issueReport.getCreatedAt() == null ? null : issueReport.getCreatedAt().format(ISSUE_REPORT_DATE_FORMAT);
        return new IssueReportOutDTO(issueReport.getId(), issueReport.getTitle(), issueReport.getDescription(), issueReport.getCategory(), issueReport.getPriority(), issueReport.getStatus(), issueReport.getLatitude(), issueReport.getLongitude(), createdAt, issueReport.getReportedStreetName(), issueReport.getDetectedDistrictName(), issueReport.getDetectedStreetName(), issueReport.getImageUrl(), reporterId, reporterName, reportNeighborhoodId, reportNeighborhoodName);
    }

    private IssueReportSummaryOutDTO mapToSummaryOutDTO(IssueReport issueReport) {
        String createdAt = issueReport.getCreatedAt() == null ? null : issueReport.getCreatedAt().format(ISSUE_REPORT_DATE_FORMAT);
        return new IssueReportSummaryOutDTO(issueReport.getId(), issueReport.getTitle(), issueReport.getCategory(), issueReport.getPriority(), issueReport.getStatus(), createdAt, issueReport.getImageUrl());
    }

    private String html(Object value) {
        if (value == null) {
            return "لا يوجد";
        }
        return HtmlUtils.htmlEscape(value.toString(), StandardCharsets.UTF_8.name());
    }

    // =========================================================================
    // سجل تتبّع البلاغ (Issue Report Timeline / Tracking)
    //
    // - IssueReport.status يخزّن الحالة الحالية للبلاغ (OPEN / IN_PROGRESS / COMPLETED).
    // - IssueReportTimeline يخزّن خطوات التتبّع التاريخية (متى أُنشئ، متى صُنّف، متى عُولج، متى حُلّ).
    // - الفرونت إند يستخدم endpoint الـ timeline لرسم شريط تقدّم البلاغ (Progress Tracker).
    // - الفكرة مشابهة لتتبّع الشحنات (Shipment Tracking).
    // =========================================================================

    // نضيف خطوة جديدة في سجل التتبّع مع تجنّب تكرار نفس المرحلة لنفس البلاغ.
    private void addTimelineEntry(IssueReport report, String stage, String title, String description, LocalDateTime createdAt) {
        if (report == null || report.getId() == null) {
            return;
        }
        // لا نكرّر نفس المرحلة لنفس البلاغ (حسب متطلبات منع التكرار).
        if (issueReportTimelineRepository.existsByIssueReport_IdAndStage(report.getId(), stage)) {
            return;
        }
        IssueReportTimeline entry = new IssueReportTimeline();
        entry.setStage(stage);
        entry.setTitle(title);
        entry.setDescription(description);
        entry.setCreatedAt(createdAt);
        entry.setIssueReport(report);
        issueReportTimelineRepository.save(entry);
    }

    // إرجاع كل خطوات تتبّع البلاغ مرتبة تصاعدياً حسب وقت الإنشاء.
    // كل خطوة مخزّنة تمثّل مرحلة حدثت فعلاً، لذلك completed = true لكل الخطوات المعادة.
    public List<IssueReportTimelineOutDTO> getReportTimeline(Integer issueReportId) {
        IssueReport report = issueReportRepository.findIssueReportById(issueReportId);
        if (report == null) {
            throw new ApiException("Issue report not found");
        }
        List<IssueReportTimelineOutDTO> out = new ArrayList<>();
        for (IssueReportTimeline entry : issueReportTimelineRepository.findByIssueReport_IdOrderByCreatedAtAsc(issueReportId)) {
            out.add(new IssueReportTimelineOutDTO(
                    entry.getId(),
                    entry.getStage(),
                    entry.getTitle(),
                    entry.getDescription(),
                    entry.getCreatedAt(),
                    true
            ));
        }
        return out;
    }

}
