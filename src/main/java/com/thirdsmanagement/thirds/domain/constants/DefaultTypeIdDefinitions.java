package com.thirdsmanagement.thirds.domain.constants;

import java.util.List;

import com.thirdsmanagement.thirds.domain.model.PersonClassification;

/**
 * Tipos de identificación base esperados por el sistema (normatividad colombiana).
 */
public final class DefaultTypeIdDefinitions {

    public record Definition(String code, String name, PersonClassification classification) {
    }

    public static final Definition CC = new Definition(
            "CC", "Cédula de Ciudadanía", PersonClassification.NATURAL_PERSON);
    public static final Definition CE = new Definition(
            "CE", "Cédula de Extranjería", PersonClassification.NATURAL_PERSON);
    public static final Definition TI = new Definition(
            "TI", "Tarjeta de Identidad", PersonClassification.NATURAL_PERSON);
    public static final Definition PP = new Definition(
            "PP", "Pasaporte", PersonClassification.NATURAL_PERSON);
    public static final Definition NIT = new Definition(
            "NIT", "NIT", PersonClassification.LEGAL_ENTITY);

    public static final List<Definition> ALL = List.of(CC, CE, TI, PP, NIT);

    private DefaultTypeIdDefinitions() {
    }
}
