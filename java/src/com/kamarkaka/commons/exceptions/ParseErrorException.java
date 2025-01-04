package com.kamarkaka.commons.exceptions;

/** checked exception for errors when parsing data */
public class ParseErrorException extends Exception {
    public ParseErrorException(String message) {
        super(message);
    }

    public ParseErrorException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
