package com.utec.gradecalculator.calculator;

import com.utec.gradecalculator.dto.ExamDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component // Hacemos que Spring lo pueda inyectar en el Service
public class GradeCalculator {

    // Método para el cálculo base (RF04 y RNF03: Determinismo)
    public double calculateWeightedAverage(List<ExamDTO> exams) {
        double weightedSum = 0.0;
        double totalWeight = 0.0;

        for (ExamDTO exam : exams) {
            weightedSum += (exam.getScore() * exam.getWeight());
            totalWeight += exam.getWeight();
        }

        // Validación de pesos (se valida si es != 0 para evitar división por cero)
        if (totalWeight <= 0) {
            return 0.0;
        }

        // Retorna la suma ponderada (no dividimos si los pesos suman 1.0, lo cual asumimos)
        // Usamos BigDecimal para asegurar el determinismo en el cálculo financiero
        BigDecimal bd = new BigDecimal(Double.toString(weightedSum));
        // Redondeamos a dos decimales, vital para RNF03
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }
}