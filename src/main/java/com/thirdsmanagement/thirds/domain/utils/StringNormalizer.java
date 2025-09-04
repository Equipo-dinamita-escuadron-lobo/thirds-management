package com.thirdsmanagement.thirds.domain.utils;

import java.text.Normalizer;

/**
 * Utilidad para normalizar cadenas de texto para validaciones de unicidad.
 * Proporciona métodos para limpiar, normalizar y formatear nombres de manera consistente.
 */
public final class StringNormalizer {

    private StringNormalizer() {
        // Clase utilitaria - constructor privado
    }

    /**
     * Normaliza un nombre de manera consistente para validación y almacenamiento.
     * Elimina tildes/acentos, espacios extra y capitaliza la primera letra.
     * Este formato se usa tanto para comparar como para guardar en la base de datos.
     *
     * @param input el texto a normalizar
     * @return el texto normalizado, o null si el input es null
     */
    public static String normalize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        String cleaned = removeAccents(input.trim().toLowerCase());
        return capitalizeFirstLetter(cleaned);
    }

    /**
     * Normaliza un nombre eliminando acentos pero preservando el formato de mayúsculas/minúsculas.
     * Útil para nombres descriptivos donde se quiere mantener la presentación original.
     *
     * @param input el texto a normalizar
     * @return el texto sin acentos pero con formato original, o null si el input es null
     */
    public static String normalizePreservingCase(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        return removeAccents(input.trim());
    }

    /**
     * Normaliza un texto para comparaciones case-insensitive.
     * Elimina acentos, espacios extra y convierte a minúsculas para comparación.
     *
     * @param input el texto a normalizar para comparación
     * @return el texto normalizado para comparación, o null si el input es null
     */
    public static String normalizeForComparison(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        return removeAccents(input.trim().toLowerCase());
    }

    /**
     * Normaliza un código/referencia manteniendo el formato en mayúsculas.
     * Elimina tildes/acentos y espacios extra, pero convierte todo a mayúsculas.
     *
     * @param input el código/referencia a normalizar
     * @return el código normalizado en mayúsculas, o null si el input es null
     */
    public static String normalizeCode(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        return removeAccents(input.trim().toUpperCase());
    }

    /**
     * Elimina tildes y acentos de una cadena de texto.
     *
     * @param input el texto del cual eliminar acentos
     * @return el texto sin acentos
     */
    private static String removeAccents(String input) {
        if (input == null) {
            return null;
        }
        
        // Normaliza a forma NFD (descompone caracteres con acentos)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        
        // Elimina los caracteres diacríticos (tildes, acentos, etc.)
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * Capitaliza la primera letra de una cadena.
     *
     * @param input el texto a capitalizar
     * @return el texto con la primera letra en mayúscula
     */
    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        if (input.length() == 1) {
            return input.toUpperCase();
        }
        
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
