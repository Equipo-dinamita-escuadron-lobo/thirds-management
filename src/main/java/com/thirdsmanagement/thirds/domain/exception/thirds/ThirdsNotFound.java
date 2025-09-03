package com.thirdsmanagement.thirds.domain.exception.thirds;

/**
 * Excepcion que se lanza cuando no se encuentran terceros
 */
public class ThirdsNotFound extends RuntimeException{
    /**
     * Constructor de la excepcion
     * @param message mensaje de error
     */
    public ThirdsNotFound(String message){
        super(message);
    }
}
