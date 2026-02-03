package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

public class UnavailableScheduleException extends RuntimeException {
    public UnavailableScheduleException() {
        super("O médico estará ocupado nesse horário");
    }
}
