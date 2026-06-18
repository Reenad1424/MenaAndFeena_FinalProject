package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter @Getter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "int not null")
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Integer amount;

    @Column(columnDefinition = "int not null")
    @NotNull(message = "Platform fee cannot be null")
    @PositiveOrZero(message = "Platform fee must be zero or positive")
    private Integer platformFee;

    @Column(columnDefinition = "int not null")
    @NotNull(message = "Seller amount cannot be null")
    @PositiveOrZero(message = "Seller amount must be zero or positive")
    private Integer sellerAmount;

    // PENDING, PAID, FAILED, REFUNDED
    @Column(columnDefinition = "varchar(20) not null")
    @NotBlank(message = "Status cannot be blank")
    @Pattern(regexp = "PENDING|PAID|FAILED|REFUNDED", message = "Status must be PENDING, PAID, FAILED, or REFUNDED")
    private String status;

    @Column(columnDefinition = "varchar(100) not null")
    private String transactionId;

    @Column(columnDefinition = "varchar(500)")
    private String paymentUrl;

    @Column(columnDefinition = "varchar(255)")
    private String description;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @JsonIgnore
    private Orders orders;
}
