//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.common.exception;

import com.common.exception.ApplicationException;

public class BizException extends ApplicationException {
    private static final long serialVersionUID = 1L;
    private String code;

    public BizException() {
    }

    public BizException(String message) {
        super(message);
        this.code = message;
    }

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public String getCode() {
        return this.code;
    }
}
