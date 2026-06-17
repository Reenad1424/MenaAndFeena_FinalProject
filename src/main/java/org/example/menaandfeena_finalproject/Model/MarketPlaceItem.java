package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@NoArgsConstructor
@Setter @Getter
public class MarketPlaceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(20) not null")
    private String title;

    @Column(columnDefinition = "varchar(200) not null")
    private String description;

    // SELL, RENT, BORROW
    @Column(columnDefinition = "varchar(10) not null")
    private String type;

    // AVAILABLE, SOLD, RENTED
    @Column(columnDefinition = "varchar(10) not null")
    private String status;

    @Column(columnDefinition = "int not null")
    private double price;

    @Column(columnDefinition = "int not null")
    private double rentPrice;

    @Column(columnDefinition = "int not null")
    private double depositAmount;

    @Column(columnDefinition = "int not null")
    private Integer quantity;


    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;


    @OneToMany(mappedBy = "marketPlaceItem", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Orders> orders;
}
