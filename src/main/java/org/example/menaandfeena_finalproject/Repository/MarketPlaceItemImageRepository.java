package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.MarketPlaceItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketPlaceItemImageRepository extends JpaRepository<MarketPlaceItemImage, Integer> {
    MarketPlaceItemImage findMarketPlaceItemImageById(Integer id);

    List<MarketPlaceItemImage> findMarketPlaceItemImagesByMarketPlaceItemId(Integer marketPlaceItemId);
}
