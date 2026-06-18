package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.InsuranceInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.InsuranceOutDTO;
import org.example.menaandfeena_finalproject.Model.Insurance;
import org.example.menaandfeena_finalproject.Repository.InsuranceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;

    public List<InsuranceOutDTO> getAllInsurances() {
        List<InsuranceOutDTO> insuranceOutDTOS = new ArrayList<>();

        for (Insurance insurance : insuranceRepository.findAll()) {
            insuranceOutDTOS.add(toOutDTO(insurance));
        }

        return insuranceOutDTOS;
    }

    public void addInsurance(InsuranceInDTO insuranceInDTO) {
        Insurance insurance = new Insurance();
        insurance.setDepositAmount(insuranceInDTO.getDepositAmount());
        insurance.setRefundedAmount(insuranceInDTO.getRefundedAmount());
        insurance.setStatus(insuranceInDTO.getStatus());
        insurance.setHeldAt(insuranceInDTO.getHeldAt());
        insurance.setRefundedAt(insuranceInDTO.getRefundedAt());
        insurance.setRefundTransactionId(insuranceInDTO.getRefundTransactionId());

        insuranceRepository.save(insurance);
    }

    public void updateInsurance(Integer id, InsuranceInDTO insuranceInDTO) {
        Insurance oldInsurance = insuranceRepository.findInsuranceById(id);

        if (oldInsurance == null) {
            throw new ApiException("Insurance not found");
        }

        oldInsurance.setDepositAmount(insuranceInDTO.getDepositAmount());
        oldInsurance.setRefundedAmount(insuranceInDTO.getRefundedAmount());
        oldInsurance.setStatus(insuranceInDTO.getStatus());
        oldInsurance.setHeldAt(insuranceInDTO.getHeldAt());
        oldInsurance.setRefundedAt(insuranceInDTO.getRefundedAt());
        oldInsurance.setRefundTransactionId(insuranceInDTO.getRefundTransactionId());

        insuranceRepository.save(oldInsurance);
    }

    public void deleteInsurance(Integer id) {
        Insurance insurance = insuranceRepository.findInsuranceById(id);

        if (insurance == null) {
            throw new ApiException("Insurance not found");
        }

        insuranceRepository.delete(insurance);
    }

    private InsuranceOutDTO toOutDTO(Insurance insurance) {
        return new InsuranceOutDTO(insurance.getId(), insurance.getDepositAmount(), insurance.getRefundedAmount(), insurance.getStatus(), insurance.getHeldAt(), insurance.getRefundedAt(), insurance.getRefundTransactionId());
    }
}
