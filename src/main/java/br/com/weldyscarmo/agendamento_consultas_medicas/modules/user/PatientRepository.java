package br.com.weldyscarmo.agendamento_consultas_medicas.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<PatientEntity, UUID> {
    Optional<PatientEntity> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);
}
