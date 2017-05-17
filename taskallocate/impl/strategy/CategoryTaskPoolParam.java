package com.jd.uwp.service.taskallocate.impl.strategy;

import com.jd.uwp.common.enums.UwpTaskPriorityEnum;

import java.util.Date;
import java.util.List;

/**
 * Created by fanfengshi on 2017/3/13.
 */
public class CategoryTaskPoolParam {

    Integer taskSource;
    Integer taskPriority = UwpTaskPriorityEnum.NORMAL.getValue();
    List<String> taskAuthorities;
    String taskAuthority;
    Integer initCount;
    Integer compensateCount;

    //检索截止到此刻的任务
    Date endTime;

    public Integer getTaskSource() {
        return taskSource;
    }

    public void setTaskSource(Integer taskSource) {
        this.taskSource = taskSource;
    }

    public List<String> getTaskAuthorities() {
        return taskAuthorities;
    }

    public void setTaskAuthorities(List<String> taskAuthorities) {
        this.taskAuthorities = taskAuthorities;
    }

    public String getTaskAuthority() {
        return taskAuthority;
    }

    public void setTaskAuthority(String taskAuthority) {
        this.taskAuthority = taskAuthority;
    }

    public Integer getInitCount() {
        return initCount;
    }

    public void setInitCount(Integer initCount) {
        this.initCount = initCount;
    }

    public Integer getCompensateCount() {
        return compensateCount;
    }

    public void setCompensateCount(Integer compensateCount) {
        this.compensateCount = compensateCount;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(Integer taskPriority) {
        this.taskPriority = taskPriority;
    }
}
