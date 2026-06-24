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
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final WhatsAppService whatsAppService;
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
    private final OrderItemRepository orderItemRepository;

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final NominatimService nominatimService;

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

    public void createUser(UserRegisterRequestDto dto) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        user.setNationalId(dto.getNationalId());
        user.setBirthDate(dto.getBirthDate());
        user.setGender(dto.getGender());
        user.setYearsInNeighborhood(dto.getYearsInNeighborhood());
        user.setLatitude(dto.getLatitude());
        user.setLongitude(dto.getLongitude());
        user.setStatus("RESIDENT");
        userRepository.save(user);
    }

    public void updateUser(Integer userId, UserRegisterRequestDto dto) {

        User old = getUserOrThrow(userId);

        old.setFullName(dto.getFullName());
        old.setEmail(dto.getEmail());
        old.setPassword(passwordEncoder.encode(dto.getPassword()));        old.setPhone(dto.getPhone());
        old.setNationalId(dto.getNationalId());
        old.setBirthDate(dto.getBirthDate());
        old.setGender(dto.getGender());
        old.setYearsInNeighborhood(dto.getYearsInNeighborhood());
        old.setLatitude(dto.getLatitude());
        old.setLongitude(dto.getLongitude());

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
        user.setNeighborhood(neighborhood);
        user.setYearsInNeighborhood(dto.getYearsInNeighborhood());
        user.setLatitude(dto.getLatitude());
        user.setLongitude(dto.getLongitude());

        User savedUser = userRepository.save(user);

        String whatsappMessage =
                """
                🌞🏡🌳
        
                هلا والله %s،
        
                تم تسجيلك بنجاح في منصة منا وفينا والانضمام إلى حي %s 💚
        
                نؤمن أن الحي القوي يبدأ من سكانه، وأن التعاون يصنع فرقاً حقيقياً.
        
                🤝 شارك
                🌿 بادر
                📢 اقترح
                🎉 احضر الفعاليات
                🗳️ صوت لمن يمثل حيك
        
                أهلاً بك بين جيرانك نورت الحي 🌳🏡
        
                منا وفينا 💚
                """.formatted(
                        savedUser.getFullName(),
                        neighborhood.getName()
                );
        try {

            whatsAppService.sendWhatsAppMessage(
                    savedUser.getPhone(),
                    whatsappMessage
            );

        } catch (Exception e) {
        }

        openElectionRoundIfNeighborhoodReady(neighborhood);

        return new UserRegisterResponseDto(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                neighborhood.getName(),
                neighborhood.getCity(),
                savedUser.getCreatedAt()
        );}

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
    // PROFILE MARKETPLACE
    // =========================
    public UserMarketplaceSummaryDTO getMarketplaceSummary(Integer userId) {

        getUserOrThrow(userId);

        List<UserMarketItemDTO> myProducts =
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
                        .toList();

        List<UserOrderDTO> myOrders = getPurchases(userId);
        List<UserOrderDTO> productOrders = getSales(userId);

        Integer totalPurchasesAmount =
                myOrders.stream()
                        .mapToInt(o -> o.getTotalAmount() == null ? 0 : o.getTotalAmount())
                        .sum();

        Integer totalSalesAmount =
                productOrders.stream()
                        .mapToInt(o -> o.getTotalAmount() == null ? 0 : o.getTotalAmount())
                        .sum();

        return new UserMarketplaceSummaryDTO(
                getBasicProfile(userId),
                myOrders.size(),
                productOrders.size(),
                myProducts.size(),
                totalPurchasesAmount,
                totalSalesAmount,
                myProducts
        );
    }

    public UserOrdersPageDTO getMyOrdersPage(Integer userId) {

        getUserOrThrow(userId);

        List<UserOrderDTO> orders = getPurchases(userId);

        Integer totalAmount =
                orders.stream()
                        .mapToInt(o -> o.getTotalAmount() == null ? 0 : o.getTotalAmount())
                        .sum();

        return new UserOrdersPageDTO(
                getBasicProfile(userId),
                orders.size(),
                totalAmount,
                orders
        );
    }

    public UserOrdersPageDTO getMyProductOrdersPage(Integer userId) {

        getUserOrThrow(userId);

        List<UserOrderDTO> orders = getSales(userId);

        Integer totalAmount =
                orders.stream()
                        .mapToInt(o -> o.getTotalAmount() == null ? 0 : o.getTotalAmount())
                        .sum();

        return new UserOrdersPageDTO(
                getBasicProfile(userId),
                orders.size(),
                totalAmount,
                orders
        );
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

                sendElectionStartedWhatsAppMessages(neighborhood, firstRound);
            }
        }
    }
    private void sendElectionStartedWhatsAppMessages(
            Neighborhood neighborhood,
            ElectionRound electionRound
    ) {

        List<User> residents =
                userRepository.findByNeighborhoodId(
                        neighborhood.getId()
                );

        for (User resident : residents) {

            if (resident.getPhone() == null ||
                    resident.getPhone().isBlank()) {
                continue;
            }

            String message =
                    """
                    🗳️🌳 انتخابات الحي بدأت الآن
    
                    هلا والله %s 👋
    
                    تم فتح جولة انتخابية جديدة في حي %s 🏡
    
                    يمكنك الآن الدخول إلى منصة منا وفينا والاطلاع على المرشحين والتصويت لمن يمثل حيك.
    
                    📅 بداية الجولة: %s
                    📅 نهاية الجولة: %s
    
                    صوتك يساهم في بناء حي أكثر تعاوناً وتنظيماً 🤝🌿
    
                    منا وفينا 💚
                    """.formatted(
                            resident.getFullName(),
                            neighborhood.getName(),
                            electionRound.getStartDate(),
                            electionRound.getEndDate()
                    );

            try {
                whatsAppService.sendWhatsAppMessage(
                        resident.getPhone(),
                        message
                );
            } catch (Exception e) {
            }
        }
    }

    private Map<String, Object> fetchDistrictFromCoordinates(
            Double lat,
            Double lon
    ) {
        // نعيد استخدام NominatimService بدل تكرار منطق reverse geocoding هنا.
        Map<String, String> location = nominatimService.detectLocationFromCoordinates(lat, lon);

        String district = location.get("district");
        String city = location.get("city");

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

        List<UserOrderDTO> purchases = new ArrayList<>();

        for (Orders order : orderRepository.findOrdersByUserId(userId)) {

            String buyerName =
                    order.getUser() == null ? null : order.getUser().getFullName();

            for (OrderItem orderItem :
                    orderItemRepository.findOrderItemsByOrdersId(order.getId())) {

                MarketPlaceItem item = orderItem.getMarketPlaceItem();

                if (item == null) {
                    continue;
                }

                String sellerName =
                        item.getUser() == null ? null : item.getUser().getFullName();

                purchases.add(
                        new UserOrderDTO(
                                order.getId(),
                                orderItem.getId(),
                                item.getTitle(),
                                buyerName,
                                sellerName,
                                orderItem.getType(),
                                item.getStatus(),
                                orderItem.getQuantity(),
                                orderItem.getUnitPrice(),
                                orderItem.getRentalDays(),
                                orderItem.getDepositAmount(),
                                orderItem.getSubtotal(),
                                orderItem.getStartDate(),
                                orderItem.getEndDate(),
                                orderItem.getReturnStatus()
                        )
                );
            }
        }

        return purchases;
    }
    private List<UserOrderDTO> getSales(Integer userId) {

        List<UserOrderDTO> sales = new ArrayList<>();

        for (Orders order :
                orderRepository.findDistinctByOrderItemsMarketPlaceItemUserId(userId)) {

            String buyerName =
                    order.getUser() == null ? null : order.getUser().getFullName();

            for (OrderItem orderItem :
                    orderItemRepository.findOrderItemsByOrdersId(order.getId())) {

                MarketPlaceItem item = orderItem.getMarketPlaceItem();

                if (item == null ||
                        item.getUser() == null ||
                        !item.getUser().getId().equals(userId)) {
                    continue;
                }

                sales.add(
                        new UserOrderDTO(
                                order.getId(),
                                orderItem.getId(),
                                item.getTitle(),
                                buyerName,
                                item.getUser().getFullName(),
                                orderItem.getType(),
                                item.getStatus(),
                                orderItem.getQuantity(),
                                orderItem.getUnitPrice(),
                                orderItem.getRentalDays(),
                                orderItem.getDepositAmount(),
                                orderItem.getSubtotal(),
                                orderItem.getStartDate(),
                                orderItem.getEndDate(),
                                orderItem.getReturnStatus()
                        )
                );
            }
        }

        return sales;
    }

    public UserProfileFamilyDTO getFamilyProfile(Integer userId) {
        User user = getUserOrThrow(userId);

        return new UserProfileFamilyDTO(
                getBasicProfile(userId),
                user.getFamilyMembers()
                        .stream()
                        .map(f -> new FamilyMemberDTO(
                                f.getName(),
                                f.getAge(),
                                f.getRelation()
                        ))
                        .toList()
        );
    }
    public UserProfileVotesDTO getVotesProfile(Integer userId) {
        User user = getUserOrThrow(userId);

        return new UserProfileVotesDTO(
                getBasicProfile(userId),
                mapVotes(user)
        );
    }
    public UserProfileEventsDTO getEventsProfile(Integer userId) {
        getUserOrThrow(userId);

        return new UserProfileEventsDTO(
                getBasicProfile(userId),
                getParticipatedEvents(userId),
                eventRepository.findByCreatorId(userId)
                        .stream()
                        .map(e -> new UserEventDTO(
                                e.getId(),
                                e.getTitle(),
                                e.getDate(),
                                e.getStatus(),
                                e.getLocation()
                        ))
                        .toList()
        );
    }
    public UserProfileInitiativesDTO getInitiativesProfile(Integer userId) {
        getUserOrThrow(userId);

        return new UserProfileInitiativesDTO(
                getBasicProfile(userId),
                getParticipatedInitiatives(userId),
                initiativeRepository.findByCreatorId(userId)
                        .stream()
                        .map(i -> new UserInitiativeDTO(
                                i.getId(),
                                i.getTitle(),
                                i.getDate(),
                                i.getStatus(),
                                i.getCategory()
                        ))
                        .toList()
        );
    }
    public UserProfileReviewsDTO getReviewsProfile(Integer userId) {
        User user = getUserOrThrow(userId);

        List<UserReviewDTO> writtenReviews =
                user.getReviews()
                        .stream()
                        .map(r -> new UserReviewDTO(
                                r.getId(),
                                r.getRating(),
                                r.getComment(),
                                r.getCreatedAt(),
                                r.getUser().getFullName()
                        ))
                        .toList();

        return new UserProfileReviewsDTO(
                getBasicProfile(userId),
                writtenReviews,
                new ArrayList<>()
        );
    }
    public UserProfileIssuesDTO getIssuesProfile(Integer userId) {
        User user = getUserOrThrow(userId);

        List<UserIssueDTO> reports = getUserIssues(user);

        Integer completed =
                (int) reports.stream()
                        .filter(r -> "COMPLETED".equalsIgnoreCase(r.getStatus()))
                        .count();

        Integer open =
                reports.size() - completed;

        return new UserProfileIssuesDTO(
                getBasicProfile(userId),
                reports.size(),
                open,
                completed,
                reports
        );
    }
}
