package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.AnnouncementInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.AnnouncementOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.PublisherContactOutDTO;
import org.example.menaandfeena_finalproject.Model.Announcement;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.AnnouncementRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;
    private final WhatsAppService whatsAppService;

    public List<AnnouncementOutDTO> getAllAnnouncements() {

        return announcementRepository.findAll()
                .stream()
                .map(this::convertToOutDTO)
                .toList();
    }

    public List<AnnouncementOutDTO> getMyAnnouncements(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }

        List<AnnouncementOutDTO> myAnnouncements = new ArrayList<>();
        for (Announcement announcement : announcementRepository.findByUserId(userId)) {
            myAnnouncements.add(convertToOutDTO(announcement));
        }
        return myAnnouncements;
    }

    public void updateAnnouncement(Integer id, Integer userId, AnnouncementInDTO announcementInDTO) {

        Announcement oldAnnouncement = announcementRepository.findAnnouncementById(id);

        if (oldAnnouncement == null) {
            throw new ApiException("Announcement not found");
        }
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (oldAnnouncement.getUser() == null || !oldAnnouncement.getUser().getId().equals(userId)) {
            throw new ApiException("Only the announcement owner can update announcement");
        }
        if (oldAnnouncement.getCreatedAt() == null) {
            throw new ApiException("Announcement creation date is missing");
        }
        if (oldAnnouncement.getCreatedAt().isBefore(LocalDate.now().minusDays(2))) {
            throw new ApiException("Announcement can only be updated within 2 days of posting");
        }

        oldAnnouncement.setTitle(announcementInDTO.getTitle());
        oldAnnouncement.setContent(announcementInDTO.getContent());
        announcementRepository.save(oldAnnouncement);
    }

    public void deleteAnnouncement(Integer id, Integer userId) {

        Announcement announcement = announcementRepository.findAnnouncementById(id);

        if (announcement == null) {
            throw new ApiException("Announcement not found");
        }
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (announcement.getUser() == null || !announcement.getUser().getId().equals(userId)) {
            throw new ApiException("Only the announcement owner can delete announcement");
        }

        announcementRepository.delete(announcement);
    }

    public void createAnnouncement(Integer userId, AnnouncementInDTO announcementInDTO) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        Announcement announcement = new Announcement();

        announcement.setTitle(announcementInDTO.getTitle());
        announcement.setContent(announcementInDTO.getContent());
        announcement.setUser(user);
        announcement.setStatus("PENDING");
        announcement.setCreatedAt(LocalDate.now());

        announcementRepository.save(announcement);
    }

    public void moderateAnnouncement(Integer announcementId, Integer userId) {

        Announcement announcement = announcementRepository.findAnnouncementById(announcementId);

        if (announcement == null) {
            throw new ApiException("Announcement not found");
        }
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (announcement.getUser() == null || !announcement.getUser().getId().equals(userId)) {
            throw new ApiException("Only the announcement owner can moderate announcement");
        }
        if (!"PENDING".equals(announcement.getStatus())) {
            throw new ApiException("Only pending announcements can be moderated");
        }

        String aiResult = openAIService.askAI(
                "You are a moderation system. Return only REJECTED if the text contains drugs, narcotics, weapons, scams, fraud, insults, offensive language, adult content, or prohibited sales. Return only APPROVED otherwise.",
                "Title: " + announcement.getTitle() +
                        "\nContent: " + announcement.getContent()
        );

        String moderationStatus = aiResult == null ? "" : aiResult.trim().toUpperCase();
        if (!moderationStatus.equals("APPROVED") && !moderationStatus.equals("REJECTED")) {
            throw new ApiException("AI moderation failed. Please try again later");
        }

        announcement.setStatus(moderationStatus);
        announcementRepository.save(announcement);

        if (moderationStatus.equals("APPROVED")) {

            String importanceResult = openAIService.askAI(
                    "You are an importance detection system for a neighborhood platform. " +
                            "Return only IMPORTANT if the announcement is urgent or important for all neighborhood residents, " +
                            "such as missing person, safety warning, fire, water outage, electricity outage, road closure, emergency, or public health alert. " +
                            "Return only NORMAL otherwise.",
                    "Title: " + announcement.getTitle() +
                            "\nContent: " + announcement.getContent()
            );

            if (importanceResult.trim().equalsIgnoreCase("IMPORTANT")) {

                if (announcement.getUser().getNeighborhood() == null) {
                    throw new ApiException("User neighborhood not found");
                }
                Integer neighborhoodId = announcement.getUser().getNeighborhood().getId();

                List<User> residents = userRepository.findByNeighborhoodId(neighborhoodId);

                String message = "\uD83D\uDEA8 Important neighborhood announcement\n\n" +
                        "Title: " + announcement.getTitle() + "\n" +
                        "Content: " + announcement.getContent();

                for (User resident : residents) {
                    if (resident.getPhone() != null) {
                        whatsAppService.sendWhatsAppMessage(
                                resident.getPhone(),
                                message
                        );
                    }
                }
            }
        }
    }

    public List<AnnouncementOutDTO> searchAnnouncements(String keyword) {

        return announcementRepository
                .findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToOutDTO)
                .toList();
    }

    public AnnouncementOutDTO getAnnouncementById(Integer id) {

        Announcement announcement = announcementRepository.findAnnouncementById(id);

        if (announcement == null) {
            throw new ApiException("Announcement not found");
        }

        return convertToOutDTO(announcement);
    }

    public PublisherContactOutDTO getPublisherContact(Integer announcementId) {

        Announcement announcement = announcementRepository.findAnnouncementById(announcementId);
        if (announcement == null) {
            throw new ApiException("Announcement not found");
        }

        User user = announcement.getUser();
        return new PublisherContactOutDTO(
                user.getFullName(),
                user.getEmail(),
                user.getPhone()

        );
    }

    private AnnouncementOutDTO convertToOutDTO(Announcement announcement) {

        return new AnnouncementOutDTO(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getStatus(),
                announcement.getCreatedAt(),
                announcement.getUser().getFullName()
        );
    }


}
