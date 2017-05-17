package com.jd.uwp.service.taskallocate.impl;

import com.jd.uwp.common.exception.UwpException;
import com.jd.uwp.domain.user.UwpUserDomain;
import com.jd.uwp.domain.uwpTask.UwpTaskDomain;
import com.jd.uwp.service.taskallocate.ITaskAllocator;
import com.jd.uwp.service.taskallocate.TaskObserver;
import com.jd.uwp.service.taskallocate.exception.PoolLackException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanfengshi on 2017/3/2.
 */
public class BaseTaskAllocator implements ITaskAllocator{

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTaskAllocator.class);

    public AllocatorContext context;

    private List<UwpTaskDomain> taskPool;//不能对池进行赋值操作!!!!!
    private List<UwpUserDomain> userPool;

    private List<TaskObserver> taskObservers = new ArrayList<TaskObserver>();

    public BaseTaskAllocator(AllocatorContext context){
        this.context = context;
    }

    //初始化加载两种资源池
    protected void load(){
        try{
            taskPool = context.loadTask();
            userPool = context.loadUser();
        }catch (PoolLackException poolLackEx){
            LOGGER.error("任务分派结束，任务池匮乏或无在线人员",poolLackEx.getMessage());
            throw poolLackEx;
        }
    }

    //开始分派
    public void startAllocate(){
        load();
        if(taskObservers.size() == 0){
            throw new UwpException("任务没有任何分派响应，不能启动分派");
        }
        context.allocate(taskPool,userPool,this);
    }

    public void compensateTask(){
        try {
            taskPool.addAll(context.compensateTask());
        }catch(PoolLackException ex){
            LOGGER.warn("任务池未加载到补偿任务，任务正常结束。");
            throw ex;
        }
    }

    public void compensateUser(){
        try {
            userPool.addAll(context.compensateUser());
        }catch(PoolLackException ex){
            LOGGER.warn("任务池未加载到补偿任务，任务正常结束。");
            throw ex;
        }
    }

    //通知观察者
    public void onceAllocated(UwpTaskDomain taskVO){
        for (TaskObserver observer:taskObservers){
            observer.taskAllocated(taskVO);
        }
    }
    //添加分派动作的任务观察者
    public boolean addTaskObserver(TaskObserver observer){
        return taskObservers.add(observer);
    }


    public AllocatorContext getContext(){
        return context;
    }

}
