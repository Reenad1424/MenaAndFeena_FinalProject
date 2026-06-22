package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.MarketPlaceItemInDTO;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Service.MarketPlaceItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/marketplace")
@RequiredArgsConstructor
public class MarketPlaceItemController {

    private final MarketPlaceItemService marketPlaceItemService;

    @PostMapping("/add")
    public ResponseEntity<?> addMarketPlaceItem(@AuthenticationPrincipal User user,
                                                @RequestBody @Valid MarketPlaceItemInDTO marketPlaceItemInDTO) {
        marketPlaceItemService.addMarketPlaceItem(user.getId(), marketPlaceItemInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Market place item added"));
    }

    // TODO SECURITY: ADMIN/DEBUG general listing. User-facing listing should use /user/get for neighborhood isolation.
    @GetMapping("/get")
    public ResponseEntity<?> getAllMarketPlaceItems() {
        return ResponseEntity.status(200).body(marketPlaceItemService.getAllMarketPlaceItems());
    }

    @GetMapping("/user/get")
    public ResponseEntity<?> getMarketPlaceItemsForUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMarketPlaceItemsForUser(user.getId()));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getPersonalizedRecommendations(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getPersonalizedRecommendations(user.getId()));
    }

    // TODO SECURITY: ADMIN/DEBUG product details. User-facing details should use /user/item/{id}.
    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getMarketPlaceItemById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMarketPlaceItemById(id));
    }

    @GetMapping("/user/item/{id}")
    public ResponseEntity<?> getMarketPlaceItemByIdForUser(@PathVariable Integer id,
                                                           @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMarketPlaceItemByIdForUser(id, user.getId()));
    }

    @GetMapping("/{marketPlaceItemId}/similar")
    public ResponseEntity<?> getSimilarProducts(@PathVariable Integer marketPlaceItemId,
                                                @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getSimilarProducts(marketPlaceItemId, user.getId()));
    }

    // TODO SECURITY: ADMIN/DEBUG type filter. User-facing filter should use /user/type/{type}.
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getMarketPlaceItemsByType(@PathVariable String type) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMarketPlaceItemsByType(type));
    }

    @GetMapping("/user/type/{type}")
    public ResponseEntity<?> getMarketPlaceItemsByTypeForUser(@PathVariable String type,
                                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMarketPlaceItemsByTypeForUser(type, user.getId()));
    }

    @GetMapping("/user/search")
    public ResponseEntity<?> searchMarketPlaceItemsForUser(@RequestParam String keyword,
                                                           @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(marketPlaceItemService.searchMarketPlaceItemsForUser(user.getId(), keyword));
    }

    @GetMapping("/my-items")
    public ResponseEntity<?> getMyMarketPlaceItems(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMyMarketPlaceItems(user.getId()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMarketPlaceItem(@PathVariable Integer id,
                                                   @AuthenticationPrincipal User user,
                                                   @RequestBody @Valid MarketPlaceItemInDTO marketPlaceItemInDTO) {
        marketPlaceItemService.updateMarketPlaceItem(id, user.getId(), marketPlaceItemInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Market place item updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMarketPlaceItem(@PathVariable Integer id,
                                                   @AuthenticationPrincipal User user) {
        marketPlaceItemService.deleteMarketPlaceItem(id, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Market place item deleted"));
    }
}
