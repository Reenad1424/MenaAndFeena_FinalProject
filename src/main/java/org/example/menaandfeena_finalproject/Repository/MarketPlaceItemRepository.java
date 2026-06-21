package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.MarketPlaceItem;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MarketPlaceItemRepository extends JpaRepository<MarketPlaceItem, Integer> {

    MarketPlaceItem findMarketPlaceItemById(Integer id);

    List<MarketPlaceItem> findMarketPlaceItemsByType(String type);

    List<MarketPlaceItem> findMarketPlaceItemsByUserId(Integer userId);

    List<MarketPlaceItem> findTop5ByUser_Neighborhood(Neighborhood neighborhood);

    Integer countByUser_Neighborhood(Neighborhood neighborhood);
}