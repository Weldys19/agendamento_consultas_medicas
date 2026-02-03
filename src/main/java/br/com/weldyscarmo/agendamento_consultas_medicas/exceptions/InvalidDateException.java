package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

public class InvalidDateException extends RuntimeException {
    public InvalidDateException() {
        super("A data não pode ser anterior a atual");
    }
}
