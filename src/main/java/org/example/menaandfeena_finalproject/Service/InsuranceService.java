package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.Out.InsuranceOutDTO;
import org.example.menaandfeena_finalproject.Model.Insurance;
import org.example.menaandfeena_finalproject.Repository.InsuranceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// Insurance is created automatically by the order/checkout flow and activated/refunded/deducted
// by the owner-confirmation methods in OrderItemService. Clients cannot create or edit insurance
// rows directly, so addInsurance/updateInsurance were removed. Only admin read/delete remain here.
@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;

    public List<InsuranceOutDTO> getAllInsurances() {
        List<InsuranceOutDTO> insuranceOutDTOS = new ArrayList<>();

        for (Insurance insurance : insuranceRepository.findAll()) {
            Integer orderItemId = insurance.getOrderItem() == null ? null : insurance.getOrderItem().getId();
            insuranceOutDTOS.add(new InsuranceOutDTO(insurance.getId(), insurance.getDepositAmount(), insurance.getRefundedAmount(), insurance.getStatus(), insurance.getHeldAt(), insurance.getRefundedAt(), insurance.getRefundTransactionId(), orderItemId));
        }

        return insuranceOutDTOS;
    }

    public void deleteInsurance(Integer id) {
        Insurance insurance = insuranceRepository.findInsuranceById(id);

        if (insurance == null) {
            throw new ApiException("Insurance not found");
        }

        insuranceRepository.delete(insurance);
    }
}
