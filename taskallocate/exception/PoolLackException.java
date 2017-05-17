package com.jd.uwp.service.taskallocate.exception;

import com.jd.uwp.common.exception.UwpException;

/**
 * 资源池缺少资源异常
 * Created by fanfengshi on 2017/3/6.
 */
public class PoolLackException extends UwpException {


    public PoolLackException(String reason ){
        super(reason);
    }

    public PoolLackException(String reason ,Throwable cause){
        super(reason,cause);
    }
}
