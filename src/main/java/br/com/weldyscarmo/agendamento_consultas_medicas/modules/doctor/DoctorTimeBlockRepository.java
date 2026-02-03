package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DoctorTimeBlockRepository extends JpaRepository<DoctorTimeBlockEntity, UUID> {
    List<DoctorTimeBlockEntity> findAllByDoctorIdAndDate(UUID doctorId, LocalDate date);
}
