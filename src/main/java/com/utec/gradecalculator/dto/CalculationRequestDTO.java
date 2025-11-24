package com.utec.gradecalculator.dto;

import lombok.AllArgsConstructor; // ¡NUEVO!
import lombok.Data;
import lombok.NoArgsConstructor; // Necesario para la deserialización JSON
import java.util.List;

@Data
@AllArgsConstructor // 🛑 AGREGAR ESTA LÍNEA PARA PERMITIR (String, List<...>, boolean, boolean)
@NoArgsConstructor  // Aunque @Data puede implicarlo, lo ponemos para seguridad en JSON
public class CalculationRequestDTO {
    private String studentCode;
    private List<ExamDTO> examsStudents;
    private boolean hasReachedMinimumClasses;
    private boolean allYearsTeachers;
}