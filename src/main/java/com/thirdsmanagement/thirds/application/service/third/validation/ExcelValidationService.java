package com.thirdsmanagement.thirds.application.service.third.validation;

import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.third.ExcelValidationException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdsErrorCode;
import com.thirdsmanagement.thirds.domain.model.*;
import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio centralizado para validaciones de Excel.
 * Maneja tanto la obtención de datos como la aplicación de validaciones.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelValidationService {

    private final IdOutputPort idOutputPort;
    private final GeographyOutputPort geographyOutputPort;

    // ========== MÉTODOS DE OBTENCIÓN DE DATOS ==========

    /**
     * Obtiene todos los tipos de identificación activos para una entidad.
     */
    public List<String> getTypeIdOptions(String entId) {
        return idOutputPort.getAllTypeIds(entId).stream()
                .filter(typeId -> typeId.getStatus() != null && typeId.getStatus())
                .map(TypeId::getTypeId)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los tipos de tercero activos para una entidad.
     */
    public List<String> getThirdTypeOptions(String entId) {
        return idOutputPort.getALLThirdTypes(entId).stream()
                .filter(thirdType -> thirdType.getStatus() != null && thirdType.getStatus())
                .map(ThirdType::getThirdTypeName)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las opciones de tipo de persona.
     */
    public List<String> getPersonTypeOptions() {
        return List.of("NATURAL", "JURIDICA");
    }

    /**
     * Obtiene todas las opciones de género.
     */
    public List<String> getGenderOptions() {
        return List.of("MASCULINO", "FEMENINO", "OTRO");
    }

    /**
     * Obtiene todas las opciones de estado (activo/inactivo).
     */
    public List<String> getStatusOptions() {
        return List.of("ACTIVO", "INACTIVO");
    }

    /**
     * Obtiene todos los países activos.
     */
    public List<String> getCountryOptions() {
        return geographyOutputPort.getAllActiveCountries().stream()
                .map(Country::getCountryName)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los departamentos de Colombia.
     */
    public List<String> getColombianStates() {
        return geographyOutputPort.getStatesByCountry("COL").stream()
                .map(State::getStateName)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el código de un país desde su nombre.
     */
    public String getCountryCodeByName(String countryName) {
        return geographyOutputPort.getAllActiveCountries().stream()
                .filter(country -> country.getCountryName().equals(countryName))
                .findFirst()
                .map(Country::getCountryCode)
                .orElse(null);
    }

    /**
     * Obtiene todos los departamentos de un país específico por nombre del país.
     */
    public List<String> getStatesByCountryName(String countryName) {
        String countryCode = getCountryCodeByName(countryName);
        if (countryCode == null) {
            return List.of();
        }
        return geographyOutputPort.getStatesByCountry(countryCode).stream()
                .map(State::getStateName)
                .filter(stateName -> stateName != null && !stateName.trim().isEmpty())
                .sorted()
                .collect(Collectors.toList());
    }

    // ========== MÉTODOS DE APLICACIÓN DE VALIDACIONES ==========

    /**
     * Aplica todas las validaciones de datos a una hoja de Excel para terceros.
     * Los índices de columna son dinámicos según la configuración de exportación.
     * 
     * @param sheet hoja de Excel
     * @param entId ID de la empresa
     * @param startRow fila inicial
     * @param endRow fila final
     * @param genderColumnIndex índice de la columna Género (-1 si no está incluida)
     * @param stateColumnIndex índice de la columna Estado
     */
    public void applyThirdValidations(Sheet sheet, String entId, int startRow, int endRow,
            int genderColumnIndex, int stateColumnIndex) {
        // Columna 0: Tipo de Identificación (siempre fija)
        applyDropdownValidation(sheet, 0, startRow, endRow,
                getTypeIdOptions(entId),
                "Seleccione un tipo de identificación válido");

        // Columna 2: Dígito Verificación (siempre fija)
        applyNumericRangeValidation(sheet, 2, startRow, endRow, 0, 9,
                "Dígito Verificación",
                "El dígito de verificación debe ser un número entre 0 y 9");

        // Columna 3: Tipo de Persona (siempre fija)
        applyDropdownValidation(sheet, 3, startRow, endRow,
                getPersonTypeOptions(),
                "Seleccione NATURAL o JURIDICA");

        // Género - OPCIONAL (solo si está incluido en la exportación)
        if (genderColumnIndex >= 0) {
            applyDropdownValidation(sheet, genderColumnIndex, startRow, endRow,
                    getGenderOptions(),
                    "Seleccione MASCULINO, FEMENINO u OTRO");
        }

        // Estado - posición dinámica según si género está incluido
        applyDropdownValidation(sheet, stateColumnIndex, startRow, endRow,
                getStatusOptions(),
                "Seleccione ACTIVO o INACTIVO");
    }

    /**
     * Aplica validaciones para terceros con tipos incluidos.
     * Permite múltiples tipos de tercero separados por comas.
     * 
     * @param sheet hoja de Excel
     * @param entId ID de la empresa
     * @param startRow fila inicial
     * @param endRow fila final
     * @param genderColumnIndex índice de la columna Género (-1 si no está incluida)
     * @param stateColumnIndex índice de la columna Estado
     * @param typesColumnIndex índice de la columna Tipos de Tercero
     */
    public void applyThirdValidationsWithTypes(Sheet sheet, String entId, int startRow, int endRow,
            int genderColumnIndex, int stateColumnIndex, int typesColumnIndex) {
        // Aplicar validaciones básicas con índices dinámicos
        applyThirdValidations(sheet, entId, startRow, endRow, genderColumnIndex, stateColumnIndex);

        // Columna de Tipos de Tercero (posición variable)
        applyMultiSelectValidation(sheet, typesColumnIndex, startRow, endRow,
                getThirdTypeOptions(entId),
                "Tipos de Tercero",
                "Ingrese uno o más tipos separados por coma. Tipos disponibles: ");
    }

    /**
     * Aplica validaciones geográficas para terceros con ciudades incluidas.
     * Solo aplica validaciones si los índices de columna son válidos (>= 0).
     * 
     * @param sheet hoja de Excel
     * @param startRow fila inicial
     * @param endRow fila final
     * @param countryColumnIndex índice de columna País (-1 si no está incluido)
     * @param stateColumnIndex índice de columna Departamento (-1 si no está incluido)
     * @param cityColumnIndex índice de columna Ciudad (-1 si no está incluido)
     */
    public void applyGeographyValidations(Sheet sheet, int startRow, int endRow,
            int countryColumnIndex, int stateColumnIndex, int cityColumnIndex) {
        try {
            // País - solo aplicar si está incluido en la exportación
            if (countryColumnIndex >= 0) {
                applyReferenceBasedValidation(sheet, countryColumnIndex, startRow, endRow,
                        "Datos_Referencia", "$A$2:$A$100",
                        "Seleccione un país válido");
            }

            // Departamento/Estado - solo aplicar si está incluido Y si País también está incluido
            if (stateColumnIndex >= 0 && countryColumnIndex >= 0) {
                applyStateValidationWithNamedRanges(sheet, stateColumnIndex, countryColumnIndex, startRow, endRow);
            }

            // Ciudad - solo aplicar si está incluido Y si Departamento también está incluido
            if (cityColumnIndex >= 0 && stateColumnIndex >= 0) {
                applyCityValidationWithNamedRanges(sheet, cityColumnIndex, stateColumnIndex, startRow, endRow);
            }
        } catch (Exception e) {
            throw new ExcelValidationException(ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
                    "Error al aplicar validaciones geográficas", e);
        }
    }

    /**
     * Aplica validación de lista desplegable a una columna específica.
     */
    public void applyDropdownValidation(Sheet sheet, int columnIndex, int startRow, int endRow,
            List<String> options, String errorMessage) {
        if (options == null || options.isEmpty()) {
            return;
        }

        try {

            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(xssfSheet);

            // Crear el rango de celdas donde aplicar la validación
            CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, columnIndex, columnIndex);

            // Crear la lista de opciones
            String[] optionsArray = options.toArray(new String[0]);
            DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(optionsArray);

            // Crear la validación
            DataValidation validation = validationHelper.createValidation(constraint, addressList);

            // Configurar propiedades de la validación
            validation.setShowErrorBox(true);
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validation.createErrorBox("Error de Validación", errorMessage);

            // Mostrar lista desplegable
            validation.setShowPromptBox(true);
            validation.createPromptBox("Selección", "Seleccione una opción de la lista");

            // Aplicar la validación a la hoja
            sheet.addValidationData(validation);

        } catch (Exception e) {
            throw new ExcelValidationException(ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
                    "Error al aplicar validación en columna " + columnIndex, e);
        }
    }

    /**
     * Aplica validación de rango numérico a una columna específica.
     * 
     * @param sheet        hoja de Excel
     * @param columnIndex  índice de la columna
     * @param startRow     fila inicial
     * @param endRow       fila final
     * @param minValue     valor mínimo permitido
     * @param maxValue     valor máximo permitido
     * @param fieldName    nombre del campo para mensajes
     * @param errorMessage mensaje de error personalizado
     */
    public void applyNumericRangeValidation(Sheet sheet, int columnIndex, int startRow, int endRow,
            int minValue, int maxValue, String fieldName, String errorMessage) {
        try {
            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(xssfSheet);

            // Crear el rango de celdas donde aplicar la validación
            CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, columnIndex, columnIndex);

            // Crear restricción numérica entre minValue y maxValue
            DataValidationConstraint constraint = validationHelper.createIntegerConstraint(
                    DataValidationConstraint.OperatorType.BETWEEN,
                    String.valueOf(minValue),
                    String.valueOf(maxValue));

            // Crear la validación
            DataValidation validation = validationHelper.createValidation(constraint, addressList);

            // Configurar propiedades de la validación
            validation.setShowErrorBox(true);
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validation.createErrorBox("Error de Validación", errorMessage);

            // Mostrar mensaje de ayuda
            validation.setShowPromptBox(true);
            validation.createPromptBox(fieldName, 
                    "Ingrese un número entre " + minValue + " y " + maxValue);

            // Permitir celdas vacías (campo opcional)
            validation.setEmptyCellAllowed(true);

            // Aplicar la validación a la hoja
            sheet.addValidationData(validation);

        } catch (Exception e) {
            throw new ExcelValidationException(ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
                    "Error al aplicar validación numérica en columna " + columnIndex, e);
        }
    }

    /**
     * Crea una hoja separada con los datos de referencia para las listas
     * desplegables.
     */
    public void createReferenceDataSheet(Workbook workbook, String entId) {
        Sheet refSheet = workbook.createSheet("Datos_Referencia");

        // Ocultar la hoja de referencia
        workbook.setSheetHidden(workbook.getSheetIndex(refSheet), true);

        int rowIndex = 0;

        // Crear encabezados
        Row headerRow = refSheet.createRow(rowIndex++);
        headerRow.createCell(0).setCellValue("Países");
        headerRow.createCell(1).setCellValue("Departamentos");
        headerRow.createCell(2).setCellValue("Tipos_ID");
        headerRow.createCell(3).setCellValue("Tipos_Tercero");
        headerRow.createCell(4).setCellValue("Estados");

        // Llenar datos de países
        List<String> countries = getCountryOptions();
        fillColumnData(refSheet, 0, countries);

        // Llenar datos de departamentos
        List<String> states = getColombianStates();
        fillColumnData(refSheet, 1, states);

        // Llenar datos de tipos de ID
        List<String> idTypes = getTypeIdOptions(entId);
        fillColumnData(refSheet, 2, idTypes);

        // Llenar datos de tipos de tercero
        List<String> thirdTypes = getThirdTypeOptions(entId);
        fillColumnData(refSheet, 3, thirdTypes);

        // Llenar datos de campo estado
        List<String> statusOptions = getStatusOptions();
        fillColumnData(refSheet, 4, statusOptions);

        // Crear columnas separadas para departamentos de cada país
        createNamedRangesForStatesByCountry(workbook, refSheet);

        // Crear columnas separadas para ciudades de cada departamento
        createNamedRangesForCitiesByState(workbook, refSheet);

        // Crear tabla de mapeo para la validación dependiente
        createDepartmentMappingTable(refSheet);
    }

    /**
     * Llena una columna de datos de referencia en una hoja.
     */
    private void fillColumnData(Sheet sheet, int columnIndex, List<String> data) {
        // Llenar datos
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.getRow(i + 1);
            if (row == null) {
                row = sheet.createRow(i + 1);
            }

            Cell cell = row.createCell(columnIndex);
            cell.setCellValue(data.get(i));
        }

        // Ajustar ancho de columna
        sheet.autoSizeColumn(columnIndex);
    }

    /**
     * Aplica validación usando referencias a celdas de otra hoja.
     */
    public void applyReferenceBasedValidation(Sheet sheet, int columnIndex, int startRow, int endRow,
            String referenceSheetName, String referenceRange, String errorMessage) {
        try {
            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(xssfSheet);

            CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, columnIndex, columnIndex);

            // Crear referencia a otra hoja: 'NombreHoja'!$A$1:$A$100
            String formula = "'" + referenceSheetName + "'!" + referenceRange;
            DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(formula);

            DataValidation validation = validationHelper.createValidation(constraint, addressList);

            validation.setShowErrorBox(true);
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validation.createErrorBox("Error de Validación", errorMessage);

            validation.setShowPromptBox(true);
            validation.createPromptBox("Selección", "Seleccione una opción de la lista");

            sheet.addValidationData(validation);

        } catch (Exception e) {
            throw new ExcelValidationException(ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
                    "Error aplicando validación de referencia: " + e.getMessage(), e);
        }
    }

    /**
     * Crea rangos con nombre para los departamentos de cada país.
     */
    private void createNamedRangesForStatesByCountry(Workbook workbook, Sheet referenceSheet) {
        List<String> countries = getCountryOptions();
        processGeographicalEntities(workbook, referenceSheet, countries, 4,
                "país", "Estados_", this::getStatesByCountryName);
    }

    /**
     * Crea rangos con nombre para las ciudades de cada departamento en la hoja de
     * referencia.
     * Organiza las ciudades en columnas consecutivas para facilitar OFFSET.
     */
    private void createNamedRangesForCitiesByState(Workbook workbook, Sheet referenceSheet) {
        List<String> states = getColombianStates();
        // Calcular la columna inicial después de las columnas básicas (A-D) y las
        // columnas de departamentos por país
        List<String> countries = getCountryOptions();
        int stateColumnsUsed = (int) countries.stream()
                .mapToLong(country -> getStatesByCountryName(country).isEmpty() ? 0 : 1)
                .sum();
        int startColumn = 4 + stateColumnsUsed; // Empezar después de las columnas básicas y las de departamentos

        processGeographicalEntitiesWithCustomNormalization(workbook, referenceSheet, states,
                startColumn, "departamento", null,
                this::getCitiesByStateName);
    }

    /**
     * Versión especializada para ciudades que usa normalización específica para
     * rangos.
     */
    private int processGeographicalEntitiesWithCustomNormalization(Workbook workbook, Sheet referenceSheet,
            List<String> entities, int startColumn, String entityType,
            String prefix, java.util.function.Function<String, List<String>> dataProvider) {
        Row headerRow = referenceSheet.getRow(0);
        if (headerRow == null) {
            headerRow = referenceSheet.createRow(0);
        }

        int currentColumn = startColumn;
        for (String entityName : entities) {
            List<String> data = dataProvider.apply(entityName);

            if (!data.isEmpty()) {
                try {
                    // Usar normalización específica para rangos (debe coincidir con fórmulas
                    // INDIRECT)
                    String normalizedName = StringNormalizer.normalizeForExcelNamedRange(entityName);

                    if (normalizedName.isEmpty() || normalizedName.length() > 255) {
                        continue;
                    }

                    String rangeName = prefix != null ? prefix + normalizedName : normalizedName;
                    headerRow.createCell(currentColumn).setCellValue(rangeName);
                    fillColumnData(referenceSheet, currentColumn, data);
                    createNamedRange(workbook, rangeName, currentColumn, data.size(), entityType);

                    currentColumn++;

                } catch (Exception e) {
                    throw new ExcelValidationException(ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
                            "Error creando datos para " + entityType + " '" + entityName + "'", e);
                }
            }
        }

        return currentColumn;
    }

    /**
     * Obtiene las ciudades de un departamento específico por nombre.
     */
    private List<String> getCitiesByStateName(String stateName) {
        try {
            return geographyOutputPort.getStatesByCountry("COL").stream()
                    .filter(state -> state.getStateName().equals(stateName))
                    .findFirst()
                    .map(state -> geographyOutputPort.getCitiesByState(state.getStateCode(), "COL"))
                    .orElse(List.of())
                    .stream()
                    .map(City::getCityName)
                    .filter(cityName -> cityName != null && !cityName.trim().isEmpty())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ExcelValidationException(ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
                    "Error obteniendo ciudades para departamento '" + stateName + "'", e);
        }
    }

    /**
     * Aplica validación de departamentos usando rangos con nombre directos con
     * INDIRECT.
     */
    public void applyStateValidationWithNamedRanges(Sheet sheet, int stateColumnIndex, int countryColumnIndex,
            int startRow, int endRow) {
        applyDependentValidation(sheet, stateColumnIndex, countryColumnIndex, startRow, endRow, "Estados_",
                "departamento");
    }

    /**
     * Aplica validación de ciudades usando rangos con nombre directos con INDIRECT.
     */
    public void applyCityValidationWithNamedRanges(Sheet sheet, int cityColumnIndex, int stateColumnIndex,
            int startRow, int endRow) {
        applyDependentValidation(sheet, cityColumnIndex, stateColumnIndex, startRow, endRow, null, "ciudad");
    }

    /**
     * Convierte un índice de columna numérico a letra (A, B, C, etc.).
     */
    private String getColumnLetter(int columnIndex) {
        StringBuilder columnLetter = new StringBuilder();
        while (columnIndex >= 0) {
            columnLetter.insert(0, (char) ('A' + columnIndex % 26));
            columnIndex = columnIndex / 26 - 1;
        }
        return columnLetter.toString();
    }

    /**
     * Crea una fórmula INDIRECT con normalización de caracteres especiales.
     * Delega la construcción de la fórmula al StringNormalizer para mantener
     * consistencia.
     */
    private String buildNormalizedIndirectFormula(String cellReference, String prefix) {
        return StringNormalizer.buildNormalizedIndirectFormula(cellReference, prefix);
    }

    /**
     * Configura las propiedades comunes de una validación de datos.
     */
    private void configureDataValidation(DataValidation validation, String errorTitle, String errorMessage,
            String promptTitle, String promptMessage) {
        validation.setShowErrorBox(true);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox(errorTitle, errorMessage);

        validation.setShowPromptBox(true);
        validation.createPromptBox(promptTitle, promptMessage);
    }

    /**
     * Aplica validación dependiente usando INDIRECT con configuración común.
     */
    private void applyDependentValidation(Sheet sheet, int targetColumnIndex, int sourceColumnIndex,
            int startRow, int endRow, String prefix, String validationType) {
        try {
            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(xssfSheet);

            for (int row = startRow; row <= endRow; row++) {
                CellRangeAddressList addressList = new CellRangeAddressList(row, row, targetColumnIndex,
                        targetColumnIndex);

                String sourceColumnLetter = getColumnLetter(sourceColumnIndex);
                String cellRef = sourceColumnLetter + (row + 1);
                String formula = buildNormalizedIndirectFormula(cellRef, prefix);

                DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(formula);
                DataValidation validation = validationHelper.createValidation(constraint, addressList);

                String errorMessage = "Seleccione un valor válido para la selección anterior";
                String promptMessage = "Los valores disponibles dependen de la selección anterior";

                configureDataValidation(validation, "Error de Validación", errorMessage,
                        "Selección", promptMessage);

                sheet.addValidationData(validation);
            }

        } catch (Exception e) {
            throw new ExcelValidationException(ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
                    "Error al aplicar validación dependiente de " + validationType + " en columna " + targetColumnIndex,
                    e);
        }
    }

    /**
     * Crea un rango con nombre para un conjunto de datos.
     */
    private void createNamedRange(Workbook workbook, String rangeName, int column, int dataSize, String entityType) {
        try {
            Name namedRange = workbook.createName();
            namedRange.setNameName(rangeName);

            String columnLetter = getColumnLetter(column);
            String rangeFormula = "'Datos_Referencia'!$" + columnLetter + "$2:$" + columnLetter + "$" + (dataSize + 1);
            namedRange.setRefersToFormula(rangeFormula);

        } catch (Exception e) {
            throw new ExcelValidationException(ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
                    "Error específico creando rango '" + rangeName + "'", e);
        }
    }

    /**
     * Procesa y crea rangos con nombre para una lista de entidades geográficas.
     */
    private int processGeographicalEntities(Workbook workbook, Sheet referenceSheet, List<String> entities,
            int startColumn, String entityType, String prefix,
            java.util.function.Function<String, List<String>> dataProvider) {
        Row headerRow = referenceSheet.getRow(0);
        if (headerRow == null) {
            headerRow = referenceSheet.createRow(0);
        }

        int currentColumn = startColumn;
        for (String entityName : entities) {
            List<String> data = dataProvider.apply(entityName);

            if (!data.isEmpty()) {
                try {
                    String normalizedName = StringNormalizer.normalizeForExcel(entityName);

                    if (normalizedName.isEmpty() || normalizedName.length() > 255) {
                        continue;
                    }

                    String rangeName = prefix != null ? prefix + normalizedName : normalizedName;
                    headerRow.createCell(currentColumn).setCellValue(rangeName);
                    fillColumnData(referenceSheet, currentColumn, data);
                    createNamedRange(workbook, rangeName, currentColumn, data.size(), entityType);

                    currentColumn++;

                } catch (Exception e) {
                    throw new ExcelValidationException(ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
                            "Error creando datos para " + entityType + " '" + entityName + "'", e);
                }
            }
        }

        return currentColumn;
    }

    /**
     * Aplica validación que permite múltiples valores separados por comas.
     * Muestra un mensaje informativo con los valores válidos disponibles.
     */
    public void applyMultiSelectValidation(Sheet sheet, int columnIndex, int startRow, int endRow,
            List<String> options, String fieldName, String promptPrefix) {
        if (options == null || options.isEmpty()) {
            return;
        }

        try {
            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(xssfSheet);

            // Crear el rango de celdas donde aplicar la validación
            CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, columnIndex, columnIndex);

            // Usar validación de texto personalizada que permite cualquier entrada
            // pero muestra mensaje informativo con las opciones disponibles
            DataValidationConstraint constraint = validationHelper.createCustomConstraint("TRUE");

            // Crear la validación
            DataValidation validation = validationHelper.createValidation(constraint, addressList);

            // Configurar mensaje de ayuda con las opciones disponibles
            validation.setShowPromptBox(true);
            String availableOptions = String.join(", ", options);
            String promptMessage = promptPrefix + availableOptions + ". Separe múltiples valores con comas (,)";
            
            // Limitar longitud del mensaje si es muy largo
            if (promptMessage.length() > 255) {
                promptMessage = promptPrefix + "Ver hoja 'Datos_Referencia' para opciones completas. Separe múltiples valores con comas (,)";
            }
            
            validation.createPromptBox(fieldName, promptMessage);

            // No configurar error box para permitir entrada libre
            validation.setShowErrorBox(false);

            // Aplicar la validación a la hoja
            sheet.addValidationData(validation);

        } catch (Exception e) {
            throw new ExcelValidationException(ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
                    "Error al aplicar validación multi-selección en columna " + columnIndex, e);
        }
    }

    /**
     * Crea una tabla de mapeo entre nombres originales de departamentos y nombres
     * de rangos normalizados.
     */
    private void createDepartmentMappingTable(Sheet referenceSheet) {
        try {
            List<String> states = getColombianStates();
            int mappingColumn = 38; // Columna AM (después de todas las ciudades)

            // Crear encabezado para la tabla de mapeo
            Row headerRow = referenceSheet.getRow(0);
            if (headerRow == null) {
                headerRow = referenceSheet.createRow(0);
            }
            headerRow.createCell(mappingColumn).setCellValue("Mapeo_Rangos");

            // Llenar la tabla de mapeo
            for (int i = 0; i < states.size(); i++) {
                String originalName = states.get(i);
                String normalizedName = StringNormalizer.normalizeForExcel(originalName);

                Row row = referenceSheet.getRow(i + 1);
                if (row == null) {
                    row = referenceSheet.createRow(i + 1);
                }

                // Columna AM: nombre del rango normalizado
                row.createCell(mappingColumn).setCellValue(normalizedName);
            }

        } catch (Exception e) {
            throw new ExcelValidationException(ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
                    "Error creando tabla de mapeo", e);
        }
    }

}
