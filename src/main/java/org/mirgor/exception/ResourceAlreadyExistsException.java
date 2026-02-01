package org.mirgor.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException() {
        super();
    }

    public ResourceAlreadyExistsException(Object resource, String field, String value) {
        super(String.format("%s with such %s [%s] already exists", resource.getClass().getName(), field, value));
    }
}
