package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

public class ConflictWithSchedulesException extends RuntimeException {
    public ConflictWithSchedulesException() {
        super("Não pode bloquear um horário que tenha consulta agendada");
    }
}
