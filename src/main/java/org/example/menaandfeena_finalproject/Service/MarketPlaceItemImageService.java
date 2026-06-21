package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.MarketPlaceItemImageInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MarketPlaceItemImageOutDTO;
import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.example.menaandfeena_finalproject.Model.MarketPlaceItemImage;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.MarketPlaceItemImageRepository;
import org.example.menaandfeena_finalproject.Repository.MarketPlaceItemRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarketPlaceItemImageService {
    private final MarketPlaceItemImageRepository marketPlaceItemImageRepository;
    private final MarketPlaceItemRepository marketPlaceItemRepository;
    private final UserRepository userRepository;

    // يقرأ مسار مجلد الرفع من application.properties حتى نغيره بسهولة حسب بيئة التشغيل.
    @Value("${app.upload.dir}")
    private String uploadDir;

    public List<MarketPlaceItemImageOutDTO> getAllMarketPlaceItemImages() {
        List<MarketPlaceItemImageOutDTO> imageOutDTOS = new ArrayList<>();

        for (MarketPlaceItemImage image : marketPlaceItemImageRepository.findAll()) {
            Integer marketPlaceItemId = image.getMarketPlaceItem() == null ? null : image.getMarketPlaceItem().getId();
            imageOutDTOS.add(new MarketPlaceItemImageOutDTO(image.getId(), image.getImageUrl(), marketPlaceItemId));
        }

        return imageOutDTOS;
    }

    public void uploadProductImage(Integer marketPlaceItemId, MarketPlaceItemImageInDTO marketPlaceItemImageInDTO) {
        MarketPlaceItem item = marketPlaceItemRepository.findMarketPlaceItemById(marketPlaceItemId);

        if (item == null) {
            throw new ApiException("Market place item not found");
        }

        MarketPlaceItemImage image = new MarketPlaceItemImage();
        image.setImageUrl(marketPlaceItemImageInDTO.getImageUrl());
        image.setMarketPlaceItem(item);
        marketPlaceItemImageRepository.save(image);
    }

    // رفع صور المنتج يحفظ الملفات على السيرفر فقط، ولا نخزن bytes أو blob داخل MySQL.
    // الفرق عن صورة IssueReport أن المنتج يمكن أن يملك معرض صور، لذلك كل رفع ينشئ سجل MarketPlaceItemImage جديد.
    public MarketPlaceItemImageOutDTO uploadProductImageFile(Integer userId, Integer marketPlaceItemId, MultipartFile imageFile) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        MarketPlaceItem item = marketPlaceItemRepository.findMarketPlaceItemById(marketPlaceItemId);
        if (item == null) {
            throw new ApiException("Market place item not found");
        }
        if (item.getUser() == null || item.getUser().getNeighborhood() == null) {
            throw new ApiException("Market place item owner neighborhood is required");
        }
        if (!item.getUser().getId().equals(user.getId())) {
            throw new ApiException("Marketplace item does not belong to this user");
        }
        // نتحقق من الملف قبل الحفظ: لا يكون فارغاً، لا يتجاوز 5MB، ونقبل فقط jpeg/png/webp.
        if (imageFile == null || imageFile.isEmpty()) {
            throw new ApiException("Image file cannot be empty");
        }
        if (imageFile.getSize() > 5 * 1024 * 1024) {
            throw new ApiException("Image file size must not exceed 5MB");
        }

        String contentType = imageFile.getContentType();
        String extension;
        if ("image/jpeg".equals(contentType)) {
            extension = "jpg";
        } else if ("image/png".equals(contentType)) {
            extension = "png";
        } else if ("image/webp".equals(contentType)) {
            extension = "webp";
        } else {
            throw new ApiException("Image content type must be image/jpeg, image/png, or image/webp");
        }

        try {
            // نحفظ الملف داخل uploads/marketplace-items ثم نخزن imageUrl فقط في جدول صور المنتجات.
            Path marketplaceUploadDir = Paths.get(uploadDir, "marketplace-items").toAbsolutePath().normalize();
            Files.createDirectories(marketplaceUploadDir);
            String filename = "marketplace-item-" + marketPlaceItemId + "-" + UUID.randomUUID() + "." + extension;
            Path filePath = marketplaceUploadDir.resolve(filename).normalize();
            Files.copy(imageFile.getInputStream(), filePath);

            MarketPlaceItemImage image = new MarketPlaceItemImage();
            image.setImageUrl("/uploads/marketplace-items/" + filename);
            image.setMarketPlaceItem(item);
            MarketPlaceItemImage savedImage = marketPlaceItemImageRepository.save(image);

            return new MarketPlaceItemImageOutDTO(savedImage.getId(), savedImage.getImageUrl(), item.getId());
        } catch (IOException e) {
            throw new ApiException("Could not upload marketplace item image");
        }
    }

    public void addMarketPlaceItemImage(MarketPlaceItemImageInDTO marketPlaceItemImageInDTO) {
        if (marketPlaceItemImageInDTO.getMarketPlaceItemId() == null) {
            throw new ApiException("Market place item id cannot be null");
        }

        uploadProductImage(marketPlaceItemImageInDTO.getMarketPlaceItemId(), marketPlaceItemImageInDTO);
    }

    public void updateMarketPlaceItemImage(Integer id, MarketPlaceItemImageInDTO marketPlaceItemImageInDTO) {
        MarketPlaceItemImage image = marketPlaceItemImageRepository.findMarketPlaceItemImageById(id);
        if (image == null) {
            throw new ApiException("Market place item image not found");
        }

        MarketPlaceItem item = marketPlaceItemRepository.findMarketPlaceItemById(marketPlaceItemImageInDTO.getMarketPlaceItemId());
        if (item == null) {
            throw new ApiException("Market place item not found");
        }

        image.setImageUrl(marketPlaceItemImageInDTO.getImageUrl());
        image.setMarketPlaceItem(item);
        marketPlaceItemImageRepository.save(image);
    }

    public void deleteMarketPlaceItemImage(Integer id) {
        MarketPlaceItemImage image = marketPlaceItemImageRepository.findMarketPlaceItemImageById(id);
        if (image == null) {
            throw new ApiException("Market place item image not found");
        }

        marketPlaceItemImageRepository.delete(image);
    }

    public List<MarketPlaceItemImageOutDTO> getAllImagesOfProduct(Integer marketPlaceItemId) {
        if (marketPlaceItemRepository.findMarketPlaceItemById(marketPlaceItemId) == null) {
            throw new ApiException("Market place item not found");
        }

        List<MarketPlaceItemImageOutDTO> imageOutDTOS = new ArrayList<>();
        for (MarketPlaceItemImage image : marketPlaceItemImageRepository.findMarketPlaceItemImagesByMarketPlaceItemId(marketPlaceItemId)) {
            Integer imageMarketPlaceItemId = image.getMarketPlaceItem() == null ? null : image.getMarketPlaceItem().getId();
            imageOutDTOS.add(new MarketPlaceItemImageOutDTO(image.getId(), image.getImageUrl(), imageMarketPlaceItemId));
        }

        return imageOutDTOS;
    }
}
