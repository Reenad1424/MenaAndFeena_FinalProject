package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.example.menaandfeena_finalproject.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MarketPlaceItemRepositoryTest {

    @Autowired
    private MarketPlaceItemRepository marketPlaceItemRepository;

    @Autowired
    private NeighborhoodRepository neighborhoodRepository;

    @Autowired
    private UserRepository userRepository;

    private MarketPlaceItem stroller;

    @BeforeEach
    void setUp() {
        Neighborhood neighborhood = neighborhoodRepository.save(neighborhood());
        User seller = userRepository.save(user(neighborhood));
        stroller = marketPlaceItemRepository.save(item(seller));
    }

    @Test
    void findMarketPlaceItemById_returnsItem() {
        MarketPlaceItem result = marketPlaceItemRepository.findMarketPlaceItemById(stroller.getId());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Stroller");
    }

    private Neighborhood neighborhood() {
        Neighborhood neighborhood = new Neighborhood();
        neighborhood.setName("Al Narjis");
        neighborhood.setCity("Riyadh");
        neighborhood.setLatitude(24.8391);
        neighborhood.setLongitude(46.7244);
        return neighborhood;
    }

    private User user(Neighborhood neighborhood) {
        User user = new User();
        user.setFullName("Faisal Al Qahtani");
        user.setEmail("seller.market@example.com");
        user.setPassword("Password123");
        user.setPhone("+966555555555");
        user.setNationalId("1888888881");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setGender("MALE");
        user.setNeighborhood(neighborhood);
        user.setCreatedAt(LocalDate.of(2025, 1, 1));
        return user;
    }

    private MarketPlaceItem item(User seller) {
        MarketPlaceItem item = new MarketPlaceItem();
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
