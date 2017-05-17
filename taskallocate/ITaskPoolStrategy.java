package com.jd.uwp.service.taskallocate;

import com.jd.uwp.service.taskallocate.exception.PoolLackException;

import java.util.List;

/**
 * 任务池加载策略抽象
 * Created by fanfengshi on 2017/3/2.
 */
public interface ITaskPoolStrategy {

    //初始化任务池
    public List init() throws PoolLackException;

    //补偿任务池
    public List compensate() throws PoolLackException;
}
