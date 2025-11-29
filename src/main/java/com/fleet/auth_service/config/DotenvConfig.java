package com.fleet.auth_service.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

  @PostConstruct
  public void loadEnv() {
    try {
      Dotenv dotenv = Dotenv.configure()
              .directory("./")
              .ignoreIfMissing()
              .load();

      dotenv.entries().forEach(entry -> {
        String key = entry.getKey();
        String value = entry.getValue();

        if (System.getProperty(key) == null && System.getenv(key) == null) {
          System.setProperty(key, value);
        }
      });

      System.out.println("✅ Arquivo .env carregado com sucesso!");

    } catch (Exception e) {
      System.out.println("⚠️  Arquivo .env não encontrado. Usando valores padrão.");
    }
  }
}