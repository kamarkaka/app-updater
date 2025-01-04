package com.kamarkaka.commons.exceptions;

/** checked exception for errors when uploading/download data via HTTP */
public class FetchErrorException extends Exception {
    public FetchErrorException(String message) {
        super(message);
    }

    public FetchErrorException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
