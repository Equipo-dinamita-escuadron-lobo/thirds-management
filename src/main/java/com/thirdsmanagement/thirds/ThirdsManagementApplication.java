package com.thirdsmanagement.thirds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.scheduling.annotation.EnableAsync;

import com.thirdsmanagement.thirds.infrastructure.config.FileUploadProperties;

/**
 * Clase principal de la aplicación.
 * Inicia la aplicación Spring Boot.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@EnableConfigurationProperties(FileUploadProperties.class)
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class ThirdsManagementApplication {
	/**
	 * Método principal de la aplicación.
	 * @param args argumentos de la línea de comandos
	 */
	public static void main(String[] args) {
		
		SpringApplication.run(ThirdsManagementApplication.class, args);
	}
}
