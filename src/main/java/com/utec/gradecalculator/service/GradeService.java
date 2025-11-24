package com.utec.gradecalculator.service;

import com.utec.gradecalculator.calculator.GradeCalculator;
import com.utec.gradecalculator.constants.GradeConstants;
import com.utec.gradecalculator.dto.CalculationRequestDTO;
import com.utec.gradecalculator.dto.CalculationResultDTO;
import com.utec.gradecalculator.exception.ValidationException;
import com.utec.gradecalculator.policy.AttendancePolicy;
import com.utec.gradecalculator.policy.BonusPolicy;
import com.utec.gradecalculator.policy.CalculationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class GradeService {

    // Dependencias inyectadas (sin la palabra 'final')
    private GradeCalculator gradeCalculator;
    private List<CalculationPolicy> policies;

    // 1. Constructor Vacío (No-Arg) -> Para compatibilidad con el Test Runner
    public GradeService() {
        this.policies = new ArrayList<>();
    }

    // 2. Constructor para Spring DI -> Usa @Autowired para la inyección de dependencias
    @Autowired
    public GradeService(GradeCalculator gradeCalculator, List<CalculationPolicy> policies) {
        this.gradeCalculator = gradeCalculator;
        this.policies = policies;
    }

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
            double gradeBeforePolicy = currentGrade;
            currentGrade = policy.apply(currentGrade, request);

            // 🛑 CORRECCIÓN FINAL: Incluimos BonusPolicy en la condición para asegurar que el detalle (activo/inactivo) siempre se muestre.
            if (gradeBeforePolicy != currentGrade ||
                    policy instanceof AttendancePolicy ||
                    policy instanceof BonusPolicy)
            {
                detail.append(policy.getDetail(request)).append(" ");
            }
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