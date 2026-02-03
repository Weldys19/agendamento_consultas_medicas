package br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments;

import br.com.weldyscarmo.agendamento_consultas_medicas.enums.AppointmentsStatus;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity(name = "appointments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private PatientEntity patientEntity;

    @ManyToOne
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private DoctorEntity doctorEntity;

    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "doctor_id")
    private UUID doctorId;

    private LocalDate date;

    private LocalTime startTime;
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private AppointmentsStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
