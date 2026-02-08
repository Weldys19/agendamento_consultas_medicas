package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException() {
        super("Role inválida");
    }
}
