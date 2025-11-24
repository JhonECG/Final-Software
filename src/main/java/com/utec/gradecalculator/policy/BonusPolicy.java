package com.utec.gradecalculator.policy;

import com.utec.gradecalculator.constants.GradeConstants;
import com.utec.gradecalculator.dto.CalculationRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class BonusPolicy implements CalculationPolicy {

    @Override
    public double apply(double currentGrade, CalculationRequestDTO request) {
        // Regla: Si los docentes están de acuerdo, se aplica el bono.
        if (request.isAllYearsTeachers()) {
            double gradeWithBonus = currentGrade + GradeConstants.BONUS_POINTS;
            // Tope máximo (RNF03: Determinismo y Regla Académica)
            return Math.min(gradeWithBonus, GradeConstants.MAX_SCORE);
        }
        return currentGrade;
    }

    @Override
    public String getDetail(CalculationRequestDTO request) {
        if (request.isAllYearsTeachers()) {
            return "BONO EXTRA: Aplicado +" + GradeConstants.BONUS_POINTS + " punto(s) por acuerdo docente.";
        }
        return "BONO EXTRA: Política no activa para este periodo.";
    }
}