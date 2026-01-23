package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

public class InvalidDayException extends RuntimeException {
    public InvalidDayException() {
        super("Dia inválido");
    }
}
