package com.jd.uwp.service.taskallocate.impl.strategy;

import com.jd.uwp.common.enums.UwpFetchTypeEnum;
import com.jd.uwp.common.exception.task.UwpFetchFailException;
import com.jd.uwp.common.util.UwpServiceLocator;
import com.jd.uwp.domain.user.UwpUserDomain;
import com.jd.uwp.domain.uwpTask.UwpTaskDomain;
import com.jd.uwp.service.task.TaskService;
import com.jd.uwp.service.taskallocate.IAllocateStrategy;
import com.jd.uwp.service.taskallocate.exception.PoolLackException;
import com.jd.uwp.service.taskallocate.exception.StopAllocateException;
import com.jd.uwp.service.taskallocate.impl.BaseTaskAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * 简单客服领单策略
 * Created by fanfengshi on 2017/3/3.
 */
@Component
@Transactional
public class SimpleFetchStrategy implements IAllocateStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFetchStrategy.class);


    public SimpleFetchStrategy(){
        taskService = (TaskService)UwpServiceLocator.getBean("baseTaskServiceImpl");
    }

    TaskService taskService;

    BaseTaskAllocator allocator;
    List<UwpTaskDomain> myTaskList;
    Stack<UwpTaskDomain> myTaskStack;

    @Override
    public int allocate(List<UwpTaskDomain> taskPool, List<UwpUserDomain> userPool, BaseTaskAllocator allocator) {
        this.allocator = allocator;

        //当前客服
        UwpUserDomain user = userPool.get(0);

        try {
            do{
                //任务池空，需要补偿任务。
                if(myTaskStack.empty()){
                    allocator.compensateTask();
                }
                list2Stack(taskPool);

                //栈顶任务
                UwpTaskDomain task = myTaskStack.peek();

                //达到配置阈值，分派结束
                Integer expectCount = allocator.getContext().getFetchCfg().getExpectCount();
                if(expectCount <= 0){
                    break;
                }

                int allocateResult = allocate(user, task);
                if(allocateResult > 0){
                    //领取成功后应该进行的操作
                    takeTaskAway();
                    user.setAllocatedCount(user.getAllocatedCount() + 1);//在用户对象上记录已经分派的单量
                    allocator.getContext().getFetchCfg().setExpectCount(expectCount - 1);
                }else if(allocateResult <= 0){
                    takeTaskAway();
                }
            }while (true);
        } catch (PoolLackException e) {

        }
        return 1;
    }

    /**
     * 一人一单的分派
     * @return  -1 领取失败;1 领取成功
     */
    @Transactional
    private int allocate(UwpUserDomain user, UwpTaskDomain task){

        task.setFetchType(UwpFetchTypeEnum.AUTO_ALLOCATE.getValue());
        try{
            //领取
            taskService.fetch(task, user);
        }catch (UwpFetchFailException re){//领取失败
            LOGGER.warn("平均分派过程中，分派失败。", re);
            return -1;
        }

        try {
            allocator.onceAllocated(task);
        } catch (StopAllocateException e) {
            LOGGER.error("观察领取动作的成员异常，领取无效。"+task.getId(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("观察领取动作的成员异常，但领取已完成。", e);
        }
        return 1;
    }

    private void list2Stack(List<UwpTaskDomain> taskPool){
        Collections.reverse(taskPool);

        Stack<UwpTaskDomain> taskStack =  new Stack();
        taskStack.addAll(taskPool);

        myTaskList = taskPool;
        myTaskStack = taskStack;
    }

    //移除栈顶的任务 并同步任务池
    private void takeTaskAway(){
        myTaskList.remove(myTaskStack.pop());
    }

}
