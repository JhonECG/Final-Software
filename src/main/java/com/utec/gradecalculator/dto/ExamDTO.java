package com.utec.gradecalculator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamDTO {
    private double score;      // Nota (0-20)
    private double weight;     // Peso (0.0 - 1.0, ej: 0.30 para 30%)
}