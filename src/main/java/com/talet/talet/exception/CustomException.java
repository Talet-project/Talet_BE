package com.talet.talet.exception;

import com.talet.talet.util.ErrorEnum;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorEnum errorCode;

    public CustomException(ErrorEnum errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorEnum errorCode, String overrideMessage) {
        super(overrideMessage);
        this.errorCode = errorCode;
    }
}
