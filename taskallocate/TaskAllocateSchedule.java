package com.jd.uwp.schedule;

import com.jd.clover.center.service.AbstractScheduleTaskProcess;
import com.jd.clover.schedule.domain.TaskServerParam;
import com.jd.clover.util.CoreUtil;
import com.jd.ump.annotation.JProfiler;
import com.jd.uwp.common.annotation.UmpProxy;
import com.jd.uwp.common.enums.ScheduleGroupEnum;
import com.jd.uwp.common.enums.UwpTaskSourceEnum;
import com.jd.uwp.common.util.JsonHelper;
import com.jd.uwp.common.util.UwpServiceLocator;
import com.jd.uwp.domain.model.schedule.UwpTaskSchedule;
import com.jd.uwp.domain.uwpTask.AutoAllocateTaskDO;
import com.jd.uwp.service.task.TaskManager;
import com.jd.uwp.service.taskallocate.TaskObserver;
import com.jd.uwp.service.taskallocate.exception.PoolLackException;
import com.jd.uwp.service.taskallocate.impl.AllocatorContext;
import com.jd.uwp.service.taskallocate.impl.BaseTaskAllocator;
import com.jd.uwp.service.taskallocate.impl.strategy.*;
import com.jd.uwp.service.taskschedule.TaskScheduleService;
import com.jd.uwp.service.uwpconfig.UwpConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 任务分派的调度
 * Created by fanfengshi on 2017/3/3.
 */
public class TaskAllocateSchedule extends AbstractScheduleTaskProcess<AutoAllocateTaskDO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAllocateSchedule.class);

    @Resource
    UwpConfigService configService;
    @Resource
    TaskScheduleService taskScheduleService;

    @Resource(name = "baseTaskManagerImpl")
    TaskManager taskManager;

    @Override
    @JProfiler(jKey = "Afsuwp.Method.TaskAllocateSchedule.selectTasks", jAppName = "uwp-task-mq-work")
    protected List<AutoAllocateTaskDO> selectTasks(TaskServerParam serverParam, int curServer) {
        List<AutoAllocateTaskDO> myAutoAllocateTask = null;

        try {
            List<AutoAllocateTaskDO> configs = configService.findAutoAllocateConfigs();
            if(CollectionUtils.isEmpty(configs)){
                LOGGER.error("没有找到自动派单的配置参数，检查配置表。");
            }
            myAutoAllocateTask = new ArrayList<AutoAllocateTaskDO>();
            for (int i = 0; i < configs.size(); i++) {
                AutoAllocateTaskDO autoAllocateTaskDo = configs.get(i);
                if (CoreUtil.isMyTask(i, serverParam.getServerCount(), curServer)) {

                    String taskPriority = autoAllocateTaskDo.getTaskAuthority();
                    Long interval = autoAllocateTaskDo.getInterval();

                    UwpTaskSchedule scheduleRecord = taskScheduleService.findScheduleRecord(ScheduleGroupEnum.AFS_TASK, taskPriority);
                    Date now = new Date();
                    if (null != scheduleRecord) {
                        //有上次调用的记录，则需要比较间隔
                        Date lastTime = scheduleRecord.getLastTimeStamp();
                        if(lastTime.getTime() + interval <= now.getTime()){//是否满足调度条件
                            scheduleRecord.setLastTimeStamp(now);
                            scheduleRecord.setScheduleHolder(curServer);
                            autoAllocateTaskDo.setScheduleRecord(scheduleRecord);
                            myAutoAllocateTask.add(autoAllocateTaskDo);
                        }else{
                            continue;
                        }
                    }else{
                        //如果没有记录，可能是第一次分派。需要在分派完后 插入一条新记录
                        UwpTaskSchedule firstScheduleRecord = new UwpTaskSchedule();
                        firstScheduleRecord.setScheduleGroup(ScheduleGroupEnum.AFS_TASK.getValue());
                        firstScheduleRecord.setScheduleHolder(curServer);
                        firstScheduleRecord.setLastTimeStamp(now);
                        firstScheduleRecord.setScheduleParam(taskPriority);
                        autoAllocateTaskDo.setScheduleRecord(firstScheduleRecord);
                        myAutoAllocateTask.add(autoAllocateTaskDo);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取派单任务出错！", e);
        }
        return myAutoAllocateTask;
    }

    @Override
    @JProfiler(jKey = "Afsuwp.Method.TaskAllocateSchedule.executeTasks", jAppName = "uwp-task-mq-work")
    protected void executeTasks(List<AutoAllocateTaskDO> works) {
        LOGGER.error("线程ID:"+Thread.currentThread().getId());
        for (AutoAllocateTaskDO autoAllocate : works) {
                doWork(autoAllocate);
        }
        LOGGER.error("线程ID:"+Thread.currentThread().getId()+"反馈。");
    }
    @JProfiler(jKey = "Afsuwp.Method.TaskAllocateSchedule.doWork", jAppName = "uwp-task-mq-work")
    private void doWork(AutoAllocateTaskDO autoAllocateTaskDO){
        AllocatorContext context = null;
        BaseTaskAllocator allocator = null;
        try {
            context = new AllocatorContext();

            context.setAllocateCfg(autoAllocateTaskDO);

            CategoryTaskPoolParam taskPoolParam = new CategoryTaskPoolParam();
            taskPoolParam.setTaskAuthority(autoAllocateTaskDO.getTaskAuthority());
            taskPoolParam.setTaskSource(UwpTaskSourceEnum.AFS_TASK.getValue());
            taskPoolParam.setInitCount(200);
            taskPoolParam.setCompensateCount(200);
            taskPoolParam.setEndTime(new Date());//取截止到当前的任务
            context.setTaskPoolStrategy(new CategoryTaskPoolStrategy(taskPoolParam));

            CategoryUserPoolParam userPoolParam = new CategoryUserPoolParam();
            userPoolParam.setTaskAuthority(autoAllocateTaskDO.getTaskAuthority());
            context.setUserPoolStrategy(new CategoryUserPoolStrategy(userPoolParam));

            context.setAllocateStrategy(new AverageAllocateStrategy());

            allocator = new BaseTaskAllocator(context);
            TaskObserver observer = (TaskObserver) UwpServiceLocator.getBean("baseTaskManagerImpl");
            allocator.addTaskObserver(observer);
            allocator.startAllocate();


            LOGGER.info("自动派单，任务权限组【"+autoAllocateTaskDO.getTaskAuthority()+"】，派单完毕。");
        } catch (PoolLackException e) {
            LOGGER.warn("自动派单终止，终止原因：" , e);
        }catch (Exception e) {
            LOGGER.error("自动派单发生系统异常，需排查。" + JsonHelper.toJSONString(autoAllocateTaskDO), e);
        }finally {
            //当前调度组完成一个调度，同步更新记录。
            taskScheduleService.saveOrUpdateRecord(autoAllocateTaskDO.getScheduleRecord());
        }
    }
}
