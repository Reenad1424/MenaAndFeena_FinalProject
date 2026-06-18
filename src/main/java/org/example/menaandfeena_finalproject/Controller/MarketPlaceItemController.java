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

    @GetMapping("/get")
    public ResponseEntity<?> getAllMarketPlaceItems() {
        return ResponseEntity.status(200).body(marketPlaceItemService.getAllMarketPlaceItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMarketPlaceItemById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMarketPlaceItemById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getMarketPlaceItemsByType(@PathVariable String type) {
        return ResponseEntity.status(200).body(marketPlaceItemService.getMarketPlaceItemsByType(type));
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMarketPlaceItem(@PathVariable Integer id) {
        marketPlaceItemService.deleteMarketPlaceItem(id);
        return ResponseEntity.status(200).body(new ApiResponse("Market place item deleted"));
    }
}
