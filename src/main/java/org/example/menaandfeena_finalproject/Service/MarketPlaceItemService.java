package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.MarketPlaceItemInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MarketPlaceItemOutDTO;
import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.example.menaandfeena_finalproject.Repository.MarketPlaceItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketPlaceItemService {

    private final MarketPlaceItemRepository marketPlaceItemRepository;

    public List<MarketPlaceItemOutDTO> getAllMarketPlaceItems() {
        List<MarketPlaceItemOutDTO> marketPlaceItemOutDTOS = new ArrayList<>();

        for (MarketPlaceItem marketPlaceItem : marketPlaceItemRepository.findAll()) {
            marketPlaceItemOutDTOS.add(toOutDTO(marketPlaceItem));
        }

        return marketPlaceItemOutDTOS;
    }

    public void addMarketPlaceItem(MarketPlaceItemInDTO marketPlaceItemInDTO) {
        MarketPlaceItem marketPlaceItem = new MarketPlaceItem();
        marketPlaceItem.setTitle(marketPlaceItemInDTO.getTitle());
        marketPlaceItem.setDescription(marketPlaceItemInDTO.getDescription());
        marketPlaceItem.setType(marketPlaceItemInDTO.getType());
        marketPlaceItem.setStatus(marketPlaceItemInDTO.getStatus());
        marketPlaceItem.setPrice(marketPlaceItemInDTO.getPrice());
        marketPlaceItem.setRentPrice(marketPlaceItemInDTO.getRentPrice());
        marketPlaceItem.setDepositAmount(marketPlaceItemInDTO.getDepositAmount());
        marketPlaceItem.setQuantity(marketPlaceItemInDTO.getQuantity());

        marketPlaceItemRepository.save(marketPlaceItem);
    }

    public void updateMarketPlaceItem(Integer id, MarketPlaceItemInDTO marketPlaceItemInDTO) {
        MarketPlaceItem oldMarketPlaceItem = marketPlaceItemRepository.findMarketPlaceItemById(id);

        if (oldMarketPlaceItem == null) {
            throw new ApiException("Market place item not found");
        }

        oldMarketPlaceItem.setTitle(marketPlaceItemInDTO.getTitle());
        oldMarketPlaceItem.setDescription(marketPlaceItemInDTO.getDescription());
        oldMarketPlaceItem.setType(marketPlaceItemInDTO.getType());
        oldMarketPlaceItem.setStatus(marketPlaceItemInDTO.getStatus());
        oldMarketPlaceItem.setPrice(marketPlaceItemInDTO.getPrice());
        oldMarketPlaceItem.setRentPrice(marketPlaceItemInDTO.getRentPrice());
        oldMarketPlaceItem.setDepositAmount(marketPlaceItemInDTO.getDepositAmount());
        oldMarketPlaceItem.setQuantity(marketPlaceItemInDTO.getQuantity());

        marketPlaceItemRepository.save(oldMarketPlaceItem);
    }

    public void deleteMarketPlaceItem(Integer id) {
        MarketPlaceItem marketPlaceItem = marketPlaceItemRepository.findMarketPlaceItemById(id);

        if (marketPlaceItem == null) {
            throw new ApiException("Market place item not found");
        }

        marketPlaceItemRepository.delete(marketPlaceItem);
    }

    private MarketPlaceItemOutDTO toOutDTO(MarketPlaceItem marketPlaceItem) {
        return new MarketPlaceItemOutDTO(marketPlaceItem.getId(), marketPlaceItem.getTitle(), marketPlaceItem.getDescription(), marketPlaceItem.getType(), marketPlaceItem.getStatus(), marketPlaceItem.getPrice(), marketPlaceItem.getRentPrice(), marketPlaceItem.getDepositAmount(), marketPlaceItem.getQuantity());
    }
}
