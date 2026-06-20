package com.thirdsmanagement.thirds.copy.domain.enums;

/**
 * Estados del proceso de copia de terceros.
 */
public enum CopyEstado {
    /** La copia está en ejecución */
    EN_PROCESO,
    /** La copia finalizó correctamente */
    COMPLETADO,
    /** La copia finalizó con advertencias (FKs no resueltas, etc.) */
    COMPLETADO_CON_ADVERTENCIAS,
    /** La copia falló con error reintentable */
    FALLIDO,
    /** Error permanente, no se puede reintentar */
    ERROR_NO_REINTENTABLE,
    /** La copia fue cancelada manualmente */
    CANCELADO
}
