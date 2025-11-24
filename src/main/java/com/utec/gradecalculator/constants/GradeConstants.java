package com.utec.gradecalculator.constants;

// Usaremos 'final' y 'static' para que sean constantes de la clase
public final class GradeConstants {

    // RNF01: Límite de exámenes
    public static final int EXAMS_LIMIT = 10;

    // Escala y Umbrales
    public static final double MAX_SCORE = 20.0;
    public static final double PASS_THRESHOLD = 11.0;

    // Penalizaciones
    public static final double DPI_PENALTY_SCORE = 0.0; // Nota para Desaprobado por Inasistencia
    public static final double BONUS_POINTS = 1.0;      // Puntos extra asumidos

    // Constructor privado para evitar instanciación (Clase de utilidad)
    private GradeConstants() {}
}