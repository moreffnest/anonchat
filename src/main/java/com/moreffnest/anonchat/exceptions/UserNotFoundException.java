package com.moreffnest.anonchat.exceptions;

public class UserNotFoundException extends AnonchatException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
