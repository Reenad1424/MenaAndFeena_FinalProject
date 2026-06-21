package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.AddCartItemInDTO;
import org.example.menaandfeena_finalproject.DTO.In.UpdateCartItemRentalDaysInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.CartItemOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.CartOutDTO;
import org.example.menaandfeena_finalproject.Model.Cart;
import org.example.menaandfeena_finalproject.Model.CartItem;
import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.CartItemRepository;
import org.example.menaandfeena_finalproject.Repository.CartRepository;
import org.example.menaandfeena_finalproject.Repository.MarketPlaceItemRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final MarketPlaceItemRepository marketPlaceItemRepository;

    public List<CartOutDTO> getAllCarts() {
        List<CartOutDTO> cartOutDTOS = new ArrayList<>();

        for (Cart cart : cartRepository.findAll()) {
            Integer userId = cart.getUser() == null ? null : cart.getUser().getId();
            List<CartItemOutDTO> cartItems = new ArrayList<>();

            for (CartItem cartItem : cartItemRepository.findCartItemsByCartId(cart.getId())) {
                Integer cartId = cartItem.getCart() == null ? null : cartItem.getCart().getId();
                Integer marketPlaceItemId = cartItem.getMarketPlaceItem() == null ? null : cartItem.getMarketPlaceItem().getId();
                cartItems.add(new CartItemOutDTO(cartItem.getId(), cartItem.getQuantity(), cartItem.getRentalDays(), cartItem.getStartDate(), cartItem.getEndDate(), cartId, marketPlaceItemId));
            }

            cartOutDTOS.add(new CartOutDTO(cart.getId(), userId, cartItems));
        }

        return cartOutDTOS;
    }

    public CartOutDTO viewCart(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        Cart cart = cartRepository.findCartByUserId(user.getId());

        if (cart == null) {
            throw new ApiException("Cart not found");
        }

        List<CartItemOutDTO> cartItems = new ArrayList<>();
        for (CartItem cartItem : cartItemRepository.findCartItemsByCartId(cart.getId())) {
            MarketPlaceItem item = cartItem.getMarketPlaceItem();
            if (item == null) {
                throw new ApiException("Market place item not found");
            }
            if (item.getUser() == null || item.getUser().getNeighborhood() == null) {
                throw new ApiException("Market place item owner neighborhood is required");
            }
            if (!item.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
                throw new ApiException("Cart contains an item outside your neighborhood");
            }
            Integer cartId = cartItem.getCart() == null ? null : cartItem.getCart().getId();
            Integer marketPlaceItemId = cartItem.getMarketPlaceItem() == null ? null : cartItem.getMarketPlaceItem().getId();
            cartItems.add(new CartItemOutDTO(cartItem.getId(), cartItem.getQuantity(), cartItem.getRentalDays(), cartItem.getStartDate(), cartItem.getEndDate(), cartId, marketPlaceItemId));
        }

        return new CartOutDTO(cart.getId(), user.getId(), cartItems);
    }

    public void addCart(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        if (cartRepository.findCartByUserId(user.getId()) != null) {
            throw new ApiException("User already has a cart");
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);
    }

    public void deleteCart(Integer id) {
        Cart cart = cartRepository.findCartById(id);
        if (cart == null) {
            throw new ApiException("Cart not found");
        }

        cartRepository.delete(cart);
    }

    public CartOutDTO addItemToCart(Integer userId, AddCartItemInDTO addCartItemInDTO) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }

        Cart cart = cartRepository.findCartByUserId(user.getId());

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }

        MarketPlaceItem item = marketPlaceItemRepository.findMarketPlaceItemById(addCartItemInDTO.getMarketPlaceItemId());
        if (item == null) {
            throw new ApiException("Market place item not found");
        }
        if (item.getUser() == null || item.getUser().getNeighborhood() == null) {
            throw new ApiException("Market place item owner neighborhood is required");
        }
        if (!item.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Market place item is outside your neighborhood");
        }

        if (!item.getStatus().equals("AVAILABLE")) {
            throw new ApiException("Item is not available");
        }

        if (item.getQuantity() == null || item.getQuantity() < addCartItemInDTO.getQuantity()) {
            throw new ApiException("Item quantity is unavailable");
        }

        if (item.getType().equals("RENT")) {
            if (addCartItemInDTO.getRentalDays() == null || addCartItemInDTO.getRentalDays() <= 0) {
                throw new ApiException("Rental days must be greater than zero for rent items");
            }
        } else {
            if (addCartItemInDTO.getRentalDays() != null) {
                throw new ApiException("Rental days must be null for sell items");
            }
        }

        LocalDate startDate = null;
        LocalDate endDate = null;
        if (item.getType().equals("RENT")) {
            startDate = LocalDate.now();
            endDate = startDate.plusDays(addCartItemInDTO.getRentalDays());
        }

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setMarketPlaceItem(item);
        cartItem.setQuantity(addCartItemInDTO.getQuantity());
        cartItem.setRentalDays(addCartItemInDTO.getRentalDays());
        cartItem.setStartDate(startDate);
        cartItem.setEndDate(endDate);
        cartItemRepository.save(cartItem);

        List<CartItemOutDTO> cartItems = new ArrayList<>();
        for (CartItem itemInCart : cartItemRepository.findCartItemsByCartId(cart.getId())) {
            Integer cartId = itemInCart.getCart() == null ? null : itemInCart.getCart().getId();
            Integer marketPlaceItemId = itemInCart.getMarketPlaceItem() == null ? null : itemInCart.getMarketPlaceItem().getId();
            cartItems.add(new CartItemOutDTO(itemInCart.getId(), itemInCart.getQuantity(), itemInCart.getRentalDays(), itemInCart.getStartDate(), itemInCart.getEndDate(), cartId, marketPlaceItemId));
        }

        return new CartOutDTO(cart.getId(), user.getId(), cartItems);
    }

    public void removeItemFromCart(Integer userId, Integer cartItemId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }

        CartItem cartItem = cartItemRepository.findCartItemById(cartItemId);

        if (cartItem == null) {
            throw new ApiException("Cart item not found");
        }
        if (cartItem.getCart() == null || cartItem.getCart().getUser() == null || !cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new ApiException("Cart item does not belong to this user");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }
        MarketPlaceItem item = cartItem.getMarketPlaceItem();
        if (item == null) {
            throw new ApiException("Market place item not found");
        }
        if (item.getUser() == null || item.getUser().getNeighborhood() == null || !item.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Market place item is outside your neighborhood");
        }

        cartItemRepository.delete(cartItem);
    }

    public void updateRentalDays(Integer userId, Integer cartItemId, UpdateCartItemRentalDaysInDTO updateCartItemRentalDaysInDTO) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }

        CartItem cartItem = cartItemRepository.findCartItemById(cartItemId);

        if (cartItem == null) {
            throw new ApiException("Cart item not found");
        }
        if (cartItem.getCart() == null || cartItem.getCart().getUser() == null || !cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new ApiException("Cart item does not belong to this user");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }
        MarketPlaceItem item = cartItem.getMarketPlaceItem();
        if (item == null) {
            throw new ApiException("Market place item not found");
        }
        if (item.getUser() == null || item.getUser().getNeighborhood() == null || !item.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Market place item is outside your neighborhood");
        }

        if (!item.getType().equals("RENT")) {
            throw new ApiException("Rental days can only be updated for rent items");
        }
        if (updateCartItemRentalDaysInDTO.getRentalDays() == null || updateCartItemRentalDaysInDTO.getRentalDays() <= 0) {
            throw new ApiException("Rental days must be greater than zero");
        }

        LocalDate startDate = LocalDate.now();
        cartItem.setRentalDays(updateCartItemRentalDaysInDTO.getRentalDays());
        cartItem.setStartDate(startDate);
        cartItem.setEndDate(startDate.plusDays(updateCartItemRentalDaysInDTO.getRentalDays()));
        cartItemRepository.save(cartItem);
    }
}
