package com.utec.gradecalculator.service;

import com.utec.gradecalculator.constants.GradeConstants;
import com.utec.gradecalculator.dto.CalculationRequestDTO;
import com.utec.gradecalculator.dto.CalculationResultDTO;
import com.utec.gradecalculator.dto.ExamDTO;
import com.utec.gradecalculator.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

// Usa el contexto completo de Spring para inyectar el Service y sus dependencias (Policies)
@SpringBootTest
class GradeServiceTest {

    @Autowired
    private GradeService gradeService;

    // --- 1. Caso Cálculo Normal y con/sin Puntos Extra ---

    @Test
    void shouldReturnPassingGradeWithBonus_WhenAllRequirementsMet() {
        // Notas que suman 15.5 de promedio ponderado (0.5*15 + 0.5*16 = 15.5)
        List<ExamDTO> exams = List.of(
                new ExamDTO(15.0, 0.5),
                new ExamDTO(16.0, 0.5)
        );
        CalculationRequestDTO request = new CalculationRequestDTO(
                "1001", exams, true, true); // Bono: true

        CalculationResultDTO result = gradeService.calculateFinalGrade(request);

        // 15.5 (promedio) + 1.0 (bono) = 16.5
        assertEquals(16.50, result.getFinalGrade(), 0.001);
        assertEquals("APROBADO", result.getStatus());
        assertTrue(result.getCalculationDetail().contains("Aplicado +1.0"));
    }

    @Test
    void shouldReturnFinalGradeWithoutBonus_WhenBonusPolicyIsNotActive() {
        // Notas que suman 15.5 de promedio ponderado
        List<ExamDTO> exams = List.of(
                new ExamDTO(15.0, 0.5),
                new ExamDTO(16.0, 0.5)
        );
        CalculationRequestDTO request = new CalculationRequestDTO(
                "1002", exams, true, false); // Bono: false

        CalculationResultDTO result = gradeService.calculateFinalGrade(request);

        // 15.5 (promedio) + 0 (bono) = 15.5
        assertEquals(15.50, result.getFinalGrade(), 0.001);
        assertEquals("APROBADO", result.getStatus());
        // 🛑 CORRECCIÓN FINAL: Verifica la frase exacta de la política
        assertTrue(result.getCalculationDetail().contains("Política no activa"));
    }

    // --- 2. Caso Sin Asistencia Mínima (DPI) ---

    @Test
    void shouldReturnZeroGrade_WhenAttendanceIsFalse() {
        // Aunque tenga un promedio alto, la asistencia es falsa.
        // La prueba verifica que la lógica de "break" haya evitado el bono.
        List<ExamDTO> exams = List.of(new ExamDTO(20.0, 1.0));
        CalculationRequestDTO request = new CalculationRequestDTO(
                "1003", exams, false, true); // Asistencia: false, Bono: true

        CalculationResultDTO result = gradeService.calculateFinalGrade(request);

        // Regla asumida (DPI)
        assertEquals(GradeConstants.DPI_PENALTY_SCORE, result.getFinalGrade(), 0.001);
        assertEquals("DESAPROBADO", result.getStatus());
        assertTrue(result.getCalculationDetail().contains("PENALIZACIÓN ASISTENCIA"));
        // El test implícitamente verifica que el bono NO se aplicó (1.0 vs 0.0)
    }

    // --- 3. Casos Borde y Validaciones ---

    @Test
    void shouldThrowValidationException_WhenExamsExceedLimit() {
        // Caso borde: 11 exámenes (Límite RNF01 es 10)
        List<ExamDTO> exams = IntStream.range(0, GradeConstants.EXAMS_LIMIT + 1)
                .mapToObj(i -> new ExamDTO(10.0, 0.1))
                .toList();

        CalculationRequestDTO request = new CalculationRequestDTO("1004", exams, true, true);

        // Evaluación: Debe lanzar la excepción personalizada
        assertThrows(ValidationException.class, () -> gradeService.calculateFinalGrade(request));
    }

    @Test
    void shouldHandleZeroExams_WhenListIsEmpty() {
        // Caso borde: Lista vacía
        CalculationRequestDTO request = new CalculationRequestDTO("1005", Collections.emptyList(), true, false);

        CalculationResultDTO result = gradeService.calculateFinalGrade(request);

        // El promedio ponderado debe ser 0.0
        assertEquals(0.00, result.getFinalGrade(), 0.001);
        assertEquals("DESAPROBADO", result.getStatus());
    }

    @Test
    void shouldCapFinalGradeAt20_WhenCalculationExceedsMaxScore() {
        // Notas que dan 19.5 de promedio ponderado
        List<ExamDTO> exams = List.of(new ExamDTO(19.5, 1.0));
        CalculationRequestDTO request = new CalculationRequestDTO("1006", exams, true, true); // Bono: true

        CalculationResultDTO result = gradeService.calculateFinalGrade(request);

        // El cálculo daría 20.5 (19.5 + 1.0), pero debe ser capado a 20.0
        assertEquals(GradeConstants.MAX_SCORE, result.getFinalGrade(), 0.001);
        assertTrue(result.getCalculationDetail().contains("Aplicado +1.0"));
    }

    @Test
    void shouldReturnPassingGrade_WhenGradeIsExactlyPassingThreshold() {
        // Caso borde: Nota en el umbral exacto (11.0)
        List<ExamDTO> exams = List.of(new ExamDTO(GradeConstants.PASS_THRESHOLD, 1.0));
        CalculationRequestDTO request = new CalculationRequestDTO("1007", exams, true, false);

        CalculationResultDTO result = gradeService.calculateFinalGrade(request);

        // Nota debe ser 11.0 y APROBADO
        assertEquals(GradeConstants.PASS_THRESHOLD, result.getFinalGrade(), 0.001);
        assertEquals("APROBADO", result.getStatus());
    }
}