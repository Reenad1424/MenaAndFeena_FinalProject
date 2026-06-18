package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
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
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @Column(columnDefinition = "varchar(200) not null")
    @NotBlank(message = "Description cannot be blank")
    private String description;

    // SELL, RENT
    @Column(columnDefinition = "varchar(10) not null")
    @NotBlank(message = "Type cannot be blank")
    @Pattern(regexp = "SELL|RENT", message = "Type must be SELL or RENT")
    private String type;

    // AVAILABLE, SOLD, RENTED
    @Column(columnDefinition = "varchar(10) not null")
    @NotBlank(message = "Status cannot be blank")
    @Pattern(regexp = "AVAILABLE|SOLD|RENTED", message = "Status must be AVAILABLE, SOLD or RENTED")
    private String status;

    @Column(columnDefinition = "int")
    private Integer price;

    @Column(columnDefinition = "int")
    private Integer rentPrice;

    @Column(columnDefinition = "int")
    private Integer depositAmount;

    @Column(columnDefinition = "int not null")
    @NotNull(message = "Quantity cannot be null")
    @PositiveOrZero(message = "Quantity must be zero or positive")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "marketPlaceItem", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Orders> orders;

    @OneToMany(mappedBy = "marketPlaceItem", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Inquiry> inquiries;
}
