package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.MarketPlaceItemImageInDTO;
import org.example.menaandfeena_finalproject.Service.MarketPlaceItemImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/marketplace-images")
@RequiredArgsConstructor
public class MarketPlaceItemImageController {
    private final MarketPlaceItemImageService marketPlaceItemImageService;

    @PostMapping("/add")
    public ResponseEntity<?> addMarketPlaceItemImage(@RequestBody @Valid MarketPlaceItemImageInDTO marketPlaceItemImageInDTO) {
        marketPlaceItemImageService.addMarketPlaceItemImage(marketPlaceItemImageInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Market place item image added"));
    }

    @PostMapping("/upload/{marketPlaceItemId}")
    public ResponseEntity<?> uploadProductImage(@PathVariable Integer marketPlaceItemId, @RequestBody @Valid MarketPlaceItemImageInDTO marketPlaceItemImageInDTO) {
        marketPlaceItemImageService.uploadProductImage(marketPlaceItemId, marketPlaceItemImageInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Product image uploaded"));
    }

    @PostMapping(value = "/user/{userId}/product/{marketPlaceItemId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProductImageFile(@PathVariable Integer userId, @PathVariable Integer marketPlaceItemId, @RequestParam("image") MultipartFile image) {
        return ResponseEntity.status(200).body(marketPlaceItemImageService.uploadProductImageFile(userId, marketPlaceItemId, image));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllMarketPlaceItemImages() {
        return ResponseEntity.status(200).body(marketPlaceItemImageService.getAllMarketPlaceItemImages());
    }

    @GetMapping("/product/{marketPlaceItemId}")
    public ResponseEntity<?> getAllImagesOfProduct(@PathVariable Integer marketPlaceItemId) {
        return ResponseEntity.status(200).body(marketPlaceItemImageService.getAllImagesOfProduct(marketPlaceItemId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMarketPlaceItemImage(@PathVariable Integer id, @RequestBody @Valid MarketPlaceItemImageInDTO marketPlaceItemImageInDTO) {
        marketPlaceItemImageService.updateMarketPlaceItemImage(id, marketPlaceItemImageInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Market place item image updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMarketPlaceItemImage(@PathVariable Integer id) {
        marketPlaceItemImageService.deleteMarketPlaceItemImage(id);
        return ResponseEntity.status(200).body(new ApiResponse("Market place item image deleted"));
    }
}
