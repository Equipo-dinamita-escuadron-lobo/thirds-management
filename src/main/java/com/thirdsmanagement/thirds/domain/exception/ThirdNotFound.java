package com.thirdsmanagement.thirds.domain.exception;
/*Reset DataBase*/
/**
 * Exepcion que se lanza cuando no se encuentra un tercero
 */
public class ThirdNotFound extends RuntimeException{
    /**
     * Constructor de la excepcion
     * @param message mensaje de error
     */
    public ThirdNotFound(String message){
        super(message);
    }
}
