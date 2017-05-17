package com.jd.uwp.service.taskallocate.exception;

import com.jd.uwp.common.exception.UwpException;

/**
 * 可以终止分派的异常
 * Created by fanfengshi on 2017/3/6.
 */
public class StopAllocateException extends UwpException {


    public StopAllocateException(String reason){
        super(reason);
    }

    public StopAllocateException(String reason, Throwable cause){
        super(reason, cause);
    }
}
