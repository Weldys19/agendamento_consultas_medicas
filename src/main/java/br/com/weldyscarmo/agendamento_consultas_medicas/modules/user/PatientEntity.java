package br.com.weldyscarmo.agendamento_consultas_medicas.modules.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "patient")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Pattern(regexp = "\\S+", message = "O campo username não pode conter espaços")
    private String username;

    @Email
    private String email;

    @Length(min = 8, message = "A senha deve conter no minímo 8 caracters")
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
