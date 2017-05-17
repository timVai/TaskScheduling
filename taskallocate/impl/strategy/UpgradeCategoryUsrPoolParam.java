package com.jd.uwp.service.taskallocate.impl.strategy;

import com.jd.uwp.common.enums.UwpTaskPriorityEnum;

import java.util.Date;

/**
 * Created by fanfengshi on 2017/3/13.
 */
public class UpgradeCategoryUsrPoolParam {

    private String taskAuthority;

    private UwpTaskPriorityEnum priority;

    public UwpTaskPriorityEnum getPriority() {
        return priority;
    }

    public void setPriority(UwpTaskPriorityEnum priority) {
        this.priority = priority;
    }

    public String getTaskAuthority() {
        return taskAuthority;
    }

    public void setTaskAuthority(String taskAuthority) {
        this.taskAuthority = taskAuthority;
    }
}
