package com.conchordance.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ExceptionResponse extends WebApplicationException {
    private static final long serialVersionUID = 1L;

    public ExceptionResponse(int status, String message) {
        super(Response.status(status).entity(message + "\n").build());
    }
}