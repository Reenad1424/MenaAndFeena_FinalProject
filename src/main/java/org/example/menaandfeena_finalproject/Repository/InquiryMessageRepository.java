package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.InquiryMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryMessageRepository extends JpaRepository<InquiryMessage, Integer> {
    List<InquiryMessage> findAllByInquiryIdOrderBySentAtAsc(Integer inquiryId);
}
