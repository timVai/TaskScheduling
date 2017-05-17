package com.jd.uwp.service.taskallocate;

import com.jd.uwp.domain.uwpTask.UwpTaskDomain;
import com.jd.uwp.service.taskallocate.exception.StopAllocateException;

/**
 * 任务观察
 * Created by fanfengshi on 2017/3/3.
 */
public interface TaskObserver {
    /**
     * 任务被分派了
     * @param taskVO
     * @throws StopAllocateException  当观察者处理业务时，需要终止这次分派时则应该抛出指定异常(Exception也不好使)
     */
    public void taskAllocated(UwpTaskDomain taskVO) throws StopAllocateException;
}
