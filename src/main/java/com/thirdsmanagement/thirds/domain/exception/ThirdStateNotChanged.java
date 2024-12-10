package com.thirdsmanagement.thirds.domain.exception;

/**
 * Excepcion que se lanza cuando no se ha podido cambiar el estado de un tercero
 */
public class ThirdStateNotChanged extends RuntimeException{
    /**
     * Constructor de la excepcion
     * @param message mensaje de error
     */
    public ThirdStateNotChanged(String message){
        super(message);
    }
}
