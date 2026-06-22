package org.example.menaandfeena_finalproject.Service;

import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.MarketPlaceItemInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MarketPlaceItemOutDTO;
import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.MarketPlaceItemRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketPlaceItemServiceTest {

    @Mock
    private MarketPlaceItemRepository marketPlaceItemRepository;
    @Mock
    private UserRepository userRepository;

    private MarketPlaceItemService marketPlaceItemService;

    private Neighborhood neighborhood;
    private User seller;
    private User buyer;
    private MarketPlaceItem stroller;

    @BeforeEach
    void setUp() {
        neighborhood = new Neighborhood();
        neighborhood.setId(1);
        neighborhood.setName("Al Narjis");
        neighborhood.setCity("Riyadh");

        seller = user(20, "Faisal Al Qahtani");
        buyer = user(30, "Sara Alotaibi");
        stroller = item(100, seller);

        marketPlaceItemService = new MarketPlaceItemService(
                marketPlaceItemRepository,
                userRepository,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    @Test
    void getAllMarketPlaceItems_returnsAllItems() {
        when(marketPlaceItemRepository.findAll()).thenReturn(List.of(stroller));

        List<MarketPlaceItemOutDTO> result = marketPlaceItemService.getAllMarketPlaceItems();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Stroller");
        verify(marketPlaceItemRepository).findAll();
    }

    @Test
    void addMarketPlaceItem_savesSellItem() {
        MarketPlaceItemInDTO dto = new MarketPlaceItemInDTO("Stroller", "Clean stroller", "SELL", 280, null, null, 1);
        when(userRepository.findUserById(seller.getId())).thenReturn(seller);

        marketPlaceItemService.addMarketPlaceItem(seller.getId(), dto);

        verify(userRepository).findUserById(seller.getId());
        verify(marketPlaceItemRepository).save(any(MarketPlaceItem.class));
    }

    @Test
    void deleteMarketPlaceItem_throwsWhenRequesterIsNotOwner() {
        when(marketPlaceItemRepository.findMarketPlaceItemById(stroller.getId())).thenReturn(stroller);
        when(userRepository.findUserById(buyer.getId())).thenReturn(buyer);

        assertThatThrownBy(() -> marketPlaceItemService.deleteMarketPlaceItem(stroller.getId(), buyer.getId()))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Only the item owner can delete item");

        verify(marketPlaceItemRepository).findMarketPlaceItemById(stroller.getId());
        verify(userRepository).findUserById(buyer.getId());
        verify(marketPlaceItemRepository, never()).delete(any(MarketPlaceItem.class));
    }

    private User user(Integer id, String fullName) {
        User user = new User();
        user.setId(id);
        user.setFullName(fullName);
        user.setEmail(fullName.toLowerCase().replace(" ", ".") + "@example.com");
        user.setPassword("Password123");
        user.setPhone("+966555555555");
        user.setNationalId("1" + id + "0000000");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setGender("MALE");
        user.setNeighborhood(neighborhood);
        user.setCreatedAt(LocalDate.of(2025, 1, 1));
        return user;
    }

    private MarketPlaceItem item(Integer id, User seller) {
        MarketPlaceItem item = new MarketPlaceItem();
        item.setId(id);
        item.setTitle("Stroller");
        item.setDescription("Clean stroller in excellent condition");
        item.setType("SELL");
        item.setStatus("AVAILABLE");
        item.setPrice(280);
        item.setQuantity(1);
        item.setUser(seller);
        return item;
    }
}
