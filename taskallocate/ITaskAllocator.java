package com.jd.uwp.service.taskallocate;


import com.jd.uwp.domain.uwpTask.UwpTaskDomain;
import com.jd.uwp.service.taskallocate.impl.AllocatorContext;

/**
 * Created by fanfengshi on 2017/3/2.
 * 平台任务分发器：
 * 实现类需持有任务池、人员池和分派策略环境，
 * 内部实现分派细节。
 * 接口用于通知分派结果
 */
public interface ITaskAllocator {

    /**
     * 添加任务的观察者
     * @param observer
     * @return
     */
    public boolean addTaskObserver(TaskObserver observer);

    /**
     * 开始分派
     */
    public void startAllocate();

    /**
     * 获取上下文
     * @return
     */
    public AllocatorContext getContext();

}
