package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

public class InvalidScheduleException extends RuntimeException {
    public InvalidScheduleException() {
        super("O horário inicial não pode ser depois que o horário final");
    }
}
