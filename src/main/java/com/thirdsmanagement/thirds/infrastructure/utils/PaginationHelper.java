package com.thirdsmanagement.thirds.infrastructure.utils;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * @brief Utilidades para manejo flexible de paginación en Spring Data
 *
 * Clase de utilidad que facilita la creación de objetos Pageable con parámetros opcionales.
 * Maneja conversiones seguras long a int y soporta paginación opcional donde si no se
 * especifican parámetros, retorna todos los registros en una sola página.
 */
public class PaginationHelper {

    /**
     * @brief Crea objeto Pageable con parámetros opcionales de paginación
     * @details Si numPage o size no están presentes, crea una página que contiene todos los registros
     * disponibles (página 0 con tamaño = totalRecords). Si ambos parámetros están presentes,
     * crea paginación normal con PageRequest.of().
     * @param numPage número de página opcional (0-based)
     * @param size tamaño de página opcional
     * @param totalRecords total de registros para calcular tamaño cuando no hay paginación
     * @return Pageable configurado según parámetros disponibles
     */
    public static Pageable createFlexiblePageable(Optional<Integer> numPage,
                                                   Optional<Integer> size,
                                                   long totalRecords) {
        if (numPage.isEmpty() || size.isEmpty()) {
            // Si no se especifican parámetros de paginación, traer todos los registros
            int safeSize = safeIntCast(totalRecords);
            return PageRequest.of(0, Math.max(1, safeSize));
        } else {
            // Validar que los valores sean válidos
            validatePaginationParams(numPage.get(), size.get());
            // Usar los parámetros especificados
            return PageRequest.of(numPage.get(), size.get());
        }
    }

    /**
     * @brief Valida que los parámetros de paginación sean válidos
     * @param numPage número de página (debe ser >= 0)
     * @param size tamaño de página (debe ser >= 1)
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    private static void validatePaginationParams(int numPage, int size) {
        if (numPage < 0) {
            throw new IllegalArgumentException("El número de página no puede ser negativo. Valor recibido: " + numPage);
        }
        if (size < 1) {
            throw new IllegalArgumentException("El tamaño de página debe ser mayor a 0. Valor recibido: " + size);
        }
    }

    /**
     * @brief Convierte long a int de forma segura evitando overflow
     * @details Verifica si el valor long excede Integer.MAX_VALUE. Si es así,
     * retorna Integer.MAX_VALUE para evitar ArithmeticException. De lo contrario,
     * hace cast normal a int.
     * @param value valor long a convertir
     * @return valor int equivalente o Integer.MAX_VALUE si hay overflow
     */
    private static int safeIntCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) value;
    }
}
