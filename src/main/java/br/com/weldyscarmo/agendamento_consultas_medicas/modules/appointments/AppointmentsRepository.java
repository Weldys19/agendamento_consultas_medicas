package br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentsRepository extends JpaRepository<AppointmentsEntity, UUID> {

    List<AppointmentsEntity> findAllByDoctorIdAndDate(UUID doctorId, LocalDate date);
    List<AppointmentsEntity> findAllByPatientId(UUID patientId);
    Optional<AppointmentsEntity> findByIdAndDoctorId(UUID id, UUID doctorId);
    Optional<AppointmentsEntity> findByIdAndPatientId(UUID id, UUID patientId);
}
