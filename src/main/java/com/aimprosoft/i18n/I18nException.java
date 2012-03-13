package com.aimprosoft.i18n;

public abstract class I18nException extends Exception{
    protected I18nException() {
        super();
    }

    protected I18nException(String message) {
        super(message);
    }

    protected I18nException(String message, Throwable cause) {
        super(message, cause);
    }

    protected I18nException(Throwable cause) {
        super(cause);
    }
}
