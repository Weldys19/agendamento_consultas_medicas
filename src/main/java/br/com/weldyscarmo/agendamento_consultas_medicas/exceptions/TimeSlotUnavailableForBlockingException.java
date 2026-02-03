package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

public class TimeSlotUnavailableForBlockingException extends RuntimeException {
    public TimeSlotUnavailableForBlockingException() {
        super("Intervalo escolhido está fora dos horários de atendimento do médico");
    }
}
