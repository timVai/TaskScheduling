package com.jd.uwp.common.exception;

public class JsonConvertException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5901146825082227921L;

    public JsonConvertException() {
	    super();
    }

    public JsonConvertException(String message) {
	    super(message);
    }

    public JsonConvertException(String message, Throwable cause) {
        super(message, cause);
    } 
    
    public JsonConvertException(Throwable cause) {
        super(cause);
    }
}
