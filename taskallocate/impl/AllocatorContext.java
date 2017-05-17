package com.jd.uwp.service.taskallocate.impl;

import com.jd.uwp.domain.user.UwpUserDomain;
import com.jd.uwp.domain.uwpTask.AutoAllocateTaskDO;
import com.jd.uwp.domain.uwpTask.AutoFetchTaskDO;
import com.jd.uwp.domain.uwpTask.UwpTaskDomain;
import com.jd.uwp.service.taskallocate.IAllocateStrategy;
import com.jd.uwp.service.taskallocate.ITaskPoolStrategy;
import com.jd.uwp.service.taskallocate.IUserPoolStrategy;

import java.util.List;

/**
 * Created by fanfengshi on 2017/3/2.
 * 任务分发 上下文
 */
public class AllocatorContext {

    private AutoAllocateTaskDO allocateCfg;
    private AutoFetchTaskDO fetchCfg;

    private ITaskPoolStrategy taskPoolStrategy;


    private IUserPoolStrategy userPoolStrategy;


    private IAllocateStrategy allocateStrategy;


    public List<UwpTaskDomain> loadTask(){
        return taskPoolStrategy.init();
    }

    public List<UwpUserDomain> loadUser(){
        return userPoolStrategy.init();
    }

    public int allocate(List<UwpTaskDomain> taskPool, List<UwpUserDomain> userPool,BaseTaskAllocator allocator){
        return allocateStrategy.allocate(taskPool,userPool,allocator);
    }

    public List compensateTask(){
        return taskPoolStrategy.compensate();
    }

    public List compensateUser(){
        return userPoolStrategy.compensate();
    }

    public ITaskPoolStrategy getTaskPoolStrategy() {
        return taskPoolStrategy;
    }

    public void setTaskPoolStrategy(ITaskPoolStrategy taskPoolStrategy) {
        this.taskPoolStrategy = taskPoolStrategy;
    }

    public IUserPoolStrategy getUserPoolStrategy() {
        return userPoolStrategy;
    }

    public void setUserPoolStrategy(IUserPoolStrategy userPoolStrategy) {
        this.userPoolStrategy = userPoolStrategy;
    }

    public IAllocateStrategy getAllocateStrategy() {
        return allocateStrategy;
    }

    public void setAllocateStrategy(IAllocateStrategy allocateStrategy) {
        this.allocateStrategy = allocateStrategy;
    }

    public AutoAllocateTaskDO getAllocateCfg() {
        return allocateCfg;
    }

    public void setAllocateCfg(AutoAllocateTaskDO allocateCfg) {
        this.allocateCfg = allocateCfg;
    }

    public AutoFetchTaskDO getFetchCfg() {
        return fetchCfg;
    }

    public void setFetchCfg(AutoFetchTaskDO fetchCfg) {
        this.fetchCfg = fetchCfg;
    }
}
