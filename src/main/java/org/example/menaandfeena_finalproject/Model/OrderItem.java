package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Setter @Getter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(10) not null")
    private String type;

    @Column(columnDefinition = "int not null")
    private Integer quantity;

    @Column(columnDefinition = "int not null")
    private Integer unitPrice;

    @Column(columnDefinition = "int")
    private Integer rentalDays;

    @Column(columnDefinition = "int")
    private Integer depositAmount;

    @Column(columnDefinition = "int not null")
    private Integer subtotal;

    @Column(columnDefinition = "date")
    private LocalDate startDate;

    @Column(columnDefinition = "date")
    private LocalDate endDate;

    private Boolean renterConfirmedReturn = false;

    private Boolean ownerConfirmedReturn = false;

    private LocalDateTime renterConfirmedReturnAt;

    private LocalDateTime ownerConfirmedReturnAt;

    // NOT_RETURNED, WAITING_OWNER_CONFIRMATION, RETURNED, DAMAGED
    @Column(columnDefinition = "varchar(30)")
    private String returnStatus;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @JsonIgnore
    private Orders orders;

    @ManyToOne
    @JoinColumn(name = "marketplace_item_id", referencedColumnName = "id")
    @JsonIgnore
    private MarketPlaceItem marketPlaceItem;

    @OneToOne(mappedBy = "orderItem", cascade = CascadeType.ALL)
    private Insurance insurance;
}
