package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.MarketPlaceItemInDTO;
import org.example.menaandfeena_finalproject.Service.MarketPlaceItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/marketplace")
@RequiredArgsConstructor
public class MarketPlaceItemController {

    private final MarketPlaceItemService marketPlaceItemService;

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addMarketPlaceItem(@PathVariable Integer userId, @RequestBody @Valid MarketPlaceItemInDTO marketPlaceItemInDTO) {
        marketPlaceItemService.addMarketPlaceItem(userId, marketPlaceItemInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Market place item added"));
    }

    // TODO SECURITY: ADMIN/DEBUG general listing. User-facing listing should use /user/{userId}/get for neighborhood isolation.
    @GetMapping("/get")
    public ResponseEntity<?> getAllMarketPlaceItems() {
        return ResponseEntity.status(200).body(marketPlaceItemService.getAllMarketPlaceItems());
    }

    @GetMapping("/user/{userId}/get")
    public ResponseEntity<?> getMarketPlaceItemsForUser(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMarketPlaceItemsForUser(userId));
    }

    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<?> getPersonalizedRecommendations(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getPersonalizedRecommendations(userId));
    }

    // TODO SECURITY: ADMIN/DEBUG product details. User-facing details should use /user/{userId}/item/{id}.
    @GetMapping("/{id}")
    public ResponseEntity<?> getMarketPlaceItemById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMarketPlaceItemById(id));
    }

    @GetMapping("/user/{userId}/item/{id}")
    public ResponseEntity<?> getMarketPlaceItemByIdForUser(@PathVariable Integer userId, @PathVariable Integer id) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMarketPlaceItemByIdForUser(id, userId));
    }

    @GetMapping("/{marketPlaceItemId}/similar/{userId}")
    public ResponseEntity<?> getSimilarProducts(@PathVariable Integer marketPlaceItemId, @PathVariable Integer userId) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getSimilarProducts(marketPlaceItemId, userId));
    }

    // TODO SECURITY: ADMIN/DEBUG type filter. User-facing filter should use /user/{userId}/type/{type}.
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getMarketPlaceItemsByType(@PathVariable String type) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMarketPlaceItemsByType(type));
    }

    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<?> getMarketPlaceItemsByTypeForUser(@PathVariable Integer userId, @PathVariable String type) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMarketPlaceItemsByTypeForUser(type, userId));
    }

    @GetMapping("/user/{userId}/search")
    public ResponseEntity<?> searchMarketPlaceItemsForUser(@PathVariable Integer userId, @RequestParam String keyword) {
        return ResponseEntity.status(200).body(marketPlaceItemService.searchMarketPlaceItemsForUser(userId, keyword));
    }

    @GetMapping("/my-items/{userId}")
    public ResponseEntity<?> getMyMarketPlaceItems(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMyMarketPlaceItems(userId));
    }

    @PutMapping("/update/{id}/{userId}")
    public ResponseEntity<?> updateMarketPlaceItem(@PathVariable Integer id, @PathVariable Integer userId, @RequestBody @Valid MarketPlaceItemInDTO marketPlaceItemInDTO) {
        marketPlaceItemService.updateMarketPlaceItem(id, userId, marketPlaceItemInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Market place item updated"));
    }

    @DeleteMapping("/delete/{id}/{userId}")
    public ResponseEntity<?> deleteMarketPlaceItem(@PathVariable Integer id, @PathVariable Integer userId) {
        marketPlaceItemService.deleteMarketPlaceItem(id, userId);
        return ResponseEntity.status(200).body(new ApiResponse("Market place item deleted"));
    }
}
