package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.InquiryMessageInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.InquiryMessageOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.InquiryOutDTO;
import org.example.menaandfeena_finalproject.Model.Announcement;
import org.example.menaandfeena_finalproject.Model.Inquiry;
import org.example.menaandfeena_finalproject.Model.InquiryMessage;
import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.AnnouncementRepository;
import org.example.menaandfeena_finalproject.Repository.InquiryMessageRepository;
import org.example.menaandfeena_finalproject.Repository.InquiryRepository;
import org.example.menaandfeena_finalproject.Repository.MarketPlaceItemRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private static final String OPEN = "OPEN";
    private static final String RESOLVED = "RESOLVED";

    private final InquiryRepository inquiryRepository;
    private final InquiryMessageRepository inquiryMessageRepository;
    private final UserRepository userRepository;
    private final MarketPlaceItemRepository marketPlaceItemRepository;
    private final AnnouncementRepository announcementRepository;

    // Abdullah

    public InquiryOutDTO createMarketplaceInquiry(Integer itemId, Integer requesterId) {
        User requester = userRepository.findUserById(requesterId);

        if (requester == null) {
            throw new ApiException("User not found");
        }

        MarketPlaceItem item = marketPlaceItemRepository.findMarketPlaceItemById(itemId);

        if (item == null) {
            throw new ApiException("Market place item not found");
        }

        User targetUser = item.getUser();

        if (targetUser == null) {
            throw new ApiException("Inquiry target user not found");
        }

        if (requester.getId().equals(targetUser.getId())) {
            throw new ApiException("You cannot create an inquiry on your own market place item");
        }

        Inquiry existingInquiry = inquiryRepository.findInquiryByRequesterAndTargetUserAndMarketPlaceItemAndStatus(requester, targetUser, item, OPEN);

        if (existingInquiry != null) {
            Integer requesterIdOut = existingInquiry.getRequester() == null ? null : existingInquiry.getRequester().getId();
            Integer targetUserIdOut = existingInquiry.getTargetUser() == null ? null : existingInquiry.getTargetUser().getId();
            Integer marketPlaceItemIdOut = existingInquiry.getMarketPlaceItem() == null ? null : existingInquiry.getMarketPlaceItem().getId();
            Integer announcementIdOut = existingInquiry.getAnnouncement() == null ? null : existingInquiry.getAnnouncement().getId();

            return new InquiryOutDTO(existingInquiry.getId(), existingInquiry.getSubject(), existingInquiry.getStatus(), existingInquiry.getCreatedAt(), requesterIdOut, targetUserIdOut, marketPlaceItemIdOut, announcementIdOut);
        }

        Inquiry inquiry = new Inquiry();
        inquiry.setSubject("استفسار عن منتج: " + item.getTitle());
        inquiry.setStatus(OPEN);
        inquiry.setCreatedAt(LocalDateTime.now());
        inquiry.setRequester(requester);
        inquiry.setTargetUser(targetUser);
        inquiry.setMarketPlaceItem(item);
        inquiry.setAnnouncement(null);

        Inquiry savedInquiry = inquiryRepository.save(inquiry);
        Integer requesterIdOut = savedInquiry.getRequester() == null ? null : savedInquiry.getRequester().getId();
        Integer targetUserIdOut = savedInquiry.getTargetUser() == null ? null : savedInquiry.getTargetUser().getId();
        Integer marketPlaceItemIdOut = savedInquiry.getMarketPlaceItem() == null ? null : savedInquiry.getMarketPlaceItem().getId();
        Integer announcementIdOut = savedInquiry.getAnnouncement() == null ? null : savedInquiry.getAnnouncement().getId();

        return new InquiryOutDTO(savedInquiry.getId(), savedInquiry.getSubject(), savedInquiry.getStatus(), savedInquiry.getCreatedAt(), requesterIdOut, targetUserIdOut, marketPlaceItemIdOut, announcementIdOut);
    }

    public InquiryOutDTO createAnnouncementInquiry(Integer announcementId, Integer requesterId) {
        User requester = userRepository.findUserById(requesterId);

        if (requester == null) {
            throw new ApiException("User not found");
        }

        Announcement announcement = announcementRepository.findAnnouncementById(announcementId);

        if (announcement == null) {
            throw new ApiException("Announcement not found");
        }

        User targetUser = announcement.getUser();

        if (targetUser == null) {
            throw new ApiException("Inquiry target user not found");
        }

        if (requester.getId().equals(targetUser.getId())) {
            throw new ApiException("You cannot create an inquiry on your own announcement");
        }

        Inquiry existingInquiry = inquiryRepository.findInquiryByRequesterAndTargetUserAndAnnouncementAndStatus(requester, targetUser, announcement, OPEN);

        if (existingInquiry != null) {
            Integer requesterIdOut = existingInquiry.getRequester() == null ? null : existingInquiry.getRequester().getId();
            Integer targetUserIdOut = existingInquiry.getTargetUser() == null ? null : existingInquiry.getTargetUser().getId();
            Integer marketPlaceItemIdOut = existingInquiry.getMarketPlaceItem() == null ? null : existingInquiry.getMarketPlaceItem().getId();
            Integer announcementIdOut = existingInquiry.getAnnouncement() == null ? null : existingInquiry.getAnnouncement().getId();

            return new InquiryOutDTO(existingInquiry.getId(), existingInquiry.getSubject(), existingInquiry.getStatus(), existingInquiry.getCreatedAt(), requesterIdOut, targetUserIdOut, marketPlaceItemIdOut, announcementIdOut);
        }

        Inquiry inquiry = new Inquiry();
        inquiry.setSubject("استفسار عن منشور: " + announcement.getTitle());
        inquiry.setStatus(OPEN);
        inquiry.setCreatedAt(LocalDateTime.now());
        inquiry.setRequester(requester);
        inquiry.setTargetUser(targetUser);
        inquiry.setMarketPlaceItem(null);
        inquiry.setAnnouncement(announcement);

        Inquiry savedInquiry = inquiryRepository.save(inquiry);
        Integer requesterIdOut = savedInquiry.getRequester() == null ? null : savedInquiry.getRequester().getId();
        Integer targetUserIdOut = savedInquiry.getTargetUser() == null ? null : savedInquiry.getTargetUser().getId();
        Integer marketPlaceItemIdOut = savedInquiry.getMarketPlaceItem() == null ? null : savedInquiry.getMarketPlaceItem().getId();
        Integer announcementIdOut = savedInquiry.getAnnouncement() == null ? null : savedInquiry.getAnnouncement().getId();

        return new InquiryOutDTO(savedInquiry.getId(), savedInquiry.getSubject(), savedInquiry.getStatus(), savedInquiry.getCreatedAt(), requesterIdOut, targetUserIdOut, marketPlaceItemIdOut, announcementIdOut);
    }

    public InquiryMessageOutDTO addMessage(Integer inquiryId, Integer senderId, InquiryMessageInDTO inquiryMessageInDTO) {
        Inquiry inquiry = inquiryRepository.findInquiryById(inquiryId);

        if (inquiry == null) {
            throw new ApiException("Inquiry not found");
        }

        User sender = userRepository.findUserById(senderId);

        if (sender == null) {
            throw new ApiException("User not found");
        }

        boolean isRequester = inquiry.getRequester() != null && inquiry.getRequester().getId().equals(sender.getId());
        boolean isTargetUser = inquiry.getTargetUser() != null && inquiry.getTargetUser().getId().equals(sender.getId());

        if (!isRequester && !isTargetUser) {
            throw new ApiException("Only requester or target user can send messages in this inquiry");
        }

        if (RESOLVED.equals(inquiry.getStatus())) {
            throw new ApiException("Cannot send messages after inquiry is resolved");
        }

        InquiryMessage inquiryMessage = new InquiryMessage();
        inquiryMessage.setContent(inquiryMessageInDTO.getContent());
        inquiryMessage.setSentAt(LocalDateTime.now());
        inquiryMessage.setSender(sender);
        inquiryMessage.setInquiry(inquiry);

        InquiryMessage savedMessage = inquiryMessageRepository.save(inquiryMessage);
        Integer senderIdOut = savedMessage.getSender() == null ? null : savedMessage.getSender().getId();
        Integer inquiryIdOut = savedMessage.getInquiry() == null ? null : savedMessage.getInquiry().getId();

        return new InquiryMessageOutDTO(savedMessage.getId(), savedMessage.getContent(), savedMessage.getSentAt(), senderIdOut, inquiryIdOut);
    }

    public List<InquiryMessageOutDTO> getMessages(Integer inquiryId, Integer userId) {
        Inquiry inquiry = inquiryRepository.findInquiryById(inquiryId);

        if (inquiry == null) {
            throw new ApiException("Inquiry not found");
        }

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        boolean isRequester = inquiry.getRequester() != null && inquiry.getRequester().getId().equals(user.getId());
        boolean isTargetUser = inquiry.getTargetUser() != null && inquiry.getTargetUser().getId().equals(user.getId());

        if (!isRequester && !isTargetUser) {
            throw new ApiException("Only requester or target user can view messages in this inquiry");
        }

        List<InquiryMessageOutDTO> messages = new ArrayList<>();
        for (InquiryMessage inquiryMessage : inquiryMessageRepository.findAllByInquiryIdOrderBySentAtAsc(inquiryId)) {
            Integer senderIdOut = inquiryMessage.getSender() == null ? null : inquiryMessage.getSender().getId();
            Integer inquiryIdOut = inquiryMessage.getInquiry() == null ? null : inquiryMessage.getInquiry().getId();

            messages.add(new InquiryMessageOutDTO(inquiryMessage.getId(), inquiryMessage.getContent(), inquiryMessage.getSentAt(), senderIdOut, inquiryIdOut));
        }

        return messages;
    }

    public List<InquiryOutDTO> getMyInquiries(Integer userId) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        List<InquiryOutDTO> inquiries = new ArrayList<>();
        for (Inquiry inquiry : inquiryRepository.findAllByRequesterId(userId)) {
            Integer requesterIdOut = inquiry.getRequester() == null ? null : inquiry.getRequester().getId();
            Integer targetUserIdOut = inquiry.getTargetUser() == null ? null : inquiry.getTargetUser().getId();
            Integer marketPlaceItemIdOut = inquiry.getMarketPlaceItem() == null ? null : inquiry.getMarketPlaceItem().getId();
            Integer announcementIdOut = inquiry.getAnnouncement() == null ? null : inquiry.getAnnouncement().getId();

            inquiries.add(new InquiryOutDTO(inquiry.getId(), inquiry.getSubject(), inquiry.getStatus(), inquiry.getCreatedAt(), requesterIdOut, targetUserIdOut, marketPlaceItemIdOut, announcementIdOut));
        }

        return inquiries;
    }

    public List<InquiryOutDTO> getReceivedInquiries(Integer userId) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        List<InquiryOutDTO> inquiries = new ArrayList<>();
        for (Inquiry inquiry : inquiryRepository.findAllByTargetUserId(userId)) {
            Integer requesterIdOut = inquiry.getRequester() == null ? null : inquiry.getRequester().getId();
            Integer targetUserIdOut = inquiry.getTargetUser() == null ? null : inquiry.getTargetUser().getId();
            Integer marketPlaceItemIdOut = inquiry.getMarketPlaceItem() == null ? null : inquiry.getMarketPlaceItem().getId();
            Integer announcementIdOut = inquiry.getAnnouncement() == null ? null : inquiry.getAnnouncement().getId();

            inquiries.add(new InquiryOutDTO(inquiry.getId(), inquiry.getSubject(), inquiry.getStatus(), inquiry.getCreatedAt(), requesterIdOut, targetUserIdOut, marketPlaceItemIdOut, announcementIdOut));
        }

        return inquiries;
    }

    public void resolveInquiry(Integer inquiryId, Integer userId) {
        Inquiry inquiry = inquiryRepository.findInquiryById(inquiryId);

        if (inquiry == null) {
            throw new ApiException("Inquiry not found");
        }

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        boolean isRequester = inquiry.getRequester() != null && inquiry.getRequester().getId().equals(user.getId());
        boolean isTargetUser = inquiry.getTargetUser() != null && inquiry.getTargetUser().getId().equals(user.getId());

        if (!isRequester && !isTargetUser) {
            throw new ApiException("Only requester or target user can resolve this inquiry");
        }

        inquiry.setStatus(RESOLVED);
        inquiryRepository.save(inquiry);
    }
}
