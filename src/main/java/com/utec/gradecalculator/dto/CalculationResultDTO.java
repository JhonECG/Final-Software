package com.utec.gradecalculator.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculationResultDTO {
    private String studentCode;
    private double finalGrade;
    private String status; // APROBADO, DESAPROBADO, DPI
    // RF05: Detalle del cálculo
    private String calculationDetail;
}