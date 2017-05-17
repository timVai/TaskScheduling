package com.jd.uwp.service.taskallocate;

import com.jd.uwp.domain.user.UwpUserDomain;
import com.jd.uwp.domain.uwpTask.UwpTaskDomain;
import com.jd.uwp.service.taskallocate.impl.BaseTaskAllocator;

import java.util.List;

/**
 * 分派接口
 * Created by fanfengshi on 2017/3/3.
 */
public interface IAllocateStrategy {


    /**
     * 具体分派实现
     * @param taskPool
     * @param userPool
     * @param allocator  分派反馈
     * @return
     */
    public int allocate(List<UwpTaskDomain> taskPool, List<UwpUserDomain> userPool, BaseTaskAllocator allocator);

}
