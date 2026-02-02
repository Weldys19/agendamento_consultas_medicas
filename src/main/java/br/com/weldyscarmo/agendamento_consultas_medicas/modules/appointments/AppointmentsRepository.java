package br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentsRepository extends JpaRepository<AppointmentsEntity, UUID> {

    List<AppointmentsEntity> findAllByDoctorIdAndDate(UUID doctorId, LocalDate date);
    List<AppointmentsEntity> findAllByPatientId(UUID patientId);
}
