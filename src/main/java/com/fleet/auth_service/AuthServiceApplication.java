package com.fleet.auth_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
    loadEnvVariables();
		SpringApplication.run(AuthServiceApplication.class, args);
	}

  private static void loadEnvVariables() {
    try {
      Dotenv dotenv = Dotenv.configure()
              .directory("./")
              .ignoreIfMissing()
              .load();

      dotenv.entries().forEach(entry -> {
        if (System.getenv(entry.getKey()) == null) {
          System.setProperty(entry.getKey(), entry.getValue());
        }
      });

      System.out.println("✅ Variáveis do .env carregadas para System Properties.");
    } catch (Exception e) {
      System.out.println("ℹ️ .env não encontrado. Usando variáveis de ambiente do container.");
    }
  }
}
