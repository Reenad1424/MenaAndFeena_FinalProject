package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    private Integer depositAmount;

    @Column(columnDefinition = "int not null")
    private Integer refundedAmount;

    // HELD, REFUNDED, DEDUCTED
    @Column(columnDefinition = "varchar(20) not null")
    private String status;

    private LocalDateTime heldAt;

    private LocalDateTime refundedAt;

    @Column(columnDefinition = "varchar(100)")
    private String refundTransactionId;

    @OneToOne
    @JoinColumn(name = "order_item_id", referencedColumnName = "id")
    @JsonIgnore
    private OrderItem orderItem;
}
