package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Announcement;
import org.example.menaandfeena_finalproject.Model.Inquiry;
import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.example.menaandfeena_finalproject.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
    Inquiry findInquiryById(Integer id);

    Inquiry findInquiryByRequesterAndTargetUserAndMarketPlaceItemAndStatus(User requester, User targetUser, MarketPlaceItem marketPlaceItem, String status);

    Inquiry findInquiryByRequesterAndTargetUserAndAnnouncementAndStatus(User requester, User targetUser, Announcement announcement, String status);

    List<Inquiry> findAllByRequesterId(Integer requesterId);

    List<Inquiry> findAllByTargetUserId(Integer targetUserId);
}
