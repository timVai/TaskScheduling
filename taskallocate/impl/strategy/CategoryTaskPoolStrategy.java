package com.jd.uwp.service.taskallocate.impl.strategy;

import com.jd.uwp.common.annotation.UmpProxy;
import com.jd.uwp.common.enums.UwpTaskPriorityEnum;
import com.jd.uwp.common.enums.UwpTaskStateEnum;
import com.jd.uwp.common.util.BeanCopierUtils;
import com.jd.uwp.common.util.JsonHelper;
import com.jd.uwp.common.util.UwpServiceLocator;
import com.jd.uwp.dao.task.UwpTaskMapper;
import com.jd.uwp.domain.model.task.UwpTask;
import com.jd.uwp.domain.model.task.UwpTaskQuery;
import com.jd.uwp.domain.remote.UwpUser;
import com.jd.uwp.domain.uwpTask.UwpTaskDomain;
import com.jd.uwp.service.task.TaskManager;
import com.jd.uwp.service.taskallocate.ITaskPoolStrategy;
import com.jd.uwp.service.taskallocate.exception.PoolLackException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanfengshi on 2017/3/3.
 */
@Component
public class CategoryTaskPoolStrategy implements ITaskPoolStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryTaskPoolStrategy.class);

    private CategoryTaskPoolStrategy(){}
    public CategoryTaskPoolStrategy(CategoryTaskPoolParam param){
        taskManager = (TaskManager)UwpServiceLocator.getBean("taskManagerAfsImpl");
        this.param = param;
    }

    private CategoryTaskPoolParam param;

    TaskManager taskManager;

    @Override
    @UmpProxy
    public List init() throws PoolLackException {
        UwpTaskQuery toAllocate = new UwpTaskQuery();
        toAllocate.setTaskSource(param.getTaskSource());
        toAllocate.setTaskState(UwpTaskStateEnum.WAIT_FETCH.getValue());
        toAllocate.setTaskAuthority(param.getTaskAuthority());
        toAllocate.setTaskPriority(param.getTaskPriority());
        toAllocate.setCreateTimeEnd(param.getEndTime());
        toAllocate.setSortColumns("task_priority,id");
        toAllocate.setPageRequest(new PageRequest(0 , param.getInitCount()));
        LOGGER.info("正在初始化任务池数据，初始化参数："+ JsonHelper.toJSONString(param));
        Page<UwpTask> page = taskManager.getTaskPage(toAllocate);
        if(CollectionUtils.isEmpty(page.getContent())){
            throw new PoolLackException("没有加载到相应的任务");
        }

        List<UwpTaskDomain> tp = new ArrayList<UwpTaskDomain>();
        for(UwpTask task : page.getContent()){
            UwpTaskDomain taskDomain = new UwpTaskDomain(task);
            taskDomain.setUser(new UwpUser());
            tp.add(taskDomain);
        }
        LOGGER.info("初始化任务池完毕，任务个数："+ tp.size());
        return tp;
    }

    @Override
    @UmpProxy
    public List compensate() throws PoolLackException {
        UwpTaskQuery toAllocate = new UwpTaskQuery();
        toAllocate.setTaskSource(param.getTaskSource());
        toAllocate.setTaskState(UwpTaskStateEnum.WAIT_FETCH.getValue());
        toAllocate.setTaskAuthority(param.getTaskAuthority());
        toAllocate.setTaskPriority(param.getTaskPriority());
        toAllocate.setCreateTimeEnd(param.getEndTime());
        toAllocate.setSortColumns("task_priority,id");
        toAllocate.setPageRequest(new PageRequest(0 , param.getCompensateCount()));
        LOGGER.info("开始补偿任务池数据，补偿参数：" + JsonHelper.toJSONString(toAllocate));
        Page<UwpTask> page = taskManager.getTaskPage(toAllocate);
        if(CollectionUtils.isEmpty(page.getContent())){
            throw new PoolLackException("没有加载到相应的任务");
        }
        List<UwpTaskDomain> tp = new ArrayList<UwpTaskDomain>();
        for(UwpTask task : page.getContent()){
            UwpTaskDomain taskDomain = new UwpTaskDomain(task);
            taskDomain.setUser(new UwpUser());
            tp.add(taskDomain);
        }
        LOGGER.info("补偿任务完毕，任务个数："+ tp.size());
        return tp;
    }

}
