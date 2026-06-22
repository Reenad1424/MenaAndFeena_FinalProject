package org.example.menaandfeena_finalproject.DTO.Out;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarketplaceSellerSummaryOutDTO {
    private Integer sellerId;
    private String sellerFullName;
    private Double averageRating;
    private String averageRatingLabel;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate memberSince;
    private Long completedPurchases;
    private Boolean hasOpenInquiry;
    private Integer openInquiryId;
}
