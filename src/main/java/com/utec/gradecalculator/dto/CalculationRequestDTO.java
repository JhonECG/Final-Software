package com.utec.gradecalculator.dto;

import lombok.Data;
import java.util.List;

@Data
public class CalculationRequestDTO {
    private String studentCode;
    // RF01: Lista de evaluaciones
    private List<ExamDTO> examsStudents;
    // RF02: Asistencia mínima
    private boolean hasReachedMinimumClasses;
    // RF03: Política de puntos extra
    private boolean allYearsTeachers;
}