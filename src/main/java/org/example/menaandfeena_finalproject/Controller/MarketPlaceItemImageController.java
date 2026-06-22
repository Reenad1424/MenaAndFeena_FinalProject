package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.MarketPlaceItemImageInDTO;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Service.MarketPlaceItemImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/marketplace-images")
@RequiredArgsConstructor
public class MarketPlaceItemImageController {
    private final MarketPlaceItemImageService marketPlaceItemImageService;


    @PostMapping(value = "/product/{marketPlaceItemId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProductImageFile(@PathVariable Integer marketPlaceItemId,
                                                    @RequestParam("image") MultipartFile image,
                                                    @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(marketPlaceItemImageService.uploadProductImageFile(user.getId(), marketPlaceItemId, image));
    }

    // TODO SECURITY: ADMIN/DEBUG general listing.
    @GetMapping("/get")
    public ResponseEntity<?> getAllMarketPlaceItemImages() {
        return ResponseEntity.status(200).body(marketPlaceItemImageService.getAllMarketPlaceItemImages());
    }

    @GetMapping("/product/{marketPlaceItemId}")
    public ResponseEntity<?> getAllImagesOfProduct(@PathVariable Integer marketPlaceItemId) {
        return ResponseEntity.status(200).body(marketPlaceItemImageService.getAllImagesOfProduct(marketPlaceItemId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMarketPlaceItemImage(@PathVariable Integer id,
                                                        @AuthenticationPrincipal User user,
                                                        @RequestBody @Valid MarketPlaceItemImageInDTO marketPlaceItemImageInDTO) {
        marketPlaceItemImageService.updateMarketPlaceItemImage(id, user.getId(), marketPlaceItemImageInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Market place item image updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMarketPlaceItemImage(@PathVariable Integer id,
                                                        @AuthenticationPrincipal User user) {
        marketPlaceItemImageService.deleteMarketPlaceItemImage(id, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Market place item image deleted"));
    }
}
