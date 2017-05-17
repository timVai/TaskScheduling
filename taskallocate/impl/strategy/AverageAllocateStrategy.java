package com.jd.uwp.service.taskallocate.impl.strategy;

import com.google.common.collect.Lists;
import com.jd.uwp.common.annotation.UmpProxy;
import com.jd.uwp.common.constants.SystemConstants;
import com.jd.uwp.common.enums.UwpFetchTypeEnum;
import com.jd.uwp.common.exception.task.UwpFetchFailException;
import com.jd.uwp.common.util.UwpServiceLocator;
import com.jd.uwp.domain.model.task.UwpTaskQuery;
import com.jd.uwp.domain.remote.UwpUser;
import com.jd.uwp.domain.user.UwpUserDomain;
import com.jd.uwp.domain.uwpTask.UwpTaskDomain;
import com.jd.uwp.service.task.TaskService;
import com.jd.uwp.service.taskallocate.IAllocateStrategy;
import com.jd.uwp.service.taskallocate.ITaskAllocator;
import com.jd.uwp.service.taskallocate.exception.StopAllocateException;
import com.jd.uwp.service.taskallocate.impl.BaseTaskAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * 平均分派
 * Created by fanfengshi on 2017/3/3.
 */
public class AverageAllocateStrategy implements IAllocateStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AverageAllocateStrategy.class);


    public AverageAllocateStrategy(){
        taskService = (TaskService)UwpServiceLocator.getBean("taskServiceAfsImpl");
    }

    TaskService<UwpTaskDomain,UwpUserDomain,UwpTaskQuery> taskService;

    BaseTaskAllocator allocator;
    List<UwpTaskDomain> myTaskList;
    Stack<UwpTaskDomain> myTaskStack;

    @Override
    public int allocate(List<UwpTaskDomain> taskPool, List<UwpUserDomain> userPool, BaseTaskAllocator outerAllocator) {

        list2Stack(taskPool);
        this.allocator = outerAllocator;

        //遍历在线用户
        for(int userIndex = 0 ; userIndex < userPool.size() ; userIndex++){
            //任务池空，需要补偿任务。
            if(myTaskStack.empty()){
                LOGGER.info("【分派过程】补偿任务。。。");
                allocator.compensateTask();
                list2Stack(taskPool);
                userIndex--;//应该继续为这个客服派单
                continue;
            }
            //当前客服
            UwpUserDomain user = userPool.get(userIndex);

            //栈顶任务
            UwpTaskDomain task = myTaskStack.peek();

            //达到配置阈值，分派结束
            if(user.getAllocatedCount() + 1 > allocator.getContext().getAllocateCfg().getLimitCount()){
                LOGGER.info("【分派过程】补偿客服。此批客服获取单量："+user.getAllocatedCount());
                userPool.clear();
                allocator.compensateUser();
                userIndex = -1;
                continue;
            }

            LOGGER.info("【分派过程】正在为"+user.getUwpUser().getErp()+ "领取id为"+task.getId()+"的任务。");
            int allocateResult = allocate(user, task);
            if(allocateResult > 0){
                //领取成功后应该进行的操作
                takeTaskAway();
                user.setAllocatedCount(user.getAllocatedCount() + 1);//在用户对象上记录已经分派的单量
            }else if(allocateResult <= 0){
                takeTaskAway();
                userIndex--;//应该继续为这个客服派单
            }
            LOGGER.info(new StringBuffer("【分派过程】为").append(user.getUwpUser().getErp())
                    .append("领取id为").append(task.getId()).append("的任务。结果")
                    .append(allocateResult)
                    .toString());

            LOGGER.info(new StringBuffer("【分派过程】").append(user.getUwpUser().getErp())
                    .append("名下已分派单量：").append(user.getAllocatedCount())
                    .toString());

            //处理最后一个客服后，如果还有任务，从头开始
            if(userIndex + 1 == userPool.size()){
                if(!myTaskStack.isEmpty()){
                    userIndex = -1;
                    continue;
                }
            }
        }

        return 1;
    }

    /**
     * 一人一单的分派
     * @return  -1 领取失败;1 领取成功
     */
    @Transactional
    private int allocate(UwpUserDomain user, UwpTaskDomain task){
        UwpTaskDomain t = null;
        task.setFetchType(UwpFetchTypeEnum.AUTO_ALLOCATE.getValue());
        try{
            //领取
            t = taskService.fetch(task, user);
        }catch (UwpFetchFailException re){//领取失败
            LOGGER.warn("平均分派过程中，分派失败。", re);
            return -1;
        }

        try {
            allocator.onceAllocated(t);
        } catch (StopAllocateException e) {
            LOGGER.error("观察领取动作的成员异常，领取无效。"+task.getId(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("观察领取动作的成员异常，但领取已完成。", e);
        }
        return 1;
    }

    private void list2Stack(List<UwpTaskDomain> taskPool){
        Collections.reverse(taskPool);//保证栈顶为最先任务
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
