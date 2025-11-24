package com.utec.gradecalculator.policy;

import com.utec.gradecalculator.constants.GradeConstants;
import com.utec.gradecalculator.dto.CalculationRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class AttendancePolicy implements CalculationPolicy {

    @Override
    public double apply(double currentGrade, CalculationRequestDTO request) {
        // Regla: Si no cumple asistencia mínima, la nota final es 0.0 (DPI)
        if (!request.isHasReachedMinimumClasses()) {
            return GradeConstants.DPI_PENALTY_SCORE;
        }
        return currentGrade;
    }

    @Override
    public String getDetail(CalculationRequestDTO request) {
        if (!request.isHasReachedMinimumClasses()) {
            return "PENALIZACIÓN ASISTENCIA: No cumplió el mínimo. Nota ajustada a " + GradeConstants.DPI_PENALTY_SCORE;
        }
        return "ASISTENCIA: Requisito mínimo cumplido.";
    }
}