package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.Out.PublisherContactOutDTO;
import org.example.menaandfeena_finalproject.Model.Announcement;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.AnnouncementRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public void addAnnouncement(Announcement announcement) {
        announcementRepository.save(announcement);
    }

    public void updateAnnouncement(Integer id, Announcement announcement) {

        Announcement oldAnnouncement = announcementRepository.findAnnouncementById(id);

        if (oldAnnouncement == null) {
            throw new ApiException("Announcement not found");
        }

        oldAnnouncement.setTitle(announcement.getTitle());
        oldAnnouncement.setContent(announcement.getContent());
        oldAnnouncement.setStatus(announcement.getStatus());
        oldAnnouncement.setCreatedAt(announcement.getCreatedAt());
        announcementRepository.save(oldAnnouncement);
    }

    public void deleteAnnouncement(Integer id) {

        Announcement announcement = announcementRepository.findAnnouncementById(id);

        if (announcement == null) {
            throw new ApiException("Announcement not found");
        }

        announcementRepository.delete(announcement);
    }

    //Walaa

    public void createAnnouncement(Integer userId, Announcement announcement) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        announcement.setUser(user);
        announcement.setStatus("PENDING"); // عشان الAi لازم يراجعه
        announcement.setCreatedAt(LocalDate.now());
        announcementRepository.save(announcement);
    }

  // Walaa
  public List<Announcement> searchAnnouncements(String keyword) {
      return announcementRepository.findByTitleContainingIgnoreCase(keyword);
  }




//Walaa
public Announcement getAnnouncementById(Integer id) { // عرض تفاصيل الاعلان
    Announcement announcement = announcementRepository.findAnnouncementById(id);
    if (announcement == null) {
        throw new ApiException("Announcement not found");
    }
          return announcement;

}

// Walaa
public PublisherContactOutDTO getPublisherContact(Integer announcementId) { // نجيب صاحب الاعلان

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










}
