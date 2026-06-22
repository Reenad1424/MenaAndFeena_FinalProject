package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.EventInDTO;
import org.example.menaandfeena_finalproject.DTO.In.EventScheduleInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.EventFeatureOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.EventRecommendationOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.EventScheduleOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.WeekendPlanItemDTO;
import org.example.menaandfeena_finalproject.DTO.Out.WeekendPlanOutDTO;
import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Model.EventFeature;
import org.example.menaandfeena_finalproject.Model.EventSchedule;
import org.example.menaandfeena_finalproject.Model.FamilyMember;
import org.example.menaandfeena_finalproject.Model.Initiative;
import org.example.menaandfeena_finalproject.Repository.EventFeatureRepository;
import org.example.menaandfeena_finalproject.Repository.EventRepository;
import org.example.menaandfeena_finalproject.Repository.EventScheduleRepository;
import org.example.menaandfeena_finalproject.Repository.FamilyMemberRepository;
import org.example.menaandfeena_finalproject.Repository.InitiativeRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.example.menaandfeena_finalproject.Model.User;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final InitiativeRepository initiativeRepository;
    private final OpenAIService openAIService;
    private final EventFeatureRepository eventFeatureRepository;
    private final EventScheduleRepository eventScheduleRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // صيغة الوقت المعتمدة لفقرات البرنامج (24 ساعة).
    private static final DateTimeFormatter SCHEDULE_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }


    public void addEvent(EventInDTO eventInDTO) {
        Event event = new Event();
        event.setTitle(eventInDTO.getTitle());
        event.setDescription(eventInDTO.getDescription());
        event.setDate(eventInDTO.getDate());
        event.setLocation(eventInDTO.getLocation());
        event.setIsPaid(eventInDTO.getIsPaid());
        event.setPrice(eventInDTO.getPrice());
        event.setMaxParticipants(eventInDTO.getMaxParticipants());
        event.setStatus("ACTIVE");

        if (Boolean.FALSE.equals(eventInDTO.getIsPaid())) {
            event.setPrice(null);
        }

        if (Boolean.TRUE.equals(eventInDTO.getIsPaid()) && (eventInDTO.getPrice() == null || eventInDTO.getPrice() <= 0)) {
            throw new ApiException("Paid event must have price greater than 0");
        }

        applyEndTimeAndFeatures(event, eventInDTO, true);
        Event savedEvent = eventRepository.save(event);
        replaceSchedule(savedEvent, eventInDTO);
    }


    public void updateEvent(Integer id, EventInDTO eventInDTO) {
        Event oldEvent = eventRepository.findEventById(id);

      if(oldEvent == null){
          throw new ApiException("Event not found");
      }
        oldEvent.setTitle(eventInDTO.getTitle());
        oldEvent.setDescription(eventInDTO.getDescription());
        oldEvent.setDate(eventInDTO.getDate());
        oldEvent.setLocation(eventInDTO.getLocation());
        oldEvent.setIsPaid(eventInDTO.getIsPaid());
        oldEvent.setPrice(eventInDTO.getPrice());
        oldEvent.setMaxParticipants(eventInDTO.getMaxParticipants());

        if (Boolean.FALSE.equals(eventInDTO.getIsPaid())) {
            oldEvent.setPrice(null);
        }

        if (Boolean.TRUE.equals(eventInDTO.getIsPaid()) && (eventInDTO.getPrice() == null || eventInDTO.getPrice() <= 0)) {
            throw new ApiException("Paid event must have price greater than 0");
        }

        applyEndTimeAndFeatures(oldEvent, eventInDTO, false);
        Event savedEvent = eventRepository.save(oldEvent);
        replaceSchedule(savedEvent, eventInDTO);

    }


    public void deleteEvent(Integer id) {
        Event event = eventRepository.findEventById(id);

        if (event == null) {
            throw new ApiException("Event not found");
        }

        eventRepository.delete(event);
    }

// Walaa
    public List<Event> getUpcomingEvents() {
        return eventRepository.findEventsByDateAfter(LocalDateTime.now());
    }




  // Walaa
  public List<Event> getPreviousEvents() {
      return eventRepository.findEventsByDateBefore(LocalDateTime.now());
  }


  // Walaa
  public List<Event> getEventsByDate(LocalDate date) {
      LocalDateTime startOfDay = date.atStartOfDay();
      LocalDateTime endOfDay = date.atTime(23, 59, 59);
      return eventRepository.findEventsByDateBetween(startOfDay, endOfDay);

  }

    // Walaa
    public Event getEventById(Integer id) {
        Event event = eventRepository.findEventById(id);
        if (event == null) {
            throw new ApiException("Event not found");
        }
        return event;
    }




    // Walaa

    public void createEvent(Integer userId, EventInDTO eventInDTO) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        Event event = new Event();
        event.setTitle(eventInDTO.getTitle());
        event.setDescription(eventInDTO.getDescription());
        event.setDate(eventInDTO.getDate());
        event.setLocation(eventInDTO.getLocation());
        event.setIsPaid(eventInDTO.getIsPaid());
        event.setPrice(eventInDTO.getPrice());
        event.setMaxParticipants(eventInDTO.getMaxParticipants());

        if (Boolean.FALSE.equals(eventInDTO.getIsPaid())) {
            event.setPrice(null);
        }

        if (Boolean.TRUE.equals(eventInDTO.getIsPaid()) && (eventInDTO.getPrice() == null || eventInDTO.getPrice() <= 0)) {
            throw new ApiException("Paid event must have price greater than 0");
        }

        event.setUser(user);
        // المنشئ والحي يؤخذان من المستخدم حتى لا يظهرا null في الرد.
        event.setCreator(user);
        event.setNeighborhood(user.getNeighborhood());
        event.setStatus("ACTIVE");

        applyEndTimeAndFeatures(event, eventInDTO, true);
        Event savedEvent = eventRepository.save(event);
        replaceSchedule(savedEvent, eventInDTO);
    }

    // Uploads a single cover image for an event. The file is stored on disk under
    // uploads/events/ and only its URL is saved on the Event record (same pattern as IssueReport).
    public Event uploadEventImage(Integer userId, Integer eventId, MultipartFile image) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }

        Event event = eventRepository.findEventById(eventId);
        if (event == null) {
            throw new ApiException("Event not found");
        }
        if (event.getUser() == null || !event.getUser().getId().equals(userId)) {
            throw new ApiException("Only the event owner can upload an image");
        }

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
            Path eventUploadDir = Paths.get(uploadDir, "events").toAbsolutePath().normalize();
            Files.createDirectories(eventUploadDir);
            String filename = "event-" + eventId + "-" + UUID.randomUUID() + "." + extension;
            Path filePath = eventUploadDir.resolve(filename).normalize();
            Files.copy(image.getInputStream(), filePath);

            event.setImageUrl("/uploads/events/" + filename);
            return eventRepository.save(event);
        } catch (IOException e) {
            throw new ApiException("Could not upload event image");
        }
    }

    // =========================================================================
    // الميزات والبرنامج (Event Features & Schedule)
    //
    // - الميزات معرّفة مسبقاً في قاعدة البيانات، والمنظّم فقط يختار منها (اختيار متعدد).
    // - الأيقونات الخاصة بكل ميزة يتعامل معها الفرونت إند، الباك إند يخزّن الاسم فقط.
    // - برنامج الفعالية (Schedule) يُدخل يدوياً من المنظّم.
    // - الباك إند لا يستخدم أي ذكاء اصطناعي هنا؛ فقط يتحقق من البيانات ويخزّنها ويعيدها منظّمة.
    // =========================================================================

    // نضبط وقت النهاية (اختياري) والميزات المختارة على الفعالية قبل حفظها.
    // allowAiAutofill = true عند الإنشاء فقط: إن لم يرسل المنظّم أي ميزات، يختار الذكاء الاصطناعي حتى 4 ميزات.
    private void applyEndTimeAndFeatures(Event event, EventInDTO dto, boolean allowAiAutofill) {
        // وقت النهاية اختياري؛ إن أُرسل يجب أن يكون بعد وقت البداية.
        if (dto.getEndTime() != null) {
            if (event.getDate() != null && !dto.getEndTime().isAfter(event.getDate())) {
                throw new ApiException("Event end time must be after start time");
            }
            event.setEndTime(dto.getEndTime());
        } else {
            event.setEndTime(null);
        }

        // الميزات: إن أُرسلت القائمة (حتى لو فارغة) نستبدل مجموعة الميزات (الاختيار اليدوي له الأولوية).
        if (dto.getFeatureIds() != null) {
            Set<EventFeature> selected = new HashSet<>();
            for (Integer featureId : dto.getFeatureIds()) {
                if (featureId == null) {
                    throw new ApiException("Feature id cannot be null");
                }
                EventFeature feature = eventFeatureRepository.findEventFeatureById(featureId);
                if (feature == null) {
                    throw new ApiException("Feature not found: " + featureId);
                }
                selected.add(feature);
            }
            event.setFeatures(selected);
        } else if (allowAiAutofill) {
            // لم يختر المنظّم ميزات يدوياً، لذلك يقترح الذكاء الاصطناعي حتى 4 ميزات من القائمة المعرّفة مسبقاً.
            event.setFeatures(suggestFeaturesWithAi(event));
        }
        // عند التحديث وعدم إرسال الميزات (null) نترك الميزات الحالية كما هي بدون أي ذكاء اصطناعي.
    }

    // يطلب من الذكاء الاصطناعي اختيار حتى 4 ميزات مناسبة من القائمة المعرّفة مسبقاً بناءً على عنوان ووصف الفعالية.
    // يختار فقط من المعرّفات الموجودة فعلاً، ولا يخترع ميزات. عند أي فشل يرجع مجموعة فارغة دون كسر الإنشاء.
    private Set<EventFeature> suggestFeaturesWithAi(Event event) {
        final int maxFeatures = 4;

        List<EventFeature> allFeatures = eventFeatureRepository.findAll();
        if (allFeatures.isEmpty()) {
            return new HashSet<>();
        }

        // نبني قائمة الميزات المتاحة بصيغة "id - name" حتى يختار منها الذكاء الاصطناعي.
        StringBuilder featureLines = new StringBuilder();
        for (EventFeature feature : allFeatures) {
            featureLines.append(feature.getId()).append(" - ").append(feature.getName()).append("\n");
        }

        String systemPrompt = """
                You select the most relevant features for a neighborhood event from a FIXED predefined list.
                Rules:
                - Choose ONLY from the provided feature IDs. Never invent IDs.
                - Pick the most relevant features based on the event title and description.
                - Return a MAXIMUM of 4 feature IDs (fewer is fine).
                - Return STRICT JSON only: an array of integer IDs, e.g. [1,4,7]. No text, no markdown.
                """;

        String userContent = "Event title: " + (event.getTitle() == null ? "" : event.getTitle())
                + "\nEvent description: " + (event.getDescription() == null ? "" : event.getDescription())
                + "\n\nAvailable features (id - name):\n" + featureLines
                + "\nSelect up to " + maxFeatures + " matching feature IDs.";

        Set<EventFeature> selected = new HashSet<>();
        try {
            String aiResponse = openAIService.askAI(systemPrompt, userContent);
            if (aiResponse == null || "ERROR_FALLBACK".equals(aiResponse)) {
                return selected;
            }

            String json = aiResponse.trim();
            if (json.startsWith("```")) {
                json = json.replace("```json", "").replace("```", "").trim();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            List<Integer> ids = objectMapper.readValue(json, new TypeReference<List<Integer>>() {});

            for (Integer id : ids) {
                if (selected.size() >= maxFeatures) {
                    break; // نضمن عدم تجاوز 4 ميزات
                }
                if (id == null) {
                    continue;
                }
                EventFeature feature = eventFeatureRepository.findEventFeatureById(id);
                if (feature != null) {
                    selected.add(feature);
                }
            }
        } catch (Exception ignored) {
            // أي خطأ في الذكاء الاصطناعي أو في تحليل الرد: نرجع ما تم اختياره (قد يكون فارغاً) دون كسر الإنشاء.
        }

        return selected;
    }

    // نستبدل فقرات البرنامج للفعالية بعد التحقق منها. البرنامج يُدخل يدوياً من المنظّم.
    private void replaceSchedule(Event event, EventInDTO dto) {
        // إن لم تُرسل قائمة البرنامج (null) لا نغيّر البرنامج الحالي.
        if (dto.getSchedule() == null) {
            return;
        }

        // نحذف الفقرات القديمة (مهم عند التحديث) ثم نضيف الجديدة.
        eventScheduleRepository.deleteByEvent_Id(event.getId());

        LocalTime startTime = event.getDate() == null ? null : event.getDate().toLocalTime();
        LocalTime endTime = event.getEndTime() == null ? null : event.getEndTime().toLocalTime();

        Set<String> seen = new HashSet<>();
        List<EventSchedule> toSave = new ArrayList<>();
        int index = 0;
        for (EventScheduleInDTO entry : dto.getSchedule()) {
            // العنوان يجب ألا يكون فارغاً.
            if (entry.getTitle() == null || entry.getTitle().trim().isEmpty()) {
                throw new ApiException("Schedule title cannot be blank");
            }

            // الوقت يجب أن يكون بصيغة HH:mm صحيحة.
            LocalTime parsedTime;
            try {
                parsedTime = LocalTime.parse(entry.getTime(), SCHEDULE_TIME_FORMAT);
            } catch (Exception e) {
                throw new ApiException("Schedule time must be valid HH:mm: " + entry.getTime());
            }

            // منع تكرار نفس الوقت ونفس العنوان لنفس الفعالية.
            String key = parsedTime + "|" + entry.getTitle().trim();
            if (!seen.add(key)) {
                throw new ApiException("Duplicate schedule entry (same time and title): " + entry.getTitle().trim());
            }

            // إن كان للفعالية وقت بداية ونهاية، يجب أن تقع الفقرة ضمن هذا النطاق.
            if (startTime != null && endTime != null
                    && (parsedTime.isBefore(startTime) || parsedTime.isAfter(endTime))) {
                throw new ApiException("Schedule time " + entry.getTime() + " is outside the event time range");
            }

            EventSchedule schedule = new EventSchedule();
            schedule.setTime(parsedTime);
            schedule.setTitle(entry.getTitle().trim());
            schedule.setSortOrder(entry.getSortOrder() != null ? entry.getSortOrder() : index);
            schedule.setEvent(event);
            toSave.add(schedule);
            index++;
        }

        eventScheduleRepository.saveAll(toSave);
    }

    // إرجاع كل الميزات المعرّفة مسبقاً (لعرضها كـ checkboxes في الفرونت إند).
    public List<EventFeatureOutDTO> getAllFeatures() {
        List<EventFeatureOutDTO> out = new ArrayList<>();
        for (EventFeature feature : eventFeatureRepository.findAll()) {
            out.add(new EventFeatureOutDTO(feature.getId(), feature.getName()));
        }
        return out;
    }

    // إرجاع الميزات المختارة لفعالية معيّنة.
    public List<EventFeatureOutDTO> getEventFeatures(Integer eventId) {
        Event event = eventRepository.findEventById(eventId);
        if (event == null) {
            throw new ApiException("Event not found");
        }
        List<EventFeatureOutDTO> out = new ArrayList<>();
        if (event.getFeatures() != null) {
            for (EventFeature feature : event.getFeatures()) {
                out.add(new EventFeatureOutDTO(feature.getId(), feature.getName()));
            }
        }
        return out;
    }

    // إرجاع برنامج الفعالية مرتباً حسب sortOrder ثم الوقت.
    public List<EventScheduleOutDTO> getEventSchedule(Integer eventId) {
        Event event = eventRepository.findEventById(eventId);
        if (event == null) {
            throw new ApiException("Event not found");
        }
        List<EventScheduleOutDTO> out = new ArrayList<>();
        for (EventSchedule schedule : eventScheduleRepository.findByEvent_IdOrderBySortOrderAscTimeAsc(eventId)) {
            out.add(new EventScheduleOutDTO(
                    schedule.getId(),
                    schedule.getTime().format(SCHEDULE_TIME_FORMAT),
                    schedule.getTitle(),
                    schedule.getSortOrder()
            ));
        }
        return out;
    }


// Walaa

    public List<EventRecommendationOutDTO> recommendEventForUser(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        List<FamilyMember> familyMembers = familyMemberRepository.findByUserId(userId);
        System.out.println("FAMILY SIZE = " + familyMembers.size());

        for (FamilyMember member : familyMembers) {
            System.out.println("MEMBER AGE = " + member.getAge());
        }
        List<Event> upcomingEvents = eventRepository.findEventsByDateAfter(LocalDateTime.now());

        if (upcomingEvents.isEmpty()) {
            throw new ApiException("No upcoming events found");
        }

       // return "ready";
        StringBuilder prompt = new StringBuilder();

        prompt.append("Family Members:\n");

        for (FamilyMember member : familyMembers) {
            prompt.append("Name: ")
                    .append(member.getName())
                    .append(" | Age: ")
                    .append(member.getAge())
                    .append(" | Relation: ")
                    .append(member.getRelation())
                    .append("\n");
        }

        prompt.append("\nAvailable Events:\n");

        for (Event event : upcomingEvents) {
            prompt.append("- ")
                    .append(event.getTitle())
                    .append(": ")
                    .append(event.getDescription())
                    .append("\n");
        }
        String aiRecommendation = openAIService.askAI(
                "You are an AI event recommendation system for a smart neighborhood platform. " +
                        "Recommend one suitable event for EACH family member individually based on their age and relation. " +
                        "Use these age groups exactly: children = age 0 to 12, teenagers = age 13 to 18, adults = age 19 and above. " +
                        "Children prefer drawing, games, entertainment, and educational activities. " +
                        "Teenagers prefer sports, technology, competitions, and skill-building activities. " +
                        "Adults prefer community, cultural, educational, and social activities. " +
                        "Choose only from the available events list. " +
                        "Return STRICT JSON only, no markdown, as an array with this exact shape:\n" +
                        "[{\"familyMemberName\":\"...\",\"ageGroup\":\"children|teenagers|adults\",\"recommendedEvent\":\"event title\",\"reason\":\"short reason\"}]",
                prompt.toString()
        );

        if (aiRecommendation == null || "ERROR_FALLBACK".equals(aiRecommendation)) {
            throw new ApiException("AI recommendation failed. Please try again later");
        }

        // نحوّل رد الذكاء الاصطناعي من JSON إلى قائمة منظّمة بدلاً من نص خام.
        List<EventRecommendationOutDTO> recommendations = new ArrayList<>();
        try {
            String json = aiRecommendation.trim();
            if (json.startsWith("```")) {
                json = json.replace("```json", "").replace("```", "").trim();
            }
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> items = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
            for (Map<String, Object> item : items) {
                recommendations.add(new EventRecommendationOutDTO(
                        asText(item.get("familyMemberName")),
                        asText(item.get("ageGroup")),
                        asText(item.get("recommendedEvent")),
                        asText(item.get("reason"))
                ));
            }
        } catch (Exception e) {
            throw new ApiException("AI recommendation failed. Please try again later");
        }

        return recommendations;
    }

    // أداة مساعدة بسيطة لتحويل قيمة JSON إلى نص.
    private String asText(Object value) {
        return value == null ? null : value.toString();
    }

    public WeekendPlanOutDTO generateWeekendFamilyPlan(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        List<FamilyMember> familyMembers = familyMemberRepository.findByUserId(userId);
        LocalDate today = LocalDate.now();
        LocalDate weekendStart = getUpcomingWeekendStart(today);
        LocalDate weekendEnd = weekendStart.getDayOfWeek() == DayOfWeek.FRIDAY ? weekendStart.plusDays(1) : weekendStart;
        LocalDateTime weekendStartDateTime = weekendStart.atStartOfDay();
        LocalDateTime weekendEndDateTime = weekendEnd.atTime(23, 59, 59);

        Integer userNeighborhoodId = user.getNeighborhood() == null ? null : user.getNeighborhood().getId();

        List<Event> upcomingEvents = eventRepository.findEventsByDateBetween(weekendStartDateTime, weekendEndDateTime)
                .stream()
                .filter(event -> "ACTIVE".equals(event.getStatus()))
                .filter(event -> userNeighborhoodId == null
                        || event.getNeighborhood() == null
                        || event.getNeighborhood().getId().equals(userNeighborhoodId))
                .toList();

        List<Initiative> upcomingInitiatives = initiativeRepository.findInitiativesByDateAfter(weekendStart.minusDays(1))
                .stream()
                .filter(initiative -> initiative.getDate() != null && !initiative.getDate().isAfter(weekendEnd))
                .filter(initiative -> "ACTIVE".equals(initiative.getStatus()))
                .filter(initiative -> userNeighborhoodId == null
                        || initiative.getNeighborhood() == null
                        || initiative.getNeighborhood().getId().equals(userNeighborhoodId))
                .toList();

        if (upcomingEvents.isEmpty() && upcomingInitiatives.isEmpty()) {
            throw new ApiException("No upcoming events or initiatives found for the weekend plan");
        }

        StringBuilder prompt = new StringBuilder();

        prompt.append("Family Members:\n");
        if (familyMembers.isEmpty()) {
            prompt.append("- Main user only. No family members are registered.\n");
        } else {
            for (FamilyMember member : familyMembers) {
                prompt.append("- Name: ")
                        .append(member.getName())
                        .append(" | Age: ")
                        .append(member.getAge())
                        .append(" | Gender: ")
                        .append(member.getGender())
                        .append(" | Relation: ")
                        .append(member.getRelation())
                        .append("\n");
            }
        }

        prompt.append("\nUpcoming Events:\n");
        for (Event event : upcomingEvents) {
            prompt.append("- ID ")
                    .append(event.getId())
                    .append(" | ")
                    .append(event.getTitle())
                    .append(" | Date: ")
                    .append(event.getDate())
                    .append(" | Location: ")
                    .append(event.getLocation())
                    .append(" | Paid: ")
                    .append(event.getIsPaid())
                    .append(" | Price: ")
                    .append(event.getPrice())
                    .append(" | Description: ")
                    .append(event.getDescription())
                    .append("\n");
        }

        prompt.append("\nUpcoming Initiatives:\n");
        for (Initiative initiative : upcomingInitiatives) {
            prompt.append("- ID ")
                    .append(initiative.getId())
                    .append(" | ")
                    .append(initiative.getTitle())
                    .append(" | Date: ")
                    .append(initiative.getDate())
                    .append(" | Category: ")
                    .append(initiative.getCategory())
                    .append(" | Description: ")
                    .append(initiative.getDescription())
                    .append("\n");
        }

        String weekendPlan = openAIService.askAI(
                """
                You are an AI family activity planner for a smart neighborhood platform.
                Build a practical weekend plan using only the provided events and initiatives.
                Rules:
                - The "activity" values must be the event/initiative titles exactly as provided.
                - The "reason" values are short and written in a friendly Arabic tone.
                - Include a Friday plan and a Saturday plan (each can have Morning and/or Evening items).
                - Do not invent activities, locations, prices, or dates.
                - If there are not enough activities, return a lighter plan instead of inventing.
                Return STRICT JSON only, no markdown, with this exact shape:
                {"friday":[{"period":"Morning","activity":"...","reason":"..."}],"saturday":[{"period":"Evening","activity":"...","reason":"..."}],"notes":["short practical note"]}
                """,
                prompt.toString()
        );

        if (weekendPlan == null || weekendPlan.isBlank() || "ERROR_FALLBACK".equals(weekendPlan)) {
            throw new ApiException("AI weekend planner failed. Please try again later");
        }

        // نحوّل خطة الذكاء الاصطناعي من JSON إلى كائن منظّم بدلاً من نص خام.
        try {
            String json = weekendPlan.trim();
            if (json.startsWith("```")) {
                json = json.replace("```json", "").replace("```", "").trim();
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> parsed = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});

            List<WeekendPlanItemDTO> friday = parsePlanItems(parsed.get("friday"));
            List<WeekendPlanItemDTO> saturday = parsePlanItems(parsed.get("saturday"));

            List<String> notes = new ArrayList<>();
            Object rawNotes = parsed.get("notes");
            if (rawNotes instanceof List<?> noteList) {
                for (Object note : noteList) {
                    if (note != null) {
                        notes.add(note.toString());
                    }
                }
            }

            return new WeekendPlanOutDTO(friday, saturday, notes);
        } catch (Exception e) {
            throw new ApiException("AI weekend planner failed. Please try again later");
        }
    }

    // نحوّل قائمة فقرات اليوم (Morning/Evening) من JSON إلى قائمة DTO.
    private List<WeekendPlanItemDTO> parsePlanItems(Object raw) {
        List<WeekendPlanItemDTO> items = new ArrayList<>();
        if (raw instanceof List<?> list) {
            for (Object element : list) {
                if (element instanceof Map<?, ?> map) {
                    items.add(new WeekendPlanItemDTO(
                            asText(map.get("period")),
                            asText(map.get("activity")),
                            asText(map.get("reason"))
                    ));
                }
            }
        }
        return items;
    }

    private LocalDate getUpcomingWeekendStart(LocalDate today) {
        if (today.getDayOfWeek() == DayOfWeek.FRIDAY) {
            return today;
        }

        LocalDate date = today;
        while (date.getDayOfWeek() != DayOfWeek.FRIDAY) {
            date = date.plusDays(1);
        }
        return date;
    }


















    // Walaa
//    private EventOutDTO convertToOutDTO(Event event) {
//        return new EventOutDTO(
//                event.getId(),
//                event.getTitle(),
//                event.getDescription(),
//                event.getDate(),
//                event.getLocation(),
//                event.getIsPaid(),
//                event.getPrice(),
//                event.getMaxParticipants(),
//                event.getStatus()
//        );
//    }


}
