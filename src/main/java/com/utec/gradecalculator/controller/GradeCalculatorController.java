package com.utec.gradecalculator.controller;

import com.utec.gradecalculator.dto.CalculationRequestDTO;
import com.utec.gradecalculator.dto.CalculationResultDTO;
import com.utec.gradecalculator.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeCalculatorController {

    private final GradeService gradeService;

    // RF04: Solicitar cálculo
    @PostMapping("/calculate")
    public ResponseEntity<?> calculateGrade(@RequestBody CalculationRequestDTO request) {
        long startTime = System.currentTimeMillis();

        try {
            CalculationResultDTO result = gradeService.calculateFinalGrade(request);

            // RNF04: Monitoreo simple del tiempo (solo para log)
            long duration = System.currentTimeMillis() - startTime;
            if (duration > 300) {
                System.out.println("ALERTA RNF04: El cálculo tomó " + duration + "ms");
            }

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}