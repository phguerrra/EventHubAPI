package com.grupo1.backGrupo1.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String msg, Throwable t) { super(msg, t); }
}
