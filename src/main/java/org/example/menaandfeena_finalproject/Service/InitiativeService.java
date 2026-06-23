package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.InitiativeInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.InitiativeDTO;
import org.example.menaandfeena_finalproject.Model.Initiative;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.InitiativeRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InitiativeService {
    private final InitiativeRepository initiativeRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;
    public List<InitiativeDTO> getAllInitiatives() {
        return mapInitiativesToDTO(initiativeRepository.findAll());
    }

    public void addInitiative(InitiativeInDTO initiativeInDTO) {
        Initiative initiative = new Initiative();
        initiative.setTitle(initiativeInDTO.getTitle());
        initiative.setDescription(initiativeInDTO.getDescription());
        initiative.setDate(initiativeInDTO.getDate());
        initiative.setMaxParticipants(initiativeInDTO.getMaxParticipants());
        initiative.setCategory(initiativeInDTO.getCategory());
        initiative.setStatus("ACTIVE");

        initiativeRepository.save(initiative);
    }

    public void updateInitiative(Integer id, InitiativeInDTO initiativeInDTO) {
        Initiative oldInitiative = initiativeRepository.findInitiativeById(id);

        if (oldInitiative == null) {
            throw new ApiException("Initiative not found");
        }

        oldInitiative.setTitle(initiativeInDTO.getTitle());
        oldInitiative.setDescription(initiativeInDTO.getDescription());
        oldInitiative.setDate(initiativeInDTO.getDate());
        oldInitiative.setMaxParticipants(initiativeInDTO.getMaxParticipants());
        oldInitiative.setCategory(initiativeInDTO.getCategory());

        initiativeRepository.save(oldInitiative);
    }

    public void deleteInitiative(Integer id) {
        Initiative initiative = initiativeRepository.findInitiativeById(id);

        if (initiative == null) {
            throw new ApiException("Initiative not found");
        }

        initiativeRepository.delete(initiative);
    }


// Walaa
    public List<InitiativeDTO> getInitiativesByCategory(String category) {
        return mapInitiativesToDTO(initiativeRepository.findInitiativesByCategory(category));
    }

// Walaa
public List<InitiativeDTO> getUpcomingInitiatives() {
    return mapInitiativesToDTO(initiativeRepository.findInitiativesByDateAfter(LocalDate.now()));
}

// Walaa
public InitiativeDTO getInitiativeById(Integer id) {
    Initiative initiative = initiativeRepository.findInitiativeById(id);
    if (initiative == null) {
        throw new ApiException("Initiative not found");
    }
    return mapToDTO(initiative);

}



// Walaa
public void createInitiative(Integer userId, InitiativeInDTO initiativeInDTO) {

    User user = userRepository.findUserById(userId);

    if (user == null) {
        throw new ApiException("User not found");
    }

    Initiative initiative = new Initiative();
    initiative.setTitle(initiativeInDTO.getTitle());
    initiative.setDescription(initiativeInDTO.getDescription());
    initiative.setDate(initiativeInDTO.getDate());
    initiative.setMaxParticipants(initiativeInDTO.getMaxParticipants());
    initiative.setCategory(initiativeInDTO.getCategory());
    initiative.setUser(user);
    // المنشئ والحي يؤخذان من المستخدم حتى لا يظهرا null في الرد.
    initiative.setCreator(user);
    initiative.setNeighborhood(user.getNeighborhood());
    initiative.setStatus("ACTIVE");

    initiativeRepository.save(initiative);
}

    // Uploads a single cover image for an initiative. The file is stored on disk under
    // uploads/initiatives/ and only its URL is saved on the Initiative record.
    public InitiativeDTO uploadInitiativeImage(Integer userId, Integer initiativeId, MultipartFile image) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }

        Initiative initiative = initiativeRepository.findInitiativeById(initiativeId);
        if (initiative == null) {
            throw new ApiException("Initiative not found");
        }
        if (initiative.getUser() == null || !initiative.getUser().getId().equals(userId)) {
            throw new ApiException("Only the initiative owner can upload an image");
        }

        if (image == null || image.isEmpty()) {
            throw new ApiException("Image file cannot be empty");
        }
        if (image.getSize() > 5 * 1024 * 1024) {
            throw new ApiException("Image file size must not exceed 5MB");
        }

        String contentType = image.getContentType();
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
            Path initiativeUploadDir = Paths.get(uploadDir, "initiatives").toAbsolutePath().normalize();
            Files.createDirectories(initiativeUploadDir);
            String filename = "initiative-" + initiativeId + "-" + UUID.randomUUID() + "." + extension;
            Path filePath = initiativeUploadDir.resolve(filename).normalize();
            Files.copy(image.getInputStream(), filePath);

            initiative.setImageUrl("/uploads/initiatives/" + filename);
            return mapToDTO(initiativeRepository.save(initiative));
        } catch (IOException e) {
            throw new ApiException("Could not upload initiative image");
        }
    }

    private List<InitiativeDTO> mapInitiativesToDTO(List<Initiative> initiatives) {
        List<InitiativeDTO> dtos = new ArrayList<>();
        for (Initiative initiative : initiatives) {
            dtos.add(mapToDTO(initiative));
        }
        return dtos;
    }

    private InitiativeDTO mapToDTO(Initiative initiative) {
        User creator = initiative.getCreator() != null ? initiative.getCreator() : initiative.getUser();

        return new InitiativeDTO(
                initiative.getId(),
                initiative.getTitle(),
                initiative.getDescription(),
                initiative.getDate(),
                initiative.getMaxParticipants(),
                initiative.getStatus(),
                initiative.getCategory(),
                creator != null ? creator.getFullName() : null,
                initiative.getNeighborhood() != null ? initiative.getNeighborhood().getName() : null
        );
    }


}
