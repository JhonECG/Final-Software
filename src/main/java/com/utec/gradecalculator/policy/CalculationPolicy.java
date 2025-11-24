package com.utec.gradecalculator.policy;

import com.utec.gradecalculator.dto.CalculationRequestDTO;

// La interfaz Strategy que todas las políticas implementarán
public interface CalculationPolicy {

    // El método principal que aplica la regla a la nota actual
    double apply(double currentGrade, CalculationRequestDTO request);

    // Método para describir qué hizo la política (para RF05: Detalle del cálculo)
    String getDetail(CalculationRequestDTO request);
}