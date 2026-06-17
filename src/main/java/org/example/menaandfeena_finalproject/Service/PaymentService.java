package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.PaymentInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.PaymentOutDTO;
import org.example.menaandfeena_finalproject.Model.Payment;
import org.example.menaandfeena_finalproject.Repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public List<PaymentOutDTO> getAllPayments() {
        List<PaymentOutDTO> paymentOutDTOS = new ArrayList<>();

        for (Payment payment : paymentRepository.findAll()) {
            paymentOutDTOS.add(toOutDTO(payment));
        }

        return paymentOutDTOS;
    }

    public void addPayment(PaymentInDTO paymentInDTO) {
        Payment payment = new Payment();
        payment.setAmount(paymentInDTO.getAmount());
        payment.setPlatformFee(paymentInDTO.getPlatformFee());
        payment.setSellerAmount(paymentInDTO.getSellerAmount());
        payment.setStatus(paymentInDTO.getStatus());
        payment.setTransactionId(paymentInDTO.getTransactionId());

        paymentRepository.save(payment);
    }

    public void updatePayment(Integer id, PaymentInDTO paymentInDTO) {
        Payment oldPayment = paymentRepository.findPaymentById(id);

        if (oldPayment == null) {
            throw new ApiException("Payment not found");
        }

        oldPayment.setAmount(paymentInDTO.getAmount());
        oldPayment.setPlatformFee(paymentInDTO.getPlatformFee());
        oldPayment.setSellerAmount(paymentInDTO.getSellerAmount());
        oldPayment.setStatus(paymentInDTO.getStatus());
        oldPayment.setTransactionId(paymentInDTO.getTransactionId());

        paymentRepository.save(oldPayment);
    }

    public void deletePayment(Integer id) {
        Payment payment = paymentRepository.findPaymentById(id);

        if (payment == null) {
            throw new ApiException("Payment not found");
        }

        paymentRepository.delete(payment);
    }

    private PaymentOutDTO toOutDTO(Payment payment) {
        return new PaymentOutDTO(payment.getId(), payment.getAmount(), payment.getPlatformFee(), payment.getSellerAmount(), payment.getStatus(), payment.getTransactionId());
    }
}
