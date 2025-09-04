package com.thirdsmanagement.thirds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Clase principal de la aplicación.
 * Inicia la aplicación Spring Boot.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ThirdsManagementApplication {
	/**
	 * Método principal de la aplicación.
	 * @param args argumentos de la línea de comandos
	 */
	public static void main(String[] args) {
		
		SpringApplication.run(ThirdsManagementApplication.class, args);
	}
}
