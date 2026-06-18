package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.MarketPlaceItemInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MarketPlaceItemOutDTO;
import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.MarketPlaceItemRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketPlaceItemService {

    private final MarketPlaceItemRepository marketPlaceItemRepository;
    private final UserRepository userRepository;

    public List<MarketPlaceItemOutDTO> getAllMarketPlaceItems() {
        List<MarketPlaceItemOutDTO> marketPlaceItemOutDTOS = new ArrayList<>();

        for (MarketPlaceItem marketPlaceItem : marketPlaceItemRepository.findAll()) {
            marketPlaceItemOutDTOS.add(toOutDTO(marketPlaceItem));
        }

        return marketPlaceItemOutDTOS;
    }



    public void addMarketPlaceItem(Integer userId, MarketPlaceItemInDTO marketPlaceItemInDTO) {
        if (!marketPlaceItemInDTO.getType().matches("SELL|RENT")) {
            throw new ApiException("Type must be SELL or RENT");
        }

        if (marketPlaceItemInDTO.getQuantity() == null || marketPlaceItemInDTO.getQuantity() < 0) {
            throw new ApiException("Quantity must be zero or positive");
        }

        if (marketPlaceItemInDTO.getType().equals("SELL")) {
            if (marketPlaceItemInDTO.getPrice() == null || marketPlaceItemInDTO.getPrice() <= 0) {
                throw new ApiException("Price is required and must be greater than zero for sell items");
            }
        }

        if (marketPlaceItemInDTO.getType().equals("RENT")) {
            if (marketPlaceItemInDTO.getRentPrice() == null || marketPlaceItemInDTO.getRentPrice() <= 0) {
                throw new ApiException("Rent price is required and must be greater than zero for rent items");
            }

            if (marketPlaceItemInDTO.getDepositAmount() == null || marketPlaceItemInDTO.getDepositAmount() < 0) {
                throw new ApiException("Deposit amount is required and must be zero or greater for rent items");
            }
        }

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        MarketPlaceItem marketPlaceItem = new MarketPlaceItem();
        marketPlaceItem.setTitle(marketPlaceItemInDTO.getTitle());
        marketPlaceItem.setDescription(marketPlaceItemInDTO.getDescription());
        marketPlaceItem.setType(marketPlaceItemInDTO.getType());
        marketPlaceItem.setStatus("AVAILABLE");
        marketPlaceItem.setPrice(marketPlaceItemInDTO.getPrice());
        marketPlaceItem.setRentPrice(marketPlaceItemInDTO.getRentPrice());
        marketPlaceItem.setDepositAmount(marketPlaceItemInDTO.getDepositAmount());
        marketPlaceItem.setQuantity(marketPlaceItemInDTO.getQuantity());
        marketPlaceItem.setUser(user);

        marketPlaceItemRepository.save(marketPlaceItem);
    }

    public void updateMarketPlaceItem(Integer id, Integer userId, MarketPlaceItemInDTO marketPlaceItemInDTO) {
        if (!marketPlaceItemInDTO.getType().matches("SELL|RENT")) {
            throw new ApiException("Type must be SELL or RENT");
        }

        if (marketPlaceItemInDTO.getQuantity() == null || marketPlaceItemInDTO.getQuantity() < 0) {
            throw new ApiException("Quantity must be zero or positive");
        }

        if (marketPlaceItemInDTO.getType().equals("SELL")) {
            if (marketPlaceItemInDTO.getPrice() == null || marketPlaceItemInDTO.getPrice() <= 0) {
                throw new ApiException("Price is required and must be greater than zero for sell items");
            }
        }

        if (marketPlaceItemInDTO.getType().equals("RENT")) {
            if (marketPlaceItemInDTO.getRentPrice() == null || marketPlaceItemInDTO.getRentPrice() <= 0) {
                throw new ApiException("Rent price is required and must be greater than zero for rent items");
            }

            if (marketPlaceItemInDTO.getDepositAmount() == null || marketPlaceItemInDTO.getDepositAmount() < 0) {
                throw new ApiException("Deposit amount is required and must be zero or greater for rent items");
            }
        }

        MarketPlaceItem oldMarketPlaceItem = marketPlaceItemRepository.findMarketPlaceItemById(id);

        if (oldMarketPlaceItem == null) {
            throw new ApiException("Market place item not found");
        }

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        oldMarketPlaceItem.setTitle(marketPlaceItemInDTO.getTitle());
        oldMarketPlaceItem.setDescription(marketPlaceItemInDTO.getDescription());
        oldMarketPlaceItem.setType(marketPlaceItemInDTO.getType());
        oldMarketPlaceItem.setPrice(marketPlaceItemInDTO.getPrice());
        oldMarketPlaceItem.setRentPrice(marketPlaceItemInDTO.getRentPrice());
        oldMarketPlaceItem.setDepositAmount(marketPlaceItemInDTO.getDepositAmount());
        oldMarketPlaceItem.setQuantity(marketPlaceItemInDTO.getQuantity());
        oldMarketPlaceItem.setUser(user);

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
        Integer userId = marketPlaceItem.getUser() == null ? null : marketPlaceItem.getUser().getId();
        return new MarketPlaceItemOutDTO(marketPlaceItem.getId(), marketPlaceItem.getTitle(), marketPlaceItem.getDescription(), marketPlaceItem.getType(), marketPlaceItem.getStatus(), marketPlaceItem.getPrice(), marketPlaceItem.getRentPrice(), marketPlaceItem.getDepositAmount(), marketPlaceItem.getQuantity(), userId);
    }

    // Abdullah

    public MarketPlaceItemOutDTO getMarketPlaceItemById(Integer id) {
        MarketPlaceItem marketPlaceItem = marketPlaceItemRepository.findMarketPlaceItemById(id);

        if (marketPlaceItem == null) {
            throw new ApiException("Market place item not found");
        }

        return toOutDTO(marketPlaceItem);
    }

    public List<MarketPlaceItemOutDTO> getMarketPlaceItemsByType(String type) {
        if (!type.matches("SELL|RENT")) {
            throw new ApiException("Type must be SELL or RENT");
        }

        List<MarketPlaceItemOutDTO> marketPlaceItemOutDTOS = new ArrayList<>();

        for (MarketPlaceItem marketPlaceItem : marketPlaceItemRepository.findMarketPlaceItemsByType(type)) {
            marketPlaceItemOutDTOS.add(toOutDTO(marketPlaceItem));
        }

        return marketPlaceItemOutDTOS;
    }

    public List<MarketPlaceItemOutDTO> getMyMarketPlaceItems(Integer userId) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        List<MarketPlaceItemOutDTO> marketPlaceItemOutDTOS = new ArrayList<>();

        for (MarketPlaceItem marketPlaceItem : marketPlaceItemRepository.findMarketPlaceItemsByUserId(userId)) {
            marketPlaceItemOutDTOS.add(toOutDTO(marketPlaceItem));
        }

        return marketPlaceItemOutDTOS;
    }
}
