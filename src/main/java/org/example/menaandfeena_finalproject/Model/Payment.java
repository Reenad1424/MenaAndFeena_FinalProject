package org.example.menaandfeena_finalproject.Model;

import jakarta.persistence.*;
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
    private double amount;

    @Column(columnDefinition = "int not null")
    private double platformFee;

    @Column(columnDefinition = "int not null")
    private double sellerAmount;

    // PENDING, PAID, FAILED
    @Column(columnDefinition = "varchar(10) not null")
    private String status;

    @Column(columnDefinition = "varchar(100) not null")
    private String transactionId;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Orders orders;
}
