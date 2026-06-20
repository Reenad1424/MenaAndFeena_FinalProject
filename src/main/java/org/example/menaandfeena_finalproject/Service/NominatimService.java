package org.example.menaandfeena_finalproject.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NominatimService {

    @Value("${nominatim.reverse.url:https://nominatim.openstreetmap.org/reverse}")
    private String nominatimReverseUrl;

    public Map<String, String> detectLocationFromCoordinates(Double latitude, Double longitude) {
        Map<String, String> detectedLocation = new HashMap<>();
        if (latitude == null || longitude == null) {
            return detectedLocation;
        }

        try {
            String url = nominatimReverseUrl
                    + "?format=jsonv2"
                    + "&lat=" + latitude
                    + "&lon=" + longitude
                    + "&addressdetails=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "MenaAndFeena/1.0");

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
            if (response.getBody() == null || response.getBody().get("address") == null) {
                return detectedLocation;
            }

            Map address = (Map) response.getBody().get("address");
            String street = firstAddressValue(address, "road", "pedestrian", "footway", "residential", "neighbourhood");
            String district = firstAddressValue(address, "suburb", "city_district", "district", "neighbourhood", "quarter");

            if (street != null && !street.isBlank()) {
                detectedLocation.put("street", street);
            }
            if (district != null && !district.isBlank()) {
                detectedLocation.put("district", district);
            }
        } catch (Exception e) {
            return detectedLocation;
        }

        return detectedLocation;
    }

    private String firstAddressValue(Map address, String... keys) {
        for (String key : keys) {
            Object value = address.get(key);
            if (value != null && !value.toString().isBlank()) {
                return value.toString();
            }
        }
        return null;
    }
}
