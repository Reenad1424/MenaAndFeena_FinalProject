package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileMarketplaceDTO {

    private UserBasicInfoDTO basicInfo;

    private List<UserMarketItemDTO> marketplaceItems;

    private List<UserOrderDTO> purchases;

    private List<UserOrderDTO> sales;
}