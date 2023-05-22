package me.supcheg.advancedmanhunt.exception;

public class TemplateLoadException extends RuntimeException {
    public TemplateLoadException() {
    }

    public TemplateLoadException(String message) {
        super(message);
    }

    public TemplateLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateLoadException(Throwable cause) {
        super(cause);
    }

    public TemplateLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
