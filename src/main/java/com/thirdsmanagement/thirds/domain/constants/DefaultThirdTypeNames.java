package com.thirdsmanagement.thirds.domain.constants;

import java.util.List;

/**
 * Tipos de tercero base esperados por el sistema (alineado con maestros comerciales).
 */
public final class DefaultThirdTypeNames {

    public static final String CLIENTE = "Cliente";
    public static final String PROVEEDOR = "Proveedor";
    public static final String EMPLEADO = "Empleado";
    public static final String OTRO = "Otro";

    public static final List<String> ALL = List.of(CLIENTE, PROVEEDOR, EMPLEADO, OTRO);

    private DefaultThirdTypeNames() {
    }
}
