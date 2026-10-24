package com.autoscolombia.parqueadero;

import com.autoscolombia.parqueadero.repository.CeldaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class ParqueaderoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParqueaderoApplication.class, args);
    }

    @Bean
    public CommandLineRunner verificarInicio(DataSource dataSource, CeldaRepository celdaRepository) {
        return args -> {
            String dbProduct = "Desconocida";
            String dbCatalog = "autos_colombia";

            try (Connection conn = dataSource.getConnection()) {
                dbProduct = conn.getMetaData().getDatabaseProductName() + " " + conn.getMetaData().getDatabaseProductVersion();
                dbCatalog = conn.getCatalog();
            } catch (Exception e) {
                dbProduct = "Error al leer metadatos: " + e.getMessage();
            }

            long celdasCount = 0;
            try {
                celdasCount = celdaRepository.count();
            } catch (Exception ignored) {
            }

            System.out.println();
            System.out.println("================================================================================");
            System.out.println("   🚗  PARQUEADERO 'AUTOS COLOMBIA' - INICIADO CON ÉXITO");
            System.out.println("================================================================================");
            System.out.println("   ✅ ESTADO DEL SISTEMA : EN LÍNEA Y OPERATIVO");
            System.out.println("   🗄️  BASE DE DATOS     : " + dbProduct);
            System.out.println("   📁 ESQUEMA ACTIVO     : " + dbCatalog);
            System.out.println("   🅿️  CELDAS CARGADAS   : " + celdasCount + " celdas listas para asignación");
            System.out.println("   🌐 SERVIDOR LOCAL     : http://localhost:8080");
            System.out.println("   🖥️  INTERFAZ DE USUARIO: http://localhost:8080/index.html");
            System.out.println("================================================================================");
            System.out.println();
        };
    }
}
