package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.NeighborhoodInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.*;
import org.example.menaandfeena_finalproject.Model.*;
import org.example.menaandfeena_finalproject.Repository.*;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;


import org.example.menaandfeena_finalproject.DTO.Out.EventDTO;
import org.example.menaandfeena_finalproject.DTO.Out.InitiativeDTO;
import org.example.menaandfeena_finalproject.DTO.Out.IssueReportDTO;
import org.example.menaandfeena_finalproject.DTO.Out.NeighborhoodDashboardDTO;
import org.example.menaandfeena_finalproject.Model.*;
import org.example.menaandfeena_finalproject.Repository.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NeighborhoodService {

    private final NeighborhoodRepository neighborhoodRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final InitiativeRepository initiativeRepository;
    private final IssueReportRepository issueReportRepository;
    private final MarketPlaceItemRepository marketPlaceItemRepository;
    private final OpenAIService openAIService;


    //Reenad
    // =========================
    // GET ALL NEIGHBORHOODS
    // =========================

    public List<Neighborhood> getAllNeighborhoods() {
        List<Neighborhood> neighborhoods = neighborhoodRepository.findAll();

        // نملأ عدد السكان المقدّر بالذكاء الاصطناعي لأي حي قيمته null (best-effort) ونحفظه حتى لا يبقى null.
        for (Neighborhood neighborhood : neighborhoods) {
            if (neighborhood.getEstimatedPopulation() == null) {
                Integer estimated = estimatePopulationByAI(neighborhood.getName(), neighborhood.getCity());
                if (estimated != null) {
                    neighborhood.setEstimatedPopulation(estimated);
                    neighborhoodRepository.save(neighborhood);
                }
            }
        }

        return neighborhoods;
    }


    // =========================
    // CREATE NEIGHBORHOOD
    // =========================

    public void createNeighborhood(NeighborhoodInDTO dto) {
        Neighborhood neighborhood = new Neighborhood();
        neighborhood.setName(dto.getName());
        neighborhood.setCity(dto.getCity());
        // إن لم يُرسل المدير عدد السكان المقدّر، نقدّره بالذكاء الاصطناعي حتى لا يكون null.
        Integer estimatedPopulation = dto.getEstimatedPopulation() != null
                ? dto.getEstimatedPopulation()
                : estimatePopulationByAI(dto.getName(), dto.getCity());
        neighborhood.setEstimatedPopulation(estimatedPopulation);
        neighborhood.setRegisteredPopulation(0);
        neighborhood.setLatitude(dto.getLatitude());
        neighborhood.setLongitude(dto.getLongitude());

        neighborhoodRepository.save(neighborhood);
    }


    // =========================
    // UPDATE NEIGHBORHOOD
    // =========================

    public void updateNeighborhood(Integer neighborhoodId,
                                   NeighborhoodInDTO dto) {

        Neighborhood oldNeighborhood =
                getNeighborhoodOrThrow(neighborhoodId);

        oldNeighborhood.setName(dto.getName());
        oldNeighborhood.setCity(dto.getCity());
        oldNeighborhood.setEstimatedPopulation(dto.getEstimatedPopulation());
        oldNeighborhood.setRegisteredPopulation(
                userRepository.findByNeighborhoodId(neighborhoodId).size()
        );
        oldNeighborhood.setLatitude(dto.getLatitude());
        oldNeighborhood.setLongitude(dto.getLongitude());

        neighborhoodRepository.save(oldNeighborhood);
    }


    // =========================
    // DELETE NEIGHBORHOOD
    // =========================

    public void deleteNeighborhood(Integer neighborhoodId) {

        Neighborhood neighborhood =
                getNeighborhoodOrThrow(neighborhoodId);

        neighborhoodRepository.delete(neighborhood);
    }


    // =========================
    // NEIGHBORHOOD DASHBOARD BY USER
    // =========================

    public NeighborhoodDashboardDTO getNeighborhoodDashboardByUser(Integer userId) {

        User user = getUserOrThrow(userId);

        if (user.getNeighborhood() == null) {
            throw new ApiException("User is not assigned to a neighborhood");
        }

        Neighborhood neighborhood = user.getNeighborhood();

        List<User> residents =
                userRepository.findByNeighborhoodId(
                        neighborhood.getId()
                );

        NeighborhoodDashboardDTO dto =
                new NeighborhoodDashboardDTO();

        // =========================
        // BASIC INFO
        // =========================

        dto.setNeighborhoodId(neighborhood.getId());
        dto.setNeighborhoodName(neighborhood.getName());
        dto.setCity(neighborhood.getCity());

        // =========================
        // REGISTERED DATA FROM DB
        // =========================

        dto.setRegisteredPopulation(residents.size());

        int registeredMales = 0;
        int registeredFemales = 0;

        int childrenCount = 0;
        int adultsCount = 0;
        int elderlyCount = 0;

        for (User resident : residents) {

            if ("MALE".equalsIgnoreCase(resident.getGender())) {
                registeredMales++;
            } else if ("FEMALE".equalsIgnoreCase(resident.getGender())) {
                registeredFemales++;
            }

            if (resident.getBirthDate() != null) {

                int age =
                        Period.between(
                                resident.getBirthDate(),
                                LocalDate.now()
                        ).getYears();

                if (age < 12) {
                    childrenCount++;
                } else if (age <= 60) {
                    adultsCount++;
                } else {
                    elderlyCount++;
                }
            }
        }

        dto.setRegisteredMales(registeredMales);
        dto.setRegisteredFemales(registeredFemales);

        dto.setChildrenCount(childrenCount);
        dto.setAdultsCount(adultsCount);
        dto.setElderlyCount(elderlyCount);

        // =========================
        // ESTIMATED DATA FROM AI
        // =========================

        setEstimatedPopulationByAI(dto, neighborhood);

        // =========================
        // COUNTS FROM DB
        // =========================

        dto.setEventsCount(
                eventRepository.countByNeighborhood(neighborhood)
        );

        dto.setInitiativesCount(
                initiativeRepository.countByNeighborhood(neighborhood)
        );

        dto.setOpenIssueReportsCount(
                issueReportRepository.countByReportNeighborhoodAndStatus(
                        neighborhood,
                        "OPEN"
                )
        );

        dto.setMarketplaceItemsCount(
                marketPlaceItemRepository.countByUser_Neighborhood(
                        neighborhood
                )
        );

        // =========================
        // LAST ITEMS FROM DB
        // =========================

        dto.setLastEvents(
                eventRepository
                        .findTop3ByNeighborhoodOrderByDateDesc(neighborhood)
                        .stream()
                        .map(this::mapEvent)
                        .toList()
        );

        dto.setLastInitiatives(
                initiativeRepository
                        .findTop3ByNeighborhoodOrderByDateDesc(neighborhood)
                        .stream()
                        .map(this::mapInitiative)
                        .toList()
        );

        dto.setLastIssues(
                issueReportRepository
                        .findTop3ByReportNeighborhoodOrderByCreatedAtDesc(neighborhood)
                        .stream()
                        .map(this::mapIssue)
                        .toList()
        );

        return dto;
    }


    // =========================
    // ESTIMATED POPULATION BY AI
    // =========================

    private void setEstimatedPopulationByAI(
            NeighborhoodDashboardDTO dto,
            Neighborhood neighborhood
    ) {

        String prompt =
                """
                Return ONLY valid JSON.
                No markdown. No explanation.

                Estimate neighborhood population statistics using the neighborhood name and city.

                Neighborhood: %s
                City: %s

                Required JSON format:
                {
                  "estimatedPopulation": number,
                  "estimatedMales": number,
                  "estimatedFemales": number
                }
                """
                        .formatted(
                                neighborhood.getName(),
                                neighborhood.getCity()
                        );

        String aiResponse =
                openAIService.askAI(
                        "You are a smart city population data analyst.",
                        prompt
                );

        try {

            if (aiResponse == null ||
                    aiResponse.isBlank() ||
                    aiResponse.contains("ERROR")) {

                dto.setEstimatedPopulation(null);
                dto.setEstimatedMales(null);
                dto.setEstimatedFemales(null);
                return;
            }

            ObjectMapper mapper =
                    new ObjectMapper();

            JsonNode node =
                    mapper.readTree(aiResponse);

            dto.setEstimatedPopulation(
                    node.has("estimatedPopulation")
                            ? node.get("estimatedPopulation").asInt()
                            : null
            );

            dto.setEstimatedMales(
                    node.has("estimatedMales")
                            ? node.get("estimatedMales").asInt()
                            : null
            );

            dto.setEstimatedFemales(
                    node.has("estimatedFemales")
                            ? node.get("estimatedFemales").asInt()
                            : null
            );

        } catch (Exception e) {

            dto.setEstimatedPopulation(null);
            dto.setEstimatedMales(null);
            dto.setEstimatedFemales(null);
        }
    }


    // يقدّر عدد سكان الحي بالذكاء الاصطناعي اعتماداً على الاسم والمدينة.
    // best-effort: يرجع null عند أي فشل (لا يكسر إنشاء الحي أو جلب القائمة).
    public Integer estimatePopulationByAI(String name, String city) {
        try {
            String prompt = """
                    Return ONLY valid JSON. No markdown. No explanation.
                    Estimate the population for this neighborhood using its name and city.
                    Neighborhood: %s
                    City: %s
                    Required JSON format:
                    {"estimatedPopulation": number}
                    """.formatted(name, city);

            String aiResponse = openAIService.askAI("You are a smart city population data analyst.", prompt);
            if (aiResponse == null || aiResponse.isBlank() || aiResponse.contains("ERROR")) {
                return null;
            }

            String json = aiResponse.trim();
            if (json.startsWith("```")) {
                json = json.replace("```json", "").replace("```", "").trim();
            }

            JsonNode node = new ObjectMapper().readTree(json);
            if (node.has("estimatedPopulation") && !node.get("estimatedPopulation").isNull()) {
                return node.get("estimatedPopulation").asInt();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // =========================
    // HELPERS
    // =========================

    private User getUserOrThrow(Integer userId) {

        User user =
                userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        return user;
    }


    private Neighborhood getNeighborhoodOrThrow(Integer neighborhoodId) {

        Neighborhood neighborhood =
                neighborhoodRepository.findNeighborhoodById(neighborhoodId);

        if (neighborhood == null) {
            throw new ApiException("Neighborhood not found");
        }

        return neighborhood;
    }


    // =========================
    // MAPPERS
    // =========================

    private EventDTO mapEvent(Event event) {

        EventDTO dto =
                new EventDTO();

        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setLocation(event.getLocation());
        dto.setStatus(event.getStatus());
        dto.setIsPaid(event.getIsPaid());
        dto.setPrice(event.getPrice());
        dto.setMaxParticipants(event.getMaxParticipants());

        return dto;
    }


    private InitiativeDTO mapInitiative(Initiative initiative) {

        InitiativeDTO dto =
                new InitiativeDTO();

        dto.setId(initiative.getId());
        dto.setTitle(initiative.getTitle());
        dto.setDescription(initiative.getDescription());
        dto.setDate(initiative.getDate());
        dto.setStatus(initiative.getStatus());
        dto.setMaxParticipants(initiative.getMaxParticipants());
        dto.setCategory(initiative.getCategory());
        dto.setCreatorName(initiative.getCreator() != null
                ? initiative.getCreator().getFullName()
                : initiative.getUser() != null ? initiative.getUser().getFullName() : null);
        dto.setNeighborhoodName(initiative.getNeighborhood() != null ? initiative.getNeighborhood().getName() : null);

        return dto;
    }


    private IssueReportDTO mapIssue(IssueReport issueReport) {

        IssueReportDTO dto =
                new IssueReportDTO();

        dto.setId(issueReport.getId());
        dto.setTitle(issueReport.getTitle());
        dto.setDescription(issueReport.getDescription());
        dto.setCategory(issueReport.getCategory());
        dto.setPriority(issueReport.getPriority());
        dto.setStatus(issueReport.getStatus());
        dto.setCreatedAt(issueReport.getCreatedAt());
        dto.setDetectedDistrictName(issueReport.getDetectedDistrictName());
        dto.setDetectedStreetName(issueReport.getDetectedStreetName());

        return dto;
    }
}
