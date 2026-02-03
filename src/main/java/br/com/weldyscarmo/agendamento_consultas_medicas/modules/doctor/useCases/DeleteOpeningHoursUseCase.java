package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.TimeNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorScheduleEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteOpeningHoursUseCase {

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    public void execute(UUID doctorId, UUID doctorScheduleId) {

        DoctorScheduleEntity doctorSchedule = this.doctorScheduleRepository
                .findByIdAndDoctorId(doctorScheduleId, doctorId)
                .orElseThrow(() -> {
                    throw new TimeNotFoundException();
                });

        this.doctorScheduleRepository.delete(doctorSchedule);
    }
}
