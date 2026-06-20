package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.ContactRequestDto;
import org.example.menaandfeena_finalproject.DTO.In.UserRegisterRequestDto;
import org.example.menaandfeena_finalproject.DTO.Out.*;
import org.example.menaandfeena_finalproject.Model.*;
import org.example.menaandfeena_finalproject.Repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final InitiativeParticipationRepository initiativeParticipationRepository;
    private final IssueReportRepository issueReportRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final ElectionRoundRepository electionRoundRepository;
    private final EventRepository eventRepository;
    private final InitiativeRepository initiativeRepository;
    private final MarketPlaceItemRepository marketPlaceItemRepository;
    private final OrderRepository orderRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final EmailService emailService;

    @Value("${app.support.email}")
    private String supportEmail;
    //Reenad
    // =========================
    // AGE
    // =========================

    public Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // =========================
    // USER CRUD
    // =========================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public void updateUser(Integer userId, User user) {

        User old = getUserOrThrow(userId);

        old.setFullName(user.getFullName());
        old.setEmail(user.getEmail());
        old.setPassword(user.getPassword());
        old.setPhone(user.getPhone());
        old.setNationalId(user.getNationalId());
        old.setBirthDate(user.getBirthDate());
        old.setGender(user.getGender());
        old.setStatus(user.getStatus());
        old.setYearsInNeighborhood(user.getYearsInNeighborhood());
        old.setIsVerified(user.getIsVerified());

        userRepository.save(old);
    }

    public void deleteUser(Integer userId) {

        User user = getUserOrThrow(userId);

        userRepository.delete(user);
    }

    // =========================
    // PUBLIC INFO
    // =========================

    public WelcomeDTO getWelcomeScreen() {

        return new WelcomeDTO(
                "منا وفينا",
                "مجتمع واحد .. هدف واحد",
                "نُسهم معًا في بناء مجتمع متكافل ومستدام من خلال مبادرات نوعية وشراكات مجتمعية فاعلة داخل الحي، مدعومة بتقنيات الذكاء الاصطناعي لتعزيز المشاركة المجتمعية ورفع جودة الحياة"
        );
    }

    public AboutInfoDTO getAboutInfo() {

        return new AboutInfoDTO(
                "منصة مجتمعية ولدت من قلب الحي، تسعى لتعزيز الروابط الإنسانية وبناء بيئة تكافلية ومستدامة بين الجيران.",
                "أن نكون النموذج الرائد عالمياً في تحويل الأحياء السكنية إلى مجتمعات حيوية، مترابطة، وذكية تدعم جودة الحياة ورفاهية الجميع.",
                "تفعيل دور الأفراد من خلال مبادرات نوعية وشراكات فاعلة تساهم في تبادل الخبرات، الدعم المتبادل، وحماية البيئة المحيطة بنا.",
                List.of("المبادرة", "الاستدامة", "التكافل")
        );
    }

    public void sendContactMessage(ContactRequestDto dto) {
        emailService.sendContactEmail(
                supportEmail,
                dto
        );
    }

    // =========================
    // REGISTER
    // =========================

    public UserRegisterResponseDto registerUser(UserRegisterRequestDto dto) {

        String nationalId = dto.getNationalId();

        if (nationalId == null ||
                nationalId.length() != 10 ||
                (!nationalId.startsWith("1") && !nationalId.startsWith("2"))) {

            throw new ApiException("الهوية الوطنية غير صالحة");
        }

        if (dto.getLatitude() == null || dto.getLongitude() == null) {
            throw new ApiException("يجب إدخال إحداثيات المستخدم لتحديد الحي");
        }

        Map<String, Object> geoData =
                fetchDistrictFromCoordinates(
                        dto.getLatitude(),
                        dto.getLongitude()
                );

        String districtName = (String) geoData.get("district");
        String cityName = (String) geoData.get("city");

        if (districtName == null || districtName.isBlank()) {
            throw new ApiException("لم يتم العثور على اسم الحي من الإحداثيات");
        }

        if (cityName == null || cityName.isBlank()) {
            throw new ApiException("لم يتم العثور على اسم المدينة من الإحداثيات");
        }

        Neighborhood neighborhood =
                neighborhoodRepository.findByName(districtName).orElse(null);

        if (neighborhood == null) {

            neighborhood = new Neighborhood();

            neighborhood.setName(districtName);
            neighborhood.setCity(cityName);

            neighborhood.setEstimatedPopulation(null);
            neighborhood.setRegisteredPopulation(0);

            neighborhood.setLatitude(dto.getLatitude());
            neighborhood.setLongitude(dto.getLongitude());

            neighborhood = neighborhoodRepository.save(neighborhood);
        }

        neighborhood.setRegisteredPopulation(
                neighborhood.getRegisteredPopulation() + 1
        );

        neighborhoodRepository.save(neighborhood);

        User user = new User();

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        user.setNationalId(dto.getNationalId());
        user.setBirthDate(dto.getBirthDate());
        user.setGender(dto.getGender());
        user.setStatus("RESIDENT");
        user.setIsVerified(true);
        user.setNeighborhood(neighborhood);
        user.setYearsInNeighborhood(dto.getYearsInNeighborhood());
        user.setLatitude(dto.getLatitude());
        user.setLongitude(dto.getLongitude());

        User savedUser = userRepository.save(user);

        openElectionRoundIfNeighborhoodReady(neighborhood);

        return new UserRegisterResponseDto(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                neighborhood.getName(),
                neighborhood.getCity(),
                savedUser.getCreatedAt()
        );
    }

    // =========================
    // NEIGHBORHOOD RESIDENTS
    // =========================

    public List<NeighborhoodResidentDTO> getNeighborhoodResidents(Integer userId) {

        User currentUser = getUserOrThrow(userId);

        if (currentUser.getNeighborhood() == null) {
            throw new ApiException("أنت غير مرتبط بأي حي سكني حالياً لتصفح جيرانك");
        }

        List<User> residents =
                userRepository.findByNeighborhoodId(
                        currentUser.getNeighborhood().getId()
                );

        return residents.stream()
                .filter(resident -> !resident.getId().equals(userId))
                .map(resident ->
                        new NeighborhoodResidentDTO(
                                resident.getId(),
                                resident.getFullName(),
                                resident.getGender(),
                                resident.getPhone()
                        )
                )
                .toList();
    }

    // =========================
    // USER ACTIVITY LOG
    // =========================

    public UserActivityLogDTO getUserActivityLog(Integer userId) {

        User user = getUserOrThrow(userId);

        List<UserIssueDTO> issues =
                issueReportRepository.findByReporterId(userId)
                        .stream()
                        .map(i ->
                                new UserIssueDTO(
                                        i.getId(),
                                        i.getTitle(),
                                        i.getCategory(),
                                        i.getPriority(),
                                        i.getStatus(),
                                        i.getCreatedAt()
                                )
                        )
                        .toList();

        List<UserEventDTO> events =
                eventRegistrationRepository.findByUserId(userId)
                        .stream()
                        .filter(r -> r.getEvent() != null)
                        .map(r ->
                                new UserEventDTO(
                                        r.getEvent().getId(),
                                        r.getEvent().getTitle(),
                                        r.getEvent().getDate(),
                                        r.getEvent().getStatus(),
                                        r.getEvent().getLocation()
                                )
                        )
                        .toList();

        List<UserInitiativeDTO> initiatives =
                initiativeParticipationRepository.findByUserId(userId)
                        .stream()
                        .filter(p -> p.getInitiative() != null)
                        .map(p ->
                                new UserInitiativeDTO(
                                        p.getInitiative().getId(),
                                        p.getInitiative().getTitle(),
                                        p.getInitiative().getDate(),
                                        p.getInitiative().getStatus(),
                                        p.getInitiative().getCategory()
                                )
                        )
                        .toList();

        return new UserActivityLogDTO(
                user.getId(),
                user.getFullName(),
                issues,
                events,
                initiatives
        );
    }

    // =========================
    // PROFILE BASIC
    // =========================

    public UserBasicInfoDTO getBasicProfile(Integer userId) {

        User user = getUserOrThrow(userId);

        UserBasicInfoDTO dto = new UserBasicInfoDTO();

        dto.setFullName(user.getFullName());

        dto.setNeighborhoodName(
                user.getNeighborhood() != null
                        ? user.getNeighborhood().getName()
                        : "غير مرتبط بحي"
        );

        dto.setMemberSince(user.getCreatedAt());
        dto.setYearsInNeighborhood(user.getYearsInNeighborhood());

        return dto;
    }

    // =========================
    // PROFILE COMMUNITY
    // =========================

    public UserProfileCommunityDTO getCommunityProfile(Integer userId) {

        User user = getUserOrThrow(userId);

        UserProfileCommunityDTO dto = new UserProfileCommunityDTO();

        dto.setBasicInfo(getBasicProfile(userId));

        dto.setFamilyMembers(
                user.getFamilyMembers()
                        .stream()
                        .map(f ->
                                new FamilyMemberDTO(
                                        f.getName(),
                                        f.getAge(),
                                        f.getRelation()
                                )
                        )
                        .toList()
        );

        dto.setLastActivity(getLastActivity(user));

        dto.setVotes(mapVotes(user));

        return dto;
    }

    // =========================
    // PROFILE ACTIVITIES
    // =========================

    public UserProfileActivitiesDTO getActivitiesProfile(Integer userId) {

        User user = getUserOrThrow(userId);

        UserProfileActivitiesDTO dto = new UserProfileActivitiesDTO();

        dto.setBasicInfo(getBasicProfile(userId));

        dto.setParticipatedEvents(getParticipatedEvents(userId));

        dto.setCreatedEvents(
                eventRepository.findByCreatorId(userId)
                        .stream()
                        .map(e ->
                                new UserEventDTO(
                                        e.getId(),
                                        e.getTitle(),
                                        e.getDate(),
                                        e.getStatus(),
                                        e.getLocation()
                                )
                        )
                        .toList()
        );

        dto.setParticipatedInitiatives(getParticipatedInitiatives(userId));

        dto.setCreatedInitiatives(
                initiativeRepository.findByCreatorId(userId)
                        .stream()
                        .map(i ->
                                new UserInitiativeDTO(
                                        i.getId(),
                                        i.getTitle(),
                                        i.getDate(),
                                        i.getStatus(),
                                        i.getCategory()
                                )
                        )
                        .toList()
        );

        return dto;
    }

    // =========================
    // PROFILE REPUTATION
    // =========================

    public UserProfileReputationDTO getReputationProfile(Integer userId) {

        User user = getUserOrThrow(userId);

        UserProfileReputationDTO dto = new UserProfileReputationDTO();

        dto.setBasicInfo(getBasicProfile(userId));

        dto.setWrittenReviews(
                user.getReviews()
                        .stream()
                        .map(r ->
                                new UserReviewDTO(
                                        r.getId(),
                                        r.getRating(),
                                        r.getComment(),
                                        r.getCreatedAt(),
                                        r.getUser().getFullName()
                                )
                        )
                        .toList()
        );

        dto.setReceivedReviews(new ArrayList<>());

        dto.setIssueReports(getUserIssues(user));

        return dto;
    }

    // =========================
    // PROFILE MARKETPLACE
    // =========================

    public UserProfileMarketplaceDTO getMarketplaceProfile(Integer userId) {

        User user = getUserOrThrow(userId);

        UserProfileMarketplaceDTO dto = new UserProfileMarketplaceDTO();

        dto.setBasicInfo(getBasicProfile(userId));

        dto.setMarketplaceItems(
                marketPlaceItemRepository.findMarketPlaceItemsByUserId(userId)
                        .stream()
                        .map(item ->
                                new UserMarketItemDTO(
                                        item.getId(),
                                        item.getTitle(),
                                        item.getType(),
                                        item.getStatus(),
                                        item.getQuantity(),
                                        item.getPrice(),
                                        item.getRentPrice()
                                )
                        )
                        .toList()
        );

        dto.setPurchases(getPurchases(userId));

        dto.setSales(getSales(userId));

        return dto;
    }

    // =========================
    // PROFILE FULL
    // =========================

    public UserProfileDetailsDTO getUserProfileDetails(Integer userId) {

        User user = getUserOrThrow(userId);

        UserProfileDetailsDTO dto = new UserProfileDetailsDTO();

        dto.setBasicInfo(getBasicProfile(userId));

        dto.setFamilyMembers(
                user.getFamilyMembers()
                        .stream()
                        .map(f ->
                                new FamilyMemberDTO(
                                        f.getName(),
                                        f.getAge(),
                                        f.getRelation()
                                )
                        )
                        .toList()
        );

        dto.setLastActivity(getLastActivity(user));

        dto.setVotes(mapVotes(user));

        dto.setParticipatedEvents(getParticipatedEvents(userId));

        dto.setCreatedEvents(
                eventRepository.findByCreatorId(userId)
                        .stream()
                        .map(e ->
                                new UserEventDTO(
                                        e.getId(),
                                        e.getTitle(),
                                        e.getDate(),
                                        e.getStatus(),
                                        e.getLocation()
                                )
                        )
                        .toList()
        );

        dto.setParticipatedInitiatives(getParticipatedInitiatives(userId));

        dto.setCreatedInitiatives(
                initiativeRepository.findByCreatorId(userId)
                        .stream()
                        .map(i ->
                                new UserInitiativeDTO(
                                        i.getId(),
                                        i.getTitle(),
                                        i.getDate(),
                                        i.getStatus(),
                                        i.getCategory()
                                )
                        )
                        .toList()
        );

        dto.setWrittenReviews(
                user.getReviews()
                        .stream()
                        .map(r ->
                                new UserReviewDTO(
                                        r.getId(),
                                        r.getRating(),
                                        r.getComment(),
                                        r.getCreatedAt(),
                                        r.getUser().getFullName()
                                )
                        )
                        .toList()
        );

        dto.setReceivedReviews(new ArrayList<>());

        dto.setIssueReports(getUserIssues(user));

        dto.setMarketplaceItems(
                marketPlaceItemRepository.findMarketPlaceItemsByUserId(userId)
                        .stream()
                        .map(item ->
                                new UserMarketItemDTO(
                                        item.getId(),
                                        item.getTitle(),
                                        item.getType(),
                                        item.getStatus(),
                                        item.getQuantity(),
                                        item.getPrice(),
                                        item.getRentPrice()
                                )
                        )
                        .toList()
        );

        dto.setPurchases(getPurchases(userId));

        dto.setSales(getSales(userId));

        return dto;
    }

    // =========================
    // HELPERS
    // =========================

    private User getUserOrThrow(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        return user;
    }

    private void openElectionRoundIfNeighborhoodReady(Neighborhood neighborhood) {

        if (neighborhood.getRegisteredPopulation() >= 3) {

            boolean hasActiveRound =
                    electionRoundRepository.existsByStatusAndNeighborhoodId(
                            "ACTIVE",
                            neighborhood.getId()
                    );

            if (!hasActiveRound) {

                ElectionRound firstRound = new ElectionRound();

                firstRound.setStartDate(LocalDate.now());
                firstRound.setEndDate(LocalDate.now().plusDays(1));
                firstRound.setStatus("ACTIVE");
                firstRound.setNeighborhood(neighborhood);

                electionRoundRepository.save(firstRound);
            }
        }
    }

    private Map<String, Object> fetchDistrictFromCoordinates(
            Double lat,
            Double lon
    ) {

        try {

            String url =
                    String.format(
                            "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=%s&lon=%s&accept-language=ar",
                            lat,
                            lon
                    );

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "ManaWaFina/1.0");

            HttpEntity<String> entity =
                    new HttpEntity<>(headers);

            ResponseEntity<Map> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            Map.class
                    );

            Map<String, Object> body =
                    response.getBody();

            if (body == null || !body.containsKey("address")) {
                throw new ApiException("لم يتم العثور على بيانات العنوان من الإحداثيات");
            }

            Map<String, Object> address =
                    (Map<String, Object>) body.get("address");

            String district =
                    (String) address.getOrDefault(
                            "suburb",
                            address.getOrDefault(
                                    "neighbourhood",
                                    address.getOrDefault(
                                            "city_district",
                                            null
                                    )
                            )
                    );

            String city =
                    (String) address.getOrDefault(
                            "city",
                            address.getOrDefault(
                                    "town",
                                    address.getOrDefault(
                                            "state",
                                            null
                                    )
                            )
                    );

            if (district == null || district.isBlank()) {
                throw new ApiException("لم يتم تحديد الحي من الإحداثيات");
            }

            if (city == null || city.isBlank()) {
                throw new ApiException("لم يتم تحديد المدينة من الإحداثيات");
            }

            return Map.of(
                    "district", district,
                    "city", city
            );

        } catch (ApiException e) {
            throw e;

        } catch (Exception e) {
            throw new ApiException("تعذر تحديد الحي من الإحداثيات حالياً");
        }
    }

    private LastActivityDTO getLastActivity(User user) {

        LastActivityDTO dto = new LastActivityDTO();

        LocalDateTime latestDate = null;

        if (user.getVotes() != null) {

            for (MayorVote vote : user.getVotes()) {

                if (vote.getCreatedAt() != null &&
                        (latestDate == null ||
                                vote.getCreatedAt().isAfter(latestDate))) {

                    latestDate = vote.getCreatedAt();

                    dto.setActivityType("VOTE");
                    dto.setTitle(
                            vote.getMayorCandidate()
                                    .getUser()
                                    .getFullName()
                    );
                    dto.setActivityDate(vote.getCreatedAt());
                }
            }
        }

        if (user.getIssueReports() != null) {

            for (IssueReport report : user.getIssueReports()) {

                if (report.getCreatedAt() != null &&
                        (latestDate == null ||
                                report.getCreatedAt().isAfter(latestDate))) {

                    latestDate = report.getCreatedAt();

                    dto.setActivityType("ISSUE_REPORT");
                    dto.setTitle(report.getTitle());
                    dto.setActivityDate(report.getCreatedAt());
                }
            }
        }

        return dto;
    }

    private List<UserVoteDTO> mapVotes(User user) {

        if (user.getVotes() == null) {
            return new ArrayList<>();
        }

        return user.getVotes()
                .stream()
                .map(v ->
                        new UserVoteDTO(
                                v.getId(),
                                v.getMayorCandidate()
                                        .getUser()
                                        .getFullName(),
                                v.getElectionRound().getId(),
                                v.getElectionRound()
                                        .getStartDate()
                                        .getYear(),
                                v.getElectionRound().getStatus(),
                                v.getCreatedAt()
                        )
                )
                .toList();
    }

    private List<UserIssueDTO> getUserIssues(User user) {

        if (user.getIssueReports() == null) {
            return new ArrayList<>();
        }

        return user.getIssueReports()
                .stream()
                .map(i ->
                        new UserIssueDTO(
                                i.getId(),
                                i.getTitle(),
                                i.getCategory(),
                                i.getPriority(),
                                i.getStatus(),
                                i.getCreatedAt()
                        )
                )
                .toList();
    }

    private List<UserEventDTO> getParticipatedEvents(Integer userId) {

        return eventRegistrationRepository.findByUserId(userId)
                .stream()
                .filter(r -> r.getEvent() != null)
                .map(r ->
                        new UserEventDTO(
                                r.getEvent().getId(),
                                r.getEvent().getTitle(),
                                r.getEvent().getDate(),
                                r.getEvent().getStatus(),
                                r.getEvent().getLocation()
                        )
                )
                .toList();
    }

    private List<UserInitiativeDTO> getParticipatedInitiatives(Integer userId) {

        return initiativeParticipationRepository.findByUserId(userId)
                .stream()
                .filter(p -> p.getInitiative() != null)
                .map(p ->
                        new UserInitiativeDTO(
                                p.getInitiative().getId(),
                                p.getInitiative().getTitle(),
                                p.getInitiative().getDate(),
                                p.getInitiative().getStatus(),
                                p.getInitiative().getCategory()
                        )
                )
                .toList();
    }

    private List<UserOrderDTO> getPurchases(Integer userId) {

        return orderRepository.findOrdersByUserId(userId)
                .stream()
                .map(o ->
                        new UserOrderDTO(
                                o.getId(),
                                o.getMarketPlaceItem().getTitle(),
                                o.getUser().getFullName(),
                                o.getSeller().getFullName(),
                                o.getMarketPlaceItem().getType(),
                                o.getStatus(),
                                o.getTotalAmount()
                        )
                )
                .toList();
    }

    private List<UserOrderDTO> getSales(Integer userId) {

        return orderRepository.findOrdersBySellerId(userId)
                .stream()
                .map(o ->
                        new UserOrderDTO(
                                o.getId(),
                                o.getMarketPlaceItem().getTitle(),
                                o.getUser().getFullName(),
                                o.getSeller().getFullName(),
                                o.getMarketPlaceItem().getType(),
                                o.getStatus(),
                                o.getTotalAmount()
                        )
                )
                .toList();
    }
}