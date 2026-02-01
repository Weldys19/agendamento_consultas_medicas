package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.InvalidTimeExclusionException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.TimeNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorEntity;
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

    @Autowired
    private DoctorRepository doctorRepository;

    public void execute(UUID doctorId, UUID doctorScheduleId) {

        DoctorEntity doctorEntity = this.doctorRepository.findById(doctorId).orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        DoctorScheduleEntity doctorSchedule = this.doctorScheduleRepository.findById(doctorScheduleId)
                .orElseThrow(() -> {
                    throw new TimeNotFoundException();
                });

        if (!doctorEntity.getId().equals(doctorSchedule.getDoctorId())) {
            throw new InvalidTimeExclusionException();
        }

        this.doctorScheduleRepository.delete(doctorSchedule);
    }
}
