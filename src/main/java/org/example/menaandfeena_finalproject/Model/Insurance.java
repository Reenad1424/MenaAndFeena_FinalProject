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

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Setter @Getter
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "int not null")
    @NotNull(message = "Deposit amount cannot be null")
    @PositiveOrZero(message = "Deposit amount must be zero or positive")
    private Integer depositAmount;

    @Column(columnDefinition = "int not null")
    @NotNull(message = "Refunded amount cannot be null")
    @PositiveOrZero(message = "Refunded amount must be zero or positive")
    private Integer refundedAmount;

    // HELD, REFUNDED, DEDUCTED
    @Column(columnDefinition = "varchar(20) not null")
    @NotBlank(message = "Status cannot be blank")
    @Pattern(regexp = "HELD|REFUNDED|DEDUCTED", message = "Status must be HELD, REFUNDED, or DEDUCTED")
    private String status;

    private LocalDateTime heldAt;

    private LocalDateTime refundedAt;

    @Column(columnDefinition = "varchar(100)")
    private String refundTransactionId;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @JsonIgnore
    private Orders orders;
}
