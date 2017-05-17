package com.jd.uwp.domain.model;

/**
 * Created by qichongqun on 2017/3/8.
 */
public enum RemindType  {
    SYSTEM(10,"系统提醒"),
    AFS_CLOSE(20,"售后关单提醒"),
    WOMS(30,"工单提醒"),
    FEEDBACK(40,"意见反馈提醒"),
    MEMO(50,"备忘提醒");

    private final int value;
    private final String name;

    private RemindType(int value,String name) {
        this.value = value;
        this.name = name;
    }

    public int intValue() {
        return value;
    }

    public static RemindType valueOf(final int intValue) {
        for (RemindType remindType : values()) {
            if (remindType.intValue() == intValue) {
                return remindType;
            }
        }
        return null;
    }
}