package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.Announcement;
import org.example.menaandfeena_finalproject.Repository.AnnouncementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

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
}
