package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findPaymentById(Integer id);
    Payment findPaymentByTransactionId(String transactionId);
}
