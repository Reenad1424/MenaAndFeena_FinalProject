package org.example.menaandfeena_finalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor
@Setter @Getter
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(50) not null")
    private String invoiceNumber;

    @Column(columnDefinition = "date not null")
    private LocalDate orderDate;

    // PENDING_PAYMENT, PAYMENT_FAILED, PAID, ACTIVE, COMPLETED, CANCELLED
    @Column(columnDefinition = "varchar(30) not null")
    private String status;

    @Column(columnDefinition = "int not null")
    private Integer totalAmount;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;


    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    private Payment payment;
}
