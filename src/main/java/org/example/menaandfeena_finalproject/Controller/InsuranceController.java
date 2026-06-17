package org.example.menaandfeena_finalproject.Controller;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.InsuranceInDTO;
import org.example.menaandfeena_finalproject.Service.InsuranceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/insurance")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService insuranceService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllInsurances() {
        return ResponseEntity.status(200).body(insuranceService.getAllInsurances());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addInsurance(@RequestBody InsuranceInDTO insuranceInDTO) {
        insuranceService.addInsurance(insuranceInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Insurance added"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateInsurance(@PathVariable Integer id, @RequestBody InsuranceInDTO insuranceInDTO) {
        insuranceService.updateInsurance(id, insuranceInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Insurance updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInsurance(@PathVariable Integer id) {
        insuranceService.deleteInsurance(id);
        return ResponseEntity.status(200).body(new ApiResponse("Insurance deleted"));
    }
}
