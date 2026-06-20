package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Setter @Getter
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // PURCHASE, RENT, BORROW
    @Column(columnDefinition = "varchar(10) not null")
    private String type;

    // PENDING, PAID, ACTIVE, COMPLETED, CANCELLED
    @Column(columnDefinition = "varchar(10) not null")
    private String status;

    @Column(columnDefinition = "int not null")
    @NotNull(message = "Total amount cannot be null")
    @Positive(message = "Total amount must be positive")
    private Integer totalAmount;

    @Column(columnDefinition = "date")
    private LocalDate startDate;

    @Column(columnDefinition = "date")
    private LocalDate endDate;

    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "marketplace_item_id", referencedColumnName = "id")
    private MarketPlaceItem marketPlaceItem;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;


    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    private Insurance insurance;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;
}
