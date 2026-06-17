package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketPlaceItemRepository extends JpaRepository<MarketPlaceItem, Integer> {
    MarketPlaceItem findMarketPlaceItemById(Integer id);
}
