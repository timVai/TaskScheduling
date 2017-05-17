package com.jd.uwp.messagecenter.jsf.enums;

/**
 * 提醒类型
 * 2017年3月9日16:46:15
 */
public enum RemindTypeEnum {
    SYSTEM(10,10,"系统消息"),
    INDEX(11,11,"首页消息"),
    AFS_CLOSE(20,20,"售后关单消息"),
    AFS_SCHEDULE(21,21,"售后调度消息"),
    AFS_CANCEL(22,22,"售后取消消息"),
    WOMS(30,30,"工单消息"),
    FEEDBACK(40,40,"意见反馈消息"),
    MEMO(50,50,"备忘消息"),
    TASK_NEW(60,60,"新增任务消息"),
    TASK_CLOSE(61,61,"关闭任务提醒消息"),
    TASK_SCHEDULE(62,62,"调度任务消息"),
    TASK_CANCEL(63,63,"取消任务消息");

    private Integer value ;
    private Integer remindValue ;
    private String name ;
    public Integer getValue() {
        return value;
    }

    public Integer getRemindValue() {
        return remindValue;
    }

    public String getName() {
        return name;
    }
    private RemindTypeEnum(Integer value,Integer remindValue, String name){
        this.value = value;
        this.remindValue = remindValue;
        this.name = name;
    }

    /**
     *
     * @Title: getValueName
     * @param @param value
     * @throws
     */
    public static String getValueName(int value) {
        for (RemindTypeEnum o : RemindTypeEnum.values()) {
            if (value == o.getValue()) {
                return o.getName();
            }
        }
        return null;
    }
}
