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

    private GradeCalculator gradeCalculator;
    private List<CalculationPolicy> policies;

    public GradeService() {
        this.policies = new ArrayList<>();
    }

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

            // 🛑 LÓGICA DE SALIDA ANTICIPADA: Si se fija la nota a 0.0 (DPI), se termina el bucle.
            if (currentGrade == GradeConstants.DPI_PENALTY_SCORE) {
                detail.append(policy.getDetail(request)).append(" ");
                break; // Anula cualquier política restante (como el Bonus)
            }

            // Recolección de detalles para políticas que no son DPI (Bono, sin cambio, etc.)
            if (gradeBeforePolicy != currentGrade || policy instanceof AttendancePolicy || policy instanceof BonusPolicy)
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