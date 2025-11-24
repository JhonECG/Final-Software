package com.utec.gradecalculator.service;

import com.utec.gradecalculator.calculator.GradeCalculator;
import com.utec.gradecalculator.constants.GradeConstants;
import com.utec.gradecalculator.dto.CalculationRequestDTO;
import com.utec.gradecalculator.dto.CalculationResultDTO;
import com.utec.gradecalculator.exception.ValidationException;
import com.utec.gradecalculator.policy.CalculationPolicy;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@NoArgsConstructor
public class GradeService {

    private GradeCalculator gradeCalculator;
    private List<CalculationPolicy> policies;

    public CalculationResultDTO calculateFinalGrade(CalculationRequestDTO request) {
        StringBuilder detail = new StringBuilder();

        // RNF01: Validación de cantidad máxima de evaluaciones (Max 10)
        if (request.getExamsStudents() == null || request.getExamsStudents().size() > GradeConstants.EXAMS_LIMIT) {
            throw new ValidationException("RNF01 Error: El número máximo de evaluaciones permitidas es " + GradeConstants.EXAMS_LIMIT + ".");
        }

        // 1. Cálculo del promedio ponderado base (RF04)
        double currentGrade = gradeCalculator.calculateWeightedAverage(request.getExamsStudents());
        detail.append(String.format("Promedio Ponderado Base: %.2f. ", currentGrade));

        // 2. Aplicación de Políticas (Patrón Strategy)
        for (CalculationPolicy policy : policies) {
            // Aplicamos la política al resultado actual
            currentGrade = policy.apply(currentGrade, request);

            // Recopilamos el detalle (RF05)
            detail.append(policy.getDetail(request)).append(" ");
        }

        // 3. Determinación del Estado Final (RF05)
        String status = currentGrade >= GradeConstants.PASS_THRESHOLD ? "APROBADO" : "DESAPROBADO";

        return CalculationResultDTO.builder()
                .studentCode(request.getStudentCode())
                .finalGrade(currentGrade)
                .status(status)
                .calculationDetail(detail.toString().trim())
                .build();
    }
}