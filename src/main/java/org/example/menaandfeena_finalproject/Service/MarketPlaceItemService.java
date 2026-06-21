package org.example.menaandfeena_finalproject.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.MarketPlaceItemInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MarketPlaceItemOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MarketplaceRecommendationOutDTO;
import org.example.menaandfeena_finalproject.Model.Cart;
import org.example.menaandfeena_finalproject.Model.CartItem;
import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.example.menaandfeena_finalproject.Model.OrderItem;
import org.example.menaandfeena_finalproject.Model.Orders;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.CartItemRepository;
import org.example.menaandfeena_finalproject.Repository.CartRepository;
import org.example.menaandfeena_finalproject.Repository.MarketPlaceItemRepository;
import org.example.menaandfeena_finalproject.Repository.OrderItemRepository;
import org.example.menaandfeena_finalproject.Repository.OrderRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MarketPlaceItemService {

    private final MarketPlaceItemRepository marketPlaceItemRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OpenAIService openAIService;

    public List<MarketPlaceItemOutDTO> getAllMarketPlaceItems() {
        List<MarketPlaceItemOutDTO> marketPlaceItemOutDTOS = new ArrayList<>();

        for (MarketPlaceItem marketPlaceItem : marketPlaceItemRepository.findAll()) {
            Integer userId = marketPlaceItem.getUser() == null ? null : marketPlaceItem.getUser().getId();
            String sellerFullName = marketPlaceItem.getUser() == null ? null : marketPlaceItem.getUser().getFullName();
            marketPlaceItemOutDTOS.add(new MarketPlaceItemOutDTO(marketPlaceItem.getId(), marketPlaceItem.getTitle(), marketPlaceItem.getDescription(), marketPlaceItem.getType(), marketPlaceItem.getStatus(), marketPlaceItem.getPrice(), marketPlaceItem.getRentPrice(), marketPlaceItem.getDepositAmount(), marketPlaceItem.getQuantity(), userId, sellerFullName));
        }

        return marketPlaceItemOutDTOS;
    }

    public List<MarketPlaceItemOutDTO> getMarketPlaceItemsForUser(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        List<MarketPlaceItemOutDTO> marketPlaceItemOutDTOS = new ArrayList<>();
        for (MarketPlaceItem item : marketPlaceItemRepository.findAll()) {
            if (item.getUser() != null
                    && item.getUser().getNeighborhood() != null
                    && item.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
                Integer itemUserId = item.getUser().getId();
                String sellerFullName = item.getUser().getFullName();
                marketPlaceItemOutDTOS.add(new MarketPlaceItemOutDTO(item.getId(), item.getTitle(), item.getDescription(), item.getType(), item.getStatus(), item.getPrice(), item.getRentPrice(), item.getDepositAmount(), item.getQuantity(), itemUserId, sellerFullName));
            }
        }

        return marketPlaceItemOutDTOS;
    }



    public void addMarketPlaceItem(Integer userId, MarketPlaceItemInDTO marketPlaceItemInDTO) {
        if (!marketPlaceItemInDTO.getType().matches("SELL|RENT")) {
            throw new ApiException("Type must be SELL or RENT only");
        }

        if (marketPlaceItemInDTO.getQuantity() == null || marketPlaceItemInDTO.getQuantity() < 0) {
            throw new ApiException("Quantity must be zero or positive");
        }

        if (marketPlaceItemInDTO.getType().equals("SELL")) {
            if (marketPlaceItemInDTO.getPrice() == null || marketPlaceItemInDTO.getPrice() <= 0) {
                throw new ApiException("Price is required and must be greater than zero for sell items");
            }
            if (marketPlaceItemInDTO.getRentPrice() != null || marketPlaceItemInDTO.getDepositAmount() != null) {
                throw new ApiException("Sell items must not use rent price or deposit amount");
            }
        }

        if (marketPlaceItemInDTO.getType().equals("RENT")) {
            if (marketPlaceItemInDTO.getPrice() != null) {
                throw new ApiException("Rent items must not use price");
            }
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
            throw new ApiException("Type must be SELL or RENT only");
        }

        if (marketPlaceItemInDTO.getQuantity() == null || marketPlaceItemInDTO.getQuantity() < 0) {
            throw new ApiException("Quantity must be zero or positive");
        }

        if (marketPlaceItemInDTO.getType().equals("SELL")) {
            if (marketPlaceItemInDTO.getPrice() == null || marketPlaceItemInDTO.getPrice() <= 0) {
                throw new ApiException("Price is required and must be greater than zero for sell items");
            }
            if (marketPlaceItemInDTO.getRentPrice() != null || marketPlaceItemInDTO.getDepositAmount() != null) {
                throw new ApiException("Sell items must not use rent price or deposit amount");
            }
        }

        if (marketPlaceItemInDTO.getType().equals("RENT")) {
            if (marketPlaceItemInDTO.getPrice() != null) {
                throw new ApiException("Rent items must not use price");
            }
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

    // Abdullah

    public MarketPlaceItemOutDTO getMarketPlaceItemById(Integer id) {
        MarketPlaceItem marketPlaceItem = marketPlaceItemRepository.findMarketPlaceItemById(id);

        if (marketPlaceItem == null) {
            throw new ApiException("Market place item not found");
        }

        Integer userId = marketPlaceItem.getUser() == null ? null : marketPlaceItem.getUser().getId();
        String sellerFullName = marketPlaceItem.getUser() == null ? null : marketPlaceItem.getUser().getFullName();
        return new MarketPlaceItemOutDTO(marketPlaceItem.getId(), marketPlaceItem.getTitle(), marketPlaceItem.getDescription(), marketPlaceItem.getType(), marketPlaceItem.getStatus(), marketPlaceItem.getPrice(), marketPlaceItem.getRentPrice(), marketPlaceItem.getDepositAmount(), marketPlaceItem.getQuantity(), userId, sellerFullName);
    }

    public MarketPlaceItemOutDTO getMarketPlaceItemByIdForUser(Integer id, Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        MarketPlaceItem marketPlaceItem = marketPlaceItemRepository.findMarketPlaceItemById(id);
        if (marketPlaceItem == null) {
            throw new ApiException("Market place item not found");
        }
        if (marketPlaceItem.getUser() == null || marketPlaceItem.getUser().getNeighborhood() == null) {
            throw new ApiException("Market place item owner neighborhood is required");
        }
        if (!marketPlaceItem.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Market place item is outside your neighborhood");
        }

        Integer itemUserId = marketPlaceItem.getUser().getId();
        String sellerFullName = marketPlaceItem.getUser().getFullName();
        return new MarketPlaceItemOutDTO(marketPlaceItem.getId(), marketPlaceItem.getTitle(), marketPlaceItem.getDescription(), marketPlaceItem.getType(), marketPlaceItem.getStatus(), marketPlaceItem.getPrice(), marketPlaceItem.getRentPrice(), marketPlaceItem.getDepositAmount(), marketPlaceItem.getQuantity(), itemUserId, sellerFullName);
    }

    public List<MarketPlaceItemOutDTO> getMarketPlaceItemsByType(String type) {
        if (!type.matches("SELL|RENT")) {
            throw new ApiException("Type must be SELL or RENT only");
        }

        List<MarketPlaceItemOutDTO> marketPlaceItemOutDTOS = new ArrayList<>();

        for (MarketPlaceItem marketPlaceItem : marketPlaceItemRepository.findMarketPlaceItemsByType(type)) {
            Integer userId = marketPlaceItem.getUser() == null ? null : marketPlaceItem.getUser().getId();
            String sellerFullName = marketPlaceItem.getUser() == null ? null : marketPlaceItem.getUser().getFullName();
            marketPlaceItemOutDTOS.add(new MarketPlaceItemOutDTO(marketPlaceItem.getId(), marketPlaceItem.getTitle(), marketPlaceItem.getDescription(), marketPlaceItem.getType(), marketPlaceItem.getStatus(), marketPlaceItem.getPrice(), marketPlaceItem.getRentPrice(), marketPlaceItem.getDepositAmount(), marketPlaceItem.getQuantity(), userId, sellerFullName));
        }

        return marketPlaceItemOutDTOS;
    }

    public List<MarketPlaceItemOutDTO> getMarketPlaceItemsByTypeForUser(String type, Integer userId) {
        if (!type.matches("SELL|RENT")) {
            throw new ApiException("Type must be SELL or RENT only");
        }

        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        List<MarketPlaceItemOutDTO> marketPlaceItemOutDTOS = new ArrayList<>();
        for (MarketPlaceItem item : marketPlaceItemRepository.findMarketPlaceItemsByType(type)) {
            if (item.getUser() != null
                    && item.getUser().getNeighborhood() != null
                    && item.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
                Integer itemUserId = item.getUser().getId();
                String sellerFullName = item.getUser().getFullName();
                marketPlaceItemOutDTOS.add(new MarketPlaceItemOutDTO(item.getId(), item.getTitle(), item.getDescription(), item.getType(), item.getStatus(), item.getPrice(), item.getRentPrice(), item.getDepositAmount(), item.getQuantity(), itemUserId, sellerFullName));
            }
        }

        return marketPlaceItemOutDTOS;
    }

    public List<MarketPlaceItemOutDTO> searchMarketPlaceItemsForUser(Integer userId, String keyword) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        String searchKeyword = keyword == null ? "" : keyword.toLowerCase();
        List<MarketPlaceItemOutDTO> marketPlaceItemOutDTOS = new ArrayList<>();
        for (MarketPlaceItem item : marketPlaceItemRepository.findAll()) {
            String itemText = ((item.getTitle() == null ? "" : item.getTitle()) + " " + (item.getDescription() == null ? "" : item.getDescription())).toLowerCase();
            if (itemText.contains(searchKeyword)
                    && item.getUser() != null
                    && item.getUser().getNeighborhood() != null
                    && item.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
                Integer itemUserId = item.getUser().getId();
                String sellerFullName = item.getUser().getFullName();
                marketPlaceItemOutDTOS.add(new MarketPlaceItemOutDTO(item.getId(), item.getTitle(), item.getDescription(), item.getType(), item.getStatus(), item.getPrice(), item.getRentPrice(), item.getDepositAmount(), item.getQuantity(), itemUserId, sellerFullName));
            }
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
            Integer itemUserId = marketPlaceItem.getUser() == null ? null : marketPlaceItem.getUser().getId();
            String sellerFullName = marketPlaceItem.getUser() == null ? null : marketPlaceItem.getUser().getFullName();
            marketPlaceItemOutDTOS.add(new MarketPlaceItemOutDTO(marketPlaceItem.getId(), marketPlaceItem.getTitle(), marketPlaceItem.getDescription(), marketPlaceItem.getType(), marketPlaceItem.getStatus(), marketPlaceItem.getPrice(), marketPlaceItem.getRentPrice(), marketPlaceItem.getDepositAmount(), marketPlaceItem.getQuantity(), itemUserId, sellerFullName));
        }

        return marketPlaceItemOutDTOS;
    }

    public List<MarketplaceRecommendationOutDTO> getPersonalizedRecommendations(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        List<OrderItem> previousOrderItems = new ArrayList<>();
        List<String> purchasedProducts = new ArrayList<>();
        List<String> rentedProducts = new ArrayList<>();
        Set<String> behaviorKeywords = new HashSet<>();
        Set<String> behaviorTypes = new HashSet<>();

        for (Orders order : orderRepository.findOrdersByUserId(userId)) {
            for (OrderItem orderItem : orderItemRepository.findOrderItemsByOrdersId(order.getId())) {
                previousOrderItems.add(orderItem);
                MarketPlaceItem item = orderItem.getMarketPlaceItem();
                if (item != null) {
                    String productLine = "ID " + item.getId() + " | " + item.getTitle() + " | " + item.getDescription() + " | " + item.getType();
                    if ("RENT".equals(orderItem.getType())) {
                        rentedProducts.add(productLine);
                    } else if ("SELL".equals(orderItem.getType())) {
                        purchasedProducts.add(productLine);
                    }
                    behaviorTypes.add(item.getType());
                    if (item.getTitle() != null) {
                        behaviorKeywords.addAll(Arrays.asList(item.getTitle().toLowerCase().split("\\s+")));
                    }
                    if (item.getDescription() != null) {
                        behaviorKeywords.addAll(Arrays.asList(item.getDescription().toLowerCase().split("\\s+")));
                    }
                }
            }
        }

        Set<Integer> cartItemIds = new HashSet<>();
        List<String> currentCartProducts = new ArrayList<>();
        Cart cart = cartRepository.findCartByUserId(userId);
        if (cart != null) {
            for (CartItem cartItem : cartItemRepository.findCartItemsByCartId(cart.getId())) {
                MarketPlaceItem item = cartItem.getMarketPlaceItem();
                if (item != null) {
                    cartItemIds.add(item.getId());
                    currentCartProducts.add("ID " + item.getId() + " | " + item.getTitle() + " | " + item.getDescription() + " | " + item.getType());
                    behaviorTypes.add(item.getType());
                    if (item.getTitle() != null) {
                        behaviorKeywords.addAll(Arrays.asList(item.getTitle().toLowerCase().split("\\s+")));
                    }
                    if (item.getDescription() != null) {
                        behaviorKeywords.addAll(Arrays.asList(item.getDescription().toLowerCase().split("\\s+")));
                    }
                }
            }
        }

        Map<Integer, MarketPlaceItem> validItemsById = new LinkedHashMap<>();
        List<String> availableProductLines = new ArrayList<>();
        Integer userNeighborhoodId = user.getNeighborhood().getId();
        for (MarketPlaceItem item : marketPlaceItemRepository.findAll()) {
            Integer ownerId = item.getUser() == null ? null : item.getUser().getId();
            Integer itemNeighborhoodId = item.getUser() == null || item.getUser().getNeighborhood() == null ? null : item.getUser().getNeighborhood().getId();
            if ("AVAILABLE".equals(item.getStatus())
                    && item.getQuantity() != null
                    && item.getQuantity() > 0
                    && (ownerId == null || !ownerId.equals(userId))
                    && !cartItemIds.contains(item.getId())
                    && userNeighborhoodId.equals(itemNeighborhoodId)) {
                validItemsById.put(item.getId(), item);
                availableProductLines.add("ID " + item.getId() + " | title: " + item.getTitle() + " | description: " + item.getDescription() + " | type: " + item.getType() + " | price: " + item.getPrice() + " | rentPrice: " + item.getRentPrice() + " | deposit: " + item.getDepositAmount() + " | quantity: " + item.getQuantity());
            }
        }

        if (validItemsById.isEmpty()) {
            return new ArrayList<>();
        }

        List<MarketplaceRecommendationOutDTO> recommendations = new ArrayList<>();
        Set<Integer> usedIds = new HashSet<>();
        String systemPrompt = """
                You recommend marketplace products. Return strict JSON only.
                AI rules:
                - Do not invent products.
                - Only choose item IDs from the provided available products.
                - Never recommend unavailable products.
                - Never recommend products owned by the user.
                - Return minimum 3 recommendations whenever at least 3 valid products exist.
                - Return maximum 5 recommendations.
                - Reasons must be written in Arabic.
                JSON format:
                [{"itemId":4,"reason":"سبب عربي قصير"}]
                """;
        String userContent = "Previously rented products:\n" + String.join("\n", rentedProducts)
                + "\n\nPreviously purchased products:\n" + String.join("\n", purchasedProducts)
                + "\n\nCurrent cart products:\n" + String.join("\n", currentCartProducts)
                + "\n\nAvailable marketplace items:\n" + String.join("\n", availableProductLines)
                + "\n\nBased on this user's marketplace history and the available marketplace items, recommend the best products for this user. Return between 3 and 5 recommendations only.";

        try {
            String aiResponse = openAIService.askAI(systemPrompt, userContent);
            if (aiResponse != null && !"ERROR_FALLBACK".equals(aiResponse)) {
                String json = aiResponse.trim();
                if (json.startsWith("```")) {
                    json = json.replace("```json", "").replace("```", "").trim();
                }
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> aiItems = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
                for (Map<String, Object> aiItem : aiItems) {
                    if (recommendations.size() == 5) {
                        break;
                    }
                    Object rawId = aiItem.get("itemId");
                    Integer recommendedId = rawId instanceof Number ? ((Number) rawId).intValue() : Integer.valueOf(rawId.toString());
                    if (!validItemsById.containsKey(recommendedId) || usedIds.contains(recommendedId)) {
                        continue;
                    }
                    MarketPlaceItem item = validItemsById.get(recommendedId);
                    String reason = aiItem.get("reason") == null ? "مقترح لك بناءً على اهتماماتك في السوق." : aiItem.get("reason").toString();
                    recommendations.add(new MarketplaceRecommendationOutDTO(item.getId(), item.getTitle(), item.getDescription(), item.getType(), item.getPrice(), item.getRentPrice(), item.getDepositAmount(), item.getQuantity(), reason));
                    usedIds.add(recommendedId);
                }
            }
        } catch (Exception ignored) {
        }

        int minimum = Math.min(3, validItemsById.size());
        if (recommendations.size() < minimum) {
            List<MarketPlaceItem> fallbackItems = new ArrayList<>(validItemsById.values());
            fallbackItems.sort((first, second) -> {
                int firstScore = 0;
                int secondScore = 0;
                if (behaviorTypes.contains(first.getType())) firstScore += 3;
                if (behaviorTypes.contains(second.getType())) secondScore += 3;
                String firstText = ((first.getTitle() == null ? "" : first.getTitle()) + " " + (first.getDescription() == null ? "" : first.getDescription())).toLowerCase();
                String secondText = ((second.getTitle() == null ? "" : second.getTitle()) + " " + (second.getDescription() == null ? "" : second.getDescription())).toLowerCase();
                for (String keyword : behaviorKeywords) {
                    if (keyword.length() > 2 && firstText.contains(keyword)) firstScore++;
                    if (keyword.length() > 2 && secondText.contains(keyword)) secondScore++;
                }
                return Integer.compare(secondScore, firstScore);
            });

            for (MarketPlaceItem item : fallbackItems) {
                if (recommendations.size() == 5 || recommendations.size() >= minimum && validItemsById.size() >= 3) {
                    break;
                }
                if (usedIds.contains(item.getId())) {
                    continue;
                }
                recommendations.add(new MarketplaceRecommendationOutDTO(item.getId(), item.getTitle(), item.getDescription(), item.getType(), item.getPrice(), item.getRentPrice(), item.getDepositAmount(), item.getQuantity(), "مقترح لك بناءً على مشترياتك واستئجاراتك السابقة."));
                usedIds.add(item.getId());
            }
        }

        return recommendations;
    }

    public List<MarketplaceRecommendationOutDTO> getSimilarProducts(Integer marketPlaceItemId, Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }

        MarketPlaceItem currentItem = marketPlaceItemRepository.findMarketPlaceItemById(marketPlaceItemId);
        if (currentItem == null) {
            throw new ApiException("Market place item not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }
        if (currentItem.getUser() == null || currentItem.getUser().getNeighborhood() == null) {
            throw new ApiException("Market place item owner neighborhood is required");
        }
        if (!currentItem.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Market place item is outside your neighborhood");
        }

        List<String> userBehavior = new ArrayList<>();
        Set<String> behaviorKeywords = new HashSet<>();
        Set<String> behaviorTypes = new HashSet<>();
        for (Orders order : orderRepository.findOrdersByUserId(userId)) {
            for (OrderItem orderItem : orderItemRepository.findOrderItemsByOrdersId(order.getId())) {
                MarketPlaceItem item = orderItem.getMarketPlaceItem();
                if (item != null) {
                    userBehavior.add("ID " + item.getId() + " | " + item.getTitle() + " | " + item.getDescription() + " | " + item.getType());
                    behaviorTypes.add(item.getType());
                    if (item.getTitle() != null) {
                        behaviorKeywords.addAll(Arrays.asList(item.getTitle().toLowerCase().split("\\s+")));
                    }
                    if (item.getDescription() != null) {
                        behaviorKeywords.addAll(Arrays.asList(item.getDescription().toLowerCase().split("\\s+")));
                    }
                }
            }
        }

        if (currentItem.getTitle() != null) {
            behaviorKeywords.addAll(Arrays.asList(currentItem.getTitle().toLowerCase().split("\\s+")));
        }
        if (currentItem.getDescription() != null) {
            behaviorKeywords.addAll(Arrays.asList(currentItem.getDescription().toLowerCase().split("\\s+")));
        }

        Map<Integer, MarketPlaceItem> validItemsById = new LinkedHashMap<>();
        List<String> availableProductLines = new ArrayList<>();
        for (MarketPlaceItem item : marketPlaceItemRepository.findAll()) {
            Integer ownerId = item.getUser() == null ? null : item.getUser().getId();
            Integer itemNeighborhoodId = item.getUser() == null || item.getUser().getNeighborhood() == null ? null : item.getUser().getNeighborhood().getId();
            if (!item.getId().equals(currentItem.getId())
                    && "AVAILABLE".equals(item.getStatus())
                    && item.getQuantity() != null
                    && item.getQuantity() > 0
                    && (ownerId == null || !ownerId.equals(userId))
                    && user.getNeighborhood().getId().equals(itemNeighborhoodId)) {
                validItemsById.put(item.getId(), item);
                availableProductLines.add("ID " + item.getId() + " | title: " + item.getTitle() + " | description: " + item.getDescription() + " | type: " + item.getType() + " | price: " + item.getPrice() + " | rentPrice: " + item.getRentPrice() + " | deposit: " + item.getDepositAmount() + " | quantity: " + item.getQuantity());
            }
        }

        if (validItemsById.isEmpty()) {
            return new ArrayList<>();
        }

        List<MarketplaceRecommendationOutDTO> recommendations = new ArrayList<>();
        Set<Integer> usedIds = new HashSet<>();
        String systemPrompt = """
                You recommend marketplace products similar or relevant to a current product. Return strict JSON only.
                AI rules:
                - Do not invent products.
                - Only choose item IDs from the provided available products.
                - Never recommend unavailable products.
                - Never recommend products owned by the user.
                - Return minimum 3 recommendations whenever at least 3 valid products exist.
                - Return maximum 5 recommendations.
                - Reasons must be written in Arabic.
                JSON format:
                [{"itemId":4,"reason":"سبب عربي قصير"}]
                """;
        String userContent = "Current product:\n"
                + "ID " + currentItem.getId() + " | title: " + currentItem.getTitle() + " | description: " + currentItem.getDescription() + " | type: " + currentItem.getType() + " | price: " + currentItem.getPrice() + " | rentPrice: " + currentItem.getRentPrice()
                + "\n\nUser behavior if available:\n" + String.join("\n", userBehavior)
                + "\n\nAvailable marketplace items:\n" + String.join("\n", availableProductLines)
                + "\n\nRecommend between 3 and 5 products that are most similar or relevant to the currently viewed product.";

        try {
            String aiResponse = openAIService.askAI(systemPrompt, userContent);
            if (aiResponse != null && !"ERROR_FALLBACK".equals(aiResponse)) {
                String json = aiResponse.trim();
                if (json.startsWith("```")) {
                    json = json.replace("```json", "").replace("```", "").trim();
                }
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> aiItems = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
                for (Map<String, Object> aiItem : aiItems) {
                    if (recommendations.size() == 5) {
                        break;
                    }
                    Object rawId = aiItem.get("itemId");
                    Integer recommendedId = rawId instanceof Number ? ((Number) rawId).intValue() : Integer.valueOf(rawId.toString());
                    if (!validItemsById.containsKey(recommendedId) || usedIds.contains(recommendedId)) {
                        continue;
                    }
                    MarketPlaceItem item = validItemsById.get(recommendedId);
                    String reason = aiItem.get("reason") == null ? "منتج مشابه أو مرتبط بالمنتج الذي تشاهده." : aiItem.get("reason").toString();
                    recommendations.add(new MarketplaceRecommendationOutDTO(item.getId(), item.getTitle(), item.getDescription(), item.getType(), item.getPrice(), item.getRentPrice(), item.getDepositAmount(), item.getQuantity(), reason));
                    usedIds.add(recommendedId);
                }
            }
        } catch (Exception ignored) {
        }

        int minimum = Math.min(3, validItemsById.size());
        if (recommendations.size() < minimum) {
            List<MarketPlaceItem> fallbackItems = new ArrayList<>(validItemsById.values());
            fallbackItems.sort((first, second) -> {
                int firstScore = 0;
                int secondScore = 0;
                if (currentItem.getType() != null && currentItem.getType().equals(first.getType())) firstScore += 5;
                if (currentItem.getType() != null && currentItem.getType().equals(second.getType())) secondScore += 5;
                if (behaviorTypes.contains(first.getType())) firstScore += 2;
                if (behaviorTypes.contains(second.getType())) secondScore += 2;
                String firstText = ((first.getTitle() == null ? "" : first.getTitle()) + " " + (first.getDescription() == null ? "" : first.getDescription())).toLowerCase();
                String secondText = ((second.getTitle() == null ? "" : second.getTitle()) + " " + (second.getDescription() == null ? "" : second.getDescription())).toLowerCase();
                for (String keyword : behaviorKeywords) {
                    if (keyword.length() > 2 && firstText.contains(keyword)) firstScore++;
                    if (keyword.length() > 2 && secondText.contains(keyword)) secondScore++;
                }
                return Integer.compare(secondScore, firstScore);
            });

            for (MarketPlaceItem item : fallbackItems) {
                if (recommendations.size() == 5 || recommendations.size() >= minimum && validItemsById.size() >= 3) {
                    break;
                }
                if (usedIds.contains(item.getId())) {
                    continue;
                }
                recommendations.add(new MarketplaceRecommendationOutDTO(item.getId(), item.getTitle(), item.getDescription(), item.getType(), item.getPrice(), item.getRentPrice(), item.getDepositAmount(), item.getQuantity(), "منتج مشابه أو مرتبط بالمنتج الذي تشاهده حاليًا."));
                usedIds.add(item.getId());
            }
        }

        return recommendations;
    }
}
