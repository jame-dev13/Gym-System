package com.jame.dev.gymApp.infrastructure.config.app;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

   @Value("${spring.data.mongodb.uri}")
   private String mongoUri;

   @Value("${spring.data.mongodb.database}")
   private String databaseName;

   @Bean
   public MongoClient mongoClient() {
      final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
         .uuidRepresentation(UuidRepresentation.STANDARD)
         .applyConnectionString(new ConnectionString(mongoUri))
         .build();
      return MongoClients.create(mongoClientSettings);
   }

   @Bean
   public MongoTemplate mongoTemplate(MongoClient mongoClient) {
      return new MongoTemplate(mongoClient, databaseName);
   }
}
