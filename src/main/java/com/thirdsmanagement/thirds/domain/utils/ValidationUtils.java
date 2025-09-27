package com.thirdsmanagement.thirds.domain.utils;

import java.util.regex.Pattern;

/**
 * Utilidades de validación comunes para el sistema de terceros.
 * Centraliza validaciones reutilizables para mantener consistencia
 * y facilitar mantenimiento.
 */
public final class ValidationUtils {

    private ValidationUtils() {
        // Clase utilitaria - constructor privado
    }

    // Patrones compilados para mejor rendimiento
    private static final Pattern EMAIL_PATTERN = Pattern.compile(ImportConstants.ValidationPatterns.EMAIL_PATTERN);
    private static final Pattern PHONE_PATTERN = Pattern.compile(ImportConstants.ValidationPatterns.PHONE_PATTERN);
    private static final Pattern NIT_START_PATTERN = Pattern.compile(ImportConstants.ValidationPatterns.NIT_START_PATTERN);

    // ===== VALIDACIONES DE FORMATO =====

    /**
     * Valida si un email tiene formato válido.
     * 
     * @param email el email a validar
     * @return true si el formato es válido, false en caso contrario
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valida si un número de teléfono tiene formato válido.
     * Acepta solo números y opcionalmente signo + al inicio.
     * 
     * @param phone el número de teléfono a validar (ya limpio, sin espacios)
     * @return true si el formato es válido, false en caso contrario
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String cleanPhone = phone.trim();
        return cleanPhone.length() >= 7 && cleanPhone.length() <= 15 && 
               PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Valida si un NIT tiene el formato correcto para personas jurídicas.
     * El NIT debe empezar por 8 o 9 según regulaciones colombianas.
     * 
     * @param nit el número NIT a validar
     * @return true si el formato es válido, false en caso contrario
     */
    public static boolean isValidNitFormat(String nit) {
        if (nit == null || nit.trim().isEmpty()) {
            return false;
        }
        return NIT_START_PATTERN.matcher(nit.trim()).matches();
    }

    // ===== VALIDACIONES DE CONTENIDO =====

    /**
     * Verifica si una cadena no está vacía ni es solo espacios en blanco.
     * 
     * @param value la cadena a verificar
     * @return true si tiene contenido válido, false en caso contrario
     */
    public static boolean hasContent(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Verifica si una cadena tiene una longitud específica.
     * 
     * @param value la cadena a verificar
     * @param expectedLength la longitud esperada
     * @return true si la longitud coincide, false en caso contrario
     */
    public static boolean hasLength(String value, int expectedLength) {
        return value != null && value.trim().length() == expectedLength;
    }

    /**
     * Verifica si una cadena está dentro de un rango de longitud.
     * 
     * @param value la cadena a verificar
     * @param minLength longitud mínima (inclusiva)
     * @param maxLength longitud máxima (inclusiva)
     * @return true si está en el rango, false en caso contrario
     */
    public static boolean hasLengthBetween(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }

    // ===== VALIDACIONES DE COMPATIBILIDAD =====

    /**
     * Verifica si un mensaje de excepción indica un error de duplicado.
     * 
     * @param errorMessage el mensaje de error a analizar
     * @return true si es un error de duplicado, false en caso contrario
     */
    public static boolean isDuplicateError(String errorMessage) {
        if (errorMessage == null) {
            return false;
        }
        
        String lowerMessage = errorMessage.toLowerCase();
        return lowerMessage.contains("ya existe") || 
               lowerMessage.contains("duplicate") || 
               lowerMessage.contains("duplicado") ||
               lowerMessage.contains("unique constraint") ||
               (lowerMessage.contains("número de identificación") && lowerMessage.contains("existe"));
    }

    /**
     * Verifica si un mensaje de excepción indica un error geográfico.
     * 
     * @param errorMessage el mensaje de error a analizar
     * @return true si es un error geográfico, false en caso contrario
     */
    public static boolean isGeographyError(String errorMessage) {
        if (errorMessage == null) {
            return false;
        }
        
        String lowerMessage = errorMessage.toLowerCase();
        return lowerMessage.contains("código del país") || 
               lowerMessage.contains("código del estado") ||
               lowerMessage.contains("código de la ciudad") ||
               lowerMessage.contains("código del departamento") ||
               (lowerMessage.contains("obligatorio") && (
                   lowerMessage.contains("país") || 
                   lowerMessage.contains("estado") || 
                   lowerMessage.contains("ciudad") ||
                   lowerMessage.contains("departamento")
               ));
    }

    // ===== VALIDACIONES DE NEGOCIO =====

    /**
     * Detecta el campo específico basado en un mensaje de error de regla de negocio.
     * 
     * @param errorMessage el mensaje de error a analizar
     * @return el nombre del campo detectado o un valor genérico
     */
    public static String detectFieldFromBusinessRuleError(String errorMessage) {
        if (errorMessage == null) {
            return "Reglas Negocio";
        }
        
        String lowerMessage = errorMessage.toLowerCase();
        if (lowerMessage.contains("nit")) {
            return "Número Identificación";
        } else if (lowerMessage.contains("tipo de persona") || 
                   lowerMessage.contains("persona natural") || 
                   lowerMessage.contains("persona jurídica")) {
            return "Tipo Persona";
        } else if (lowerMessage.contains("tipo de identificación") || 
                   lowerMessage.contains("identificación")) {
            return "Tipo Identificación";
        } else if (lowerMessage.contains("nombre")) {
            return "Nombres";
        } else if (lowerMessage.contains("razón social")) {
            return "Razón Social";
        } else {
            return "Reglas Negocio";
        }
    }

    // ===== NORMALIZACIONES =====

    /**
     * Normaliza un texto para comparaciones case-insensitive.
     * 
     * @param text el texto a normalizar
     * @return el texto normalizado o null si el input es null
     */
    public static String normalizeForComparison(String text) {
        return text != null ? text.trim().toUpperCase() : null;
    }

    /**
     * Limpia un número telefónico removiendo caracteres no numéricos excepto +.
     * 
     * @param phone el número a limpiar
     * @return el número limpio o null si el input es null
     */
    public static String cleanPhoneNumber(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceAll("[^+0-9]", "");
    }
}
