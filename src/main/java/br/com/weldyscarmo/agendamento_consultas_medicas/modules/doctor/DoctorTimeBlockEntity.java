package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity(name = "doctor_time_block")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorTimeBlockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private DoctorEntity doctorEntity;

    @Column(name = "doctor_id")
    private UUID doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
