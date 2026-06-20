package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.Out.LandmarkDashboardDto;
import org.example.menaandfeena_finalproject.DTO.Out.LandmarkResponseDto;
import org.example.menaandfeena_finalproject.Model.Landmark;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.LandmarkRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LandmarkService {

    private final LandmarkRepository landmarkRepository;
    private final UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();


    //Reenad
    // =========================
    // GET ALL LANDMARKS
    // =========================

    public List<Landmark> getAllLandmarks() {
        return landmarkRepository.findAll();
    }


    // =========================
    // CREATE LANDMARK
    // =========================

    public void createLandmark(Landmark landmark) {
        landmarkRepository.save(landmark);
    }


    // =========================
    // UPDATE LANDMARK
    // =========================

    public void updateLandmark(Integer landmarkId, Landmark landmark) {

        Landmark oldLandmark = getLandmarkOrThrow(landmarkId);

        oldLandmark.setName(landmark.getName());
        oldLandmark.setType(landmark.getType());
        oldLandmark.setLatitude(landmark.getLatitude());
        oldLandmark.setLongitude(landmark.getLongitude());
        oldLandmark.setNeighborhood(landmark.getNeighborhood());

        landmarkRepository.save(oldLandmark);
    }


    // =========================
    // DELETE LANDMARK
    // =========================

    public void deleteLandmark(Integer landmarkId) {

        Landmark landmark = getLandmarkOrThrow(landmarkId);

        landmarkRepository.delete(landmark);
    }


    // =========================
    // SYNC LANDMARKS FOR USER NEIGHBORHOOD
    // =========================

    public void syncLandmarksForUser(Integer userId, Integer radius) {

        User user = getUserOrThrow(userId);

        validateUserLocation(user);
        validateUserNeighborhood(user);
        validateRadius(radius);

        syncOneTypeForUser(user, "MOSQUE", radius);
        syncOneTypeForUser(user, "SCHOOL", radius);
        syncOneTypeForUser(user, "PARK", radius);
    }
    private void syncOneTypeForUser(
            User user,
            String type,
            Integer radius
    ) {

        int currentRadius = radius;

        while (currentRadius <= 15000) {

            if (hasLandmarkForNeighborhood(user, type)) {
                return;
            }

            List<Map<String, Object>> elements =
                    fetchElementsFromOverpass(
                            user.getLatitude(),
                            user.getLongitude(),
                            currentRadius,
                            type
                    );

            saveElementsForUserNeighborhood(
                    user,
                    elements,
                    type
            );

            if (hasLandmarkForNeighborhood(user, type)) {
                return;
            }

            currentRadius += 3000;
        }
    }
    private boolean hasLandmarkForNeighborhood(
            User user,
            String type
    ) {
        List<Landmark> landmarks =
                landmarkRepository.findByNeighborhoodAndType(
                        user.getNeighborhood(),
                        type
                );

        return landmarks != null && !landmarks.isEmpty();
    }
    private List<Map<String, Object>> fetchElementsFromOverpass(
            Double lat,
            Double lon,
            Integer radius,
            String type
    ) {

        String query = buildOverpassQueryByType(
                lat,
                lon,
                radius,
                type
        );

        String url = "https://overpass-api.de/api/interpreter";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("data", query);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        url,
                        request,
                        Map.class
                );

        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null ||
                responseBody.get("elements") == null) {
            return new ArrayList<>();
        }

        return (List<Map<String, Object>>) responseBody.get("elements");
    }
    private void saveElementsForUserNeighborhood(
            User user,
            List<Map<String, Object>> elements,
            String expectedType
    ) {

        for (Map<String, Object> element : elements) {

            Map<String, Object> tags =
                    (Map<String, Object>) element.get("tags");

            if (tags == null || tags.get("name") == null) {
                continue;
            }

            String name = tags.get("name").toString();

            String type = mapType(tags);

            if (!expectedType.equals(type)) {
                continue;
            }

            Double landmarkLat;
            Double landmarkLon;

            if (element.get("lat") != null && element.get("lon") != null) {

                landmarkLat =
                        Double.valueOf(element.get("lat").toString());

                landmarkLon =
                        Double.valueOf(element.get("lon").toString());

            } else if (element.get("center") != null) {

                Map<String, Object> center =
                        (Map<String, Object>) element.get("center");

                if (center.get("lat") == null || center.get("lon") == null) {
                    continue;
                }

                landmarkLat =
                        Double.valueOf(center.get("lat").toString());

                landmarkLon =
                        Double.valueOf(center.get("lon").toString());

            } else {
                continue;
            }

            boolean exists =
                    landmarkRepository.existsByNameAndTypeAndNeighborhoodId(
                            name,
                            type,
                            user.getNeighborhood().getId()
                    );

            if (exists) {
                continue;
            }

            Landmark landmark = new Landmark();

            landmark.setName(name);
            landmark.setType(type);
            landmark.setLatitude(landmarkLat);
            landmark.setLongitude(landmarkLon);
            landmark.setNeighborhood(user.getNeighborhood());

            landmarkRepository.save(landmark);
        }
    }
    private String buildOverpassQueryByType(
            Double lat,
            Double lon,
            Integer radius,
            String type
    ) {

        return switch (type) {
            case "MOSQUE" ->
                    "[out:json][timeout:25];("
                            + "node[\"amenity\"=\"mosque\"](around:" + radius + "," + lat + "," + lon + ");"
                            + "way[\"amenity\"=\"mosque\"](around:" + radius + "," + lat + "," + lon + ");"
                            + "node[\"amenity\"=\"place_of_worship\"][\"religion\"=\"muslim\"](around:" + radius + "," + lat + "," + lon + ");"
                            + "way[\"amenity\"=\"place_of_worship\"][\"religion\"=\"muslim\"](around:" + radius + "," + lat + "," + lon + ");"
                            + ");out center;";
            case "SCHOOL" ->
                    "[out:json][timeout:25];("
                            + "node[\"amenity\"=\"school\"](around:" + radius + "," + lat + "," + lon + ");"
                            + "way[\"amenity\"=\"school\"](around:" + radius + "," + lat + "," + lon + ");"
                            + ");out center;";

            case "PARK" ->
                    "[out:json][timeout:25];("
                            + "node[\"leisure\"=\"park\"](around:" + radius + "," + lat + "," + lon + ");"
                            + "way[\"leisure\"=\"park\"](around:" + radius + "," + lat + "," + lon + ");"
                            + ");out center;";

            default ->
                    throw new ApiException("نوع المعلم غير صحيح");
        };
    }


    // =========================
    // GET NEARBY LANDMARKS FOR USER NEIGHBORHOOD
    // =========================

    public List<LandmarkResponseDto> getNearbyLandmarksForUser(Integer userId) {

        User user = getUserOrThrow(userId);

        validateUserLocation(user);
        validateUserNeighborhood(user);

        List<Landmark> landmarks =
                landmarkRepository.findByNeighborhood(
                        user.getNeighborhood()
                );

        List<LandmarkResponseDto> result = new ArrayList<>();

        for (Landmark landmark : landmarks) {

            if (landmark.getLatitude() == null ||
                    landmark.getLongitude() == null) {
                continue;
            }

            long distance =
                    Math.round(
                            calculateDistance(
                                    user.getLatitude(),
                                    user.getLongitude(),
                                    landmark.getLatitude(),
                                    landmark.getLongitude()
                            )
                    );

            result.add(
                    new LandmarkResponseDto(
                            landmark.getId(),
                            landmark.getName(),
                            convertType(landmark.getType()),
                            distance
                    )
            );
        }

        result.sort(
                Comparator.comparingLong(
                        LandmarkResponseDto::getDistanceMeters
                )
        );

        return result;
    }


    // =========================
    // GET LANDMARK DASHBOARD FOR USER
    // =========================

    public LandmarkDashboardDto getLandmarkDashboard(Integer userId) {

        User user = getUserOrThrow(userId);

        validateUserLocation(user);
        validateUserNeighborhood(user);

        LandmarkDashboardDto.LandmarkItem nearestMosque =
                getClosestLandmarkItemByType(user, "MOSQUE");

        LandmarkDashboardDto.LandmarkItem nearestSchool =
                getClosestLandmarkItemByType(user, "SCHOOL");

        LandmarkDashboardDto.LandmarkItem nearestPark =
                getClosestLandmarkItemByType(user, "PARK");

        return new LandmarkDashboardDto(
                user.getId(),
                user.getNeighborhood().getName(),
                nearestMosque,
                nearestSchool,
                nearestPark
        );
    }


    // =========================
    // HELPERS
    // =========================

    private User getUserOrThrow(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("المستخدم غير موجود");
        }

        return user;
    }


    private Landmark getLandmarkOrThrow(Integer landmarkId) {

        Landmark landmark =
                landmarkRepository.findLandmarkById(landmarkId);

        if (landmark == null) {
            throw new ApiException("Landmark not found");
        }

        return landmark;
    }


    private void validateUserLocation(User user) {

        if (user.getLatitude() == null ||
                user.getLongitude() == null) {
            throw new ApiException("موقع المستخدم غير موجود");
        }
    }


    private void validateUserNeighborhood(User user) {

        if (user.getNeighborhood() == null) {
            throw new ApiException("المستخدم غير مرتبط بحي");
        }
    }


    private void validateRadius(Integer radius) {

        if (radius == null || radius <= 0) {
            throw new ApiException("نطاق البحث غير صحيح");
        }
    }

    private LandmarkDashboardDto.LandmarkItem getClosestLandmarkItemByType(
            User user,
            String type
    ) {

        List<Landmark> landmarks =
                landmarkRepository.findByNeighborhoodAndType(
                        user.getNeighborhood(),
                        type
                );

        if (landmarks == null || landmarks.isEmpty()) {
            return null;
        }

        Landmark closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Landmark landmark : landmarks) {

            if (landmark.getLatitude() == null ||
                    landmark.getLongitude() == null) {
                continue;
            }

            double distance =
                    calculateDistance(
                            user.getLatitude(),
                            user.getLongitude(),
                            landmark.getLatitude(),
                            landmark.getLongitude()
                    );

            if (distance < minDistance) {
                minDistance = distance;
                closest = landmark;
            }
        }

        if (closest == null) {
            return null;
        }

        return new LandmarkDashboardDto.LandmarkItem(
                closest.getName(),
                Math.round(minDistance)
        );
    }


    private String mapType(Map<String, Object> tags) {

        if (tags == null) {
            return "OTHER";
        }

        String amenity = String.valueOf(tags.get("amenity"));
        String religion = String.valueOf(tags.get("religion"));

        if ("mosque".equalsIgnoreCase(amenity)) {
            return "MOSQUE";
        }

        if ("place_of_worship".equalsIgnoreCase(amenity)
                && "muslim".equalsIgnoreCase(religion)) {
            return "MOSQUE";
        }

        if ("school".equalsIgnoreCase(amenity)) {
            return "SCHOOL";
        }

        if ("park".equalsIgnoreCase(String.valueOf(tags.get("leisure")))) {
            return "PARK";
        }

        return "OTHER";
    }


    private String convertType(String type) {

        if (type == null) {
            return null;
        }

        return switch (type.toUpperCase()) {
            case "MOSQUE" -> "مسجد";
            case "SCHOOL" -> "مدرسة";
            case "PARK" -> "حديقة";
            case "HOSPITAL" -> "مستشفى";
            default -> "معلم";
        };
    }


    private double calculateDistance(
            Double lat1,
            Double lon1,
            Double lat2,
            Double lon2
    ) {

        double earthRadius = 6371000;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return earthRadius * c;
    }
}