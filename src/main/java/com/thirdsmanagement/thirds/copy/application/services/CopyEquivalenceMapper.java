package com.thirdsmanagement.thirds.copy.application.services;

import com.thirdsmanagement.thirds.copy.domain.models.CopyEquivalencia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapa en memoria de equivalencias ID_viejo → ID_nuevo durante la copia.
 * Clave compuesta: "tabla:idViejo" → idNuevo.
 * No es un bean Spring — se crea por invocación de servicio.
 */
public class CopyEquivalenceMapper {

    private final Map<String, Long> mapa = new HashMap<>();

    /**
     * Registra una equivalencia tabla:idViejo → idNuevo.
     *
     * @param tabla  nombre de la tabla (ej: "thirds", "third_type", "type_id")
     * @param idViejo ID en la empresa origen
     * @param idNuevo ID en la empresa destino
     */
    public void registrar(String tabla, Long idViejo, Long idNuevo) {
        mapa.put(tabla + ":" + idViejo, idNuevo);
    }

    /**
     * Resuelve el nuevo ID dado tabla e ID viejo.
     *
     * @param tabla   nombre de la tabla
     * @param idViejo ID en la empresa origen
     * @return ID en la empresa destino, o null si no hay equivalencia
     */
    public Long resolverNuevoId(String tabla, Long idViejo) {
        return mapa.get(tabla + ":" + idViejo);
    }

    /**
     * Limpia todas las equivalencias almacenadas.
     */
    public void limpiar() {
        mapa.clear();
    }

    /**
     * Retorna todas las equivalencias como lista de objetos de dominio.
     *
     * @return lista de equivalencias
     */
    public List<CopyEquivalencia> toList() {
        List<CopyEquivalencia> lista = new ArrayList<>();
        for (Map.Entry<String, Long> entry : mapa.entrySet()) {
            String[] partes = entry.getKey().split(":", 2);
            lista.add(CopyEquivalencia.builder()
                    .tabla(partes[0])
                    .idViejo(partes[1])
                    .idNuevo(String.valueOf(entry.getValue()))
                    .build());
        }
        return lista;
    }
}
