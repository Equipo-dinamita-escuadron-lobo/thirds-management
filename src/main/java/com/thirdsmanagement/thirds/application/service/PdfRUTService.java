package com.thirdsmanagement.thirds.application.service;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import com.thirdsmanagement.thirds.application.ports.input.PdfRUTContent;
import com.thirdsmanagement.thirds.application.ports.output.PdfRUTContentOutput;

/**
 * Clase de servicio para extraer contenido de un archivo PDF de RUT.
 * Implementa la interfaz {@link PdfRUTContent}.
 * Este servicio proporciona un método para extraer el contenido de un archivo PDF de RUT.
 * Utiliza la librería Apache PDFBox para extraer el texto del archivo PDF.
 * El contenido extraído se procesa para obtener la información de un tercero.
 * La información extraída se devuelve en un objeto {@link PdfRUTContentOutput}.
 * Si no se puede extraer la información, se devuelve un objeto vacío.
 * Si ocurre un error al procesar el contenido, se imprime un mensaje de error.
 * Siempre se elimina el archivo temporal después de extraer el contenido.
 */
@Service
public class PdfRUTService {
    /**
     * Extrae el contenido de un archivo PDF de RUT.
     * @param request Objeto con la información del archivo PDF.
     * @return Objeto con la información extraída del archivo PDF.
     * @throws IOException Si ocurre un error al cargar el archivo PDF.
     */
    public PdfRUTContentOutput extractContent(PdfRUTContent request) throws IOException {
        File tempFile = File.createTempFile("upload", ".pdf");

        // Transferir el archivo recibido a un archivo temporal
        request.getFile().transferTo(tempFile);

        try (PDDocument document = PDDocument.load(tempFile,"1")) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(1);  
            String content = pdfStripper.getText(document);
            String typeId = "";
            int idPerson = 0;
            String razonSocial="";
            String names="";
            String lastNames="";
            String [] ubication = null;
            String pais = "";
            String departamento = "";
            String ciudad = "";
            String direccion = "";
            String correo = "";
            long cell = 0;
            try{
                String[] extractedLines = extractAfterClasificacion(content);
                String[] extractedUbication = extractUbicationThird(content);
                String[] personaJuridica;
                String[] personaNatural;
                String typePerson;
                String[] aux = separateNumbersAndText(extractedLines[3]);
                typePerson = aux[0];
                if(extractedLines[3].contains("Persona jurídica")){
                    //Extraer la primera parte de identificacion para persona juridica 
                    typeId = "NIT";
                    personaJuridica = separateNumbersAndText(cleanString(extractedLines[2]));
                    idPerson = Integer.parseInt(String.valueOf(personaJuridica[0]).length() > 0 ? String.valueOf(personaJuridica[0]).substring(0, String.valueOf(personaJuridica[0]).length() - 1) : String.valueOf(personaJuridica[0]));
                    razonSocial = extractedLines[5];
                }else{
                    //Extraer la primera parte de identificacion para persona natural
                    String [] aux1 = separateNumbersAndText(cleanString(extractedLines[3]));
                    typeId = aux1[2];
                    typeId = typeId.trim();
                    personaNatural = separateNumbersAndText(cleanString(extractedLines[2]));
                    idPerson = Integer.parseInt(String.valueOf(personaNatural[0]).length() > 0 ? String.valueOf(personaNatural[0]).substring(0, String.valueOf(personaNatural[0]).length() - 1) : String.valueOf(personaNatural[0]));
                    String[] datos = new String[4];
                    datos = splitBySpaceAndUpperCase(extractedLines[5]);
                    lastNames = datos[0]+" "+ datos[1];
                    names = datos[2]+" "+ datos[3];
                }
                ubication =  separateNumbersAndText(cleanString(extractedUbication[0]));
                pais = cleanString(ubication[0]);
                departamento = cleanString(ubication[2]);
                ciudad = cleanString(ubication[4]);
                direccion = extractedUbication[1];
                correo = extractedUbication[2];
                String [] contact = separateAndJoinNumbers(extractedUbication[3]);
                cell = Long.parseLong(contact[1]);
                System.out.println("Tipo de persona: "+typePerson);
                System.out.println("Tipo de identifiacion: "+ typeId);
                System.out.println("Numero Identificacion: "+ idPerson);
                System.out.println("Razon social: "+razonSocial);
                System.out.println("Apellidos: "+lastNames);
                System.out.println("Nombres: "+names);
                System.out.println("Pais: "+pais);
                System.out.println("Departamento: "+departamento);
                System.out.println("Ciudad: "+ciudad);
                System.out.println("Direccion: "+direccion);
                System.out.println("Correo: "+correo);
                System.out.println("Telefono: "+cell);
                String infoThird = typePerson+";"+typeId+";"+idPerson+";"+razonSocial+";"+lastNames+";"+names+";"+pais+";"+departamento+";"+ciudad+";"+direccion+";"+correo+";"+cell;
                return new PdfRUTContentOutput(infoThird);
            }catch(Exception e){
                System.err.println("Error processing PDF content: " + e.getMessage());
                e.printStackTrace();
            }
            String infoThird = ""+";"+typeId+";"+idPerson+";"+razonSocial+";"+lastNames+";"+names+";"+pais+";"+departamento+";"+ciudad+";"+direccion+";"+correo+";"+cell;
            return new PdfRUTContentOutput(infoThird);
        } finally {
            tempFile.delete();
        }
    }

    /**
     * Extrae el contenido después de la palabra "CLASIFICACIÓN".
     * @param content Contenido del archivo PDF.
     * @return Arreglo con las líneas después de la palabra "CLASIFICACIÓN".
     */
    private String[] extractAfterClasificacion(String content) {
        int index = content.indexOf("CLASIFICACIÓN");
        if (index != -1) {
            String result = content.substring(index + "CLASIFICACIÓN".length()).trim();
            String[] lines = result.split("\\r?\\n"); 
            return lines;
        }
        return new String[0];
    }

    /**
     * Extrae la informacion de la ubicacion de un tercero desde el PDF de RUT.
     * @param content Contenido del archivo PDF.
     * @return Arreglo con las líneas de la ubicación de un tercero.
     */
    private String[] extractUbicationThird(String content) {
        int index = content.lastIndexOf("COLOMBIA");
        if (index != -1) {
            
            String result = content.substring(index).trim();
            String[] lines = result.split("\\r?\\n"); 
            return lines;
        }
        return new String[0];
    }
    
    /**
     * Limpia el contenido de un string.
     * @param input String a limpiar.
     * @return String limpio.
     */
    public static String cleanString(String input) {
        String cleaned = input.replaceAll("\\s*\\n\\s*", "\n") // Limpiar saltos de linea con espacios
                              .replaceAll("\\s{2,}", " ")    // Reemplaza multiples espacios por uno
                              .trim();                       // Elimina espacios al principio y al final
        // Elimina espacios entre varios numeros consecutivos
        cleaned = cleaned.replaceAll("(\\d)\\s+(?=\\d)", "$1");
        return cleaned;
    }

    /**
     * Separa numeros y texto en un string.
     * @param input String con numeros y texto.
     * @return Arreglo con los numeros y texto separados.
     */
    public static String[] separateNumbersAndText(String input) {
        // Regex para separar cuando hay un cambio de numeros a letras
        return input.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
    }

    /**
     * Separa y une numeros en un string.
     * @param input String con numeros.
     * @return Arreglo con los numeros separados.
     */
    public static String[] separateAndJoinNumbers(String input) {
        // Reemplaza multiples espacios con un separador especial
        String modifiedInput = input.replaceAll("\\s{2,}", ",");
        // Reemplaza espacios simples entre numeros
        modifiedInput = modifiedInput.replaceAll("\\s+", "");
        // Divide en un vector usando la coma como delimitador
        return modifiedInput.split(",");
    }

    /**
     * Divide un string por espacios y letras mayúsculas.
     * @param input String a dividir.
     * @return Arreglo con las partes del string.
     */
    public static String[] splitBySpaceAndUpperCase(String input) {
        return input.split("(?<=\\s)(?=[A-Z])");
    }
    
}