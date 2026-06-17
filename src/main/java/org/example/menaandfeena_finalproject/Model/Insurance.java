package org.example.menaandfeena_finalproject.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter @Getter
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "int not null")
    private double depositAmount;

    @Column(columnDefinition = "int not null")
    private double refundedAmount;

    // HELD, REFUNDED, DEDUCTED
    @Column(columnDefinition = "varchar(10) not null")
    private String status;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Orders orders;
}
