package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DoctorScheduleRepository extends JpaRepository<DoctorScheduleEntity, UUID> {
}
