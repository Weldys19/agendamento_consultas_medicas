package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorScheduleRepository extends JpaRepository<DoctorScheduleEntity, UUID> {
    List<DoctorScheduleEntity> findAllByDoctorId(UUID doctorId);
    Optional<DoctorScheduleEntity> findByIdAndDoctorId(UUID id, UUID doctorId);
    List<DoctorScheduleEntity> findAllByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek day);
}
