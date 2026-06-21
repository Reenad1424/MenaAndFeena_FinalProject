package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter @Getter
public class MarketPlaceItemImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(500) not null")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "marketplace_item_id", referencedColumnName = "id")
    @JsonIgnore
    private MarketPlaceItem marketPlaceItem;
}
