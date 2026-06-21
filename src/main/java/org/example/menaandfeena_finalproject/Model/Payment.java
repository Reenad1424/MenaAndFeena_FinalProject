package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Integer amount;

    @Column(columnDefinition = "int not null")
    private Integer platformFee;

    @Column(columnDefinition = "int not null")
    private Integer sellerAmount;

    // INITIATED, PENDING, PAID, FAILED, REFUNDED
    @Column(columnDefinition = "varchar(20) not null")
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

    @OneToOne
    @JoinColumn(name = "event_registration_id")
    @JsonIgnore
    private EventRegistration eventRegistration;



}
