package org.example.menaandfeena_finalproject.Controller;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.MarketPlaceItemInDTO;
import org.example.menaandfeena_finalproject.Service.MarketPlaceItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/market-place-item")
@RequiredArgsConstructor
public class MarketPlaceItemController {

    private final MarketPlaceItemService marketPlaceItemService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllMarketPlaceItems() {
        return ResponseEntity.status(200).body(marketPlaceItemService.getAllMarketPlaceItems());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMarketPlaceItem(@RequestBody MarketPlaceItemInDTO marketPlaceItemInDTO) {
        marketPlaceItemService.addMarketPlaceItem(marketPlaceItemInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Market place item added"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMarketPlaceItem(@PathVariable Integer id, @RequestBody MarketPlaceItemInDTO marketPlaceItemInDTO) {
        marketPlaceItemService.updateMarketPlaceItem(id, marketPlaceItemInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Market place item updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMarketPlaceItem(@PathVariable Integer id) {
        marketPlaceItemService.deleteMarketPlaceItem(id);
        return ResponseEntity.status(200).body(new ApiResponse("Market place item deleted"));
    }
}
