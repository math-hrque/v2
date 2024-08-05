package br.com.lol.lol.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class FlywayConfig {

    @Bean
    public CommandLineRunner migrateDatabase(Flyway flyway, PasswordEncoder passwordEncoder, DataSource dataSource) {
        return args -> {
            //flyway.clean();
            flyway.migrate();

            String selectQuery = "SELECT email, nome FROM usuario WHERE senha = '{bcrypt}'";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                 ResultSet resultSet = selectStatement.executeQuery()) {

                while (resultSet.next()) {
                    String email = resultSet.getString("email");
                    String password = "pass";
                    String encodedPassword = passwordEncoder.encode(password);

                    String updateQuery = "UPDATE usuario SET senha = ? WHERE email = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, encodedPassword);
                        updateStatement.setString(2, email);
                        updateStatement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
    }

}