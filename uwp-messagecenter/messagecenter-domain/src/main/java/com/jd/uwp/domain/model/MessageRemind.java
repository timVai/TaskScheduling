package com.jd.uwp.domain.model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by qichongqun on 2017/3/9.
 */
public class MessageRemind implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * ID       db_column: id
     */

    private Long id;
    /**
     * 消息类型       db_column: remind_type
     */

    private Integer remindType;
    /**
     * 提醒人账号       db_column: remind_user
     */
    private String remindUser;
    /**
     * 提醒内容       db_column: remind_content
     */
    private String remindContent;
    /**
     * 提醒状态       db_column: remind_state
     */
    private Integer remindState;

    /**
     * 业务单号       db_column: business_no
     */

    private Long businessNo;

    /**
     * 再次提醒时间       db_column: remind_date
     */

    private Date remindDate;

    /**
     * 再次提醒时间 开始
     */
    private Date remindDateBegin;

    /**
     * 再次提醒时间 结束
     */
    private Date remindDateEnd;
    /**
     * 创建时间       db_column: create_time
     */

    private Date createTime;
    /**
     * 创建人       db_column: create_user
     */
    @Length(max=50)
    private String createUser;
    /**
     * 修改时间       db_column: update_time
     */

    private Date updateTime;
    /**
     * 更新人       db_column: update_user
     */
    @Length(max=50)
    private String updateUser;
    /**
     * 版本号       db_column: sys_version
     */
    @NotNull
    private Integer sysVersion;
    /**
     * 删除标识       db_column: is_delete
     */
    @NotNull
    private Boolean isDelete;
    /**
     * ts       db_column: ts
     */

    private Date ts;
    //columns END

    /**
     * 设置ID
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * 获得ID
     */
    public Long getId() {
        return this.id;
    }
    /**
     * 设置消息类型
     */
    public void setRemindType(Integer value) {
        this.remindType = value;
    }

    /**
     * 获得消息类型
     */
    public Integer getRemindType() {
        return this.remindType;
    }
    /**
     * 设置提醒人账号
     */
    public void setRemindUser(String value) {
        this.remindUser = value;
    }

    /**
     * 获得提醒人账号
     */
    public String getRemindUser() {
        return this.remindUser;
    }
    /**
     * 设置提醒内容
     */
    public void setRemindContent(String value) {
        this.remindContent = value;
    }

    /**
     * 获得提醒内容
     */
    public String getRemindContent() {
        return this.remindContent;
    }
    /**
     * 设置提醒状态
     */
    public void setRemindState(Integer value) {
        this.remindState = value;
    }

    /**
     * 获得提醒状态
     */
    public Integer getRemindState() {
        return this.remindState;
    }

    public Long getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(Long businessNo) {
        this.businessNo = businessNo;
    }

    /**
     * 设置再次提醒时间
     */
    public void setRemindDate(Date value) {
        this.remindDate = value;
    }

    /**
     * 获得再次提醒时间
     */
    public Date getRemindDate() {
        return this.remindDate;
    }

    public Date getRemindDateBegin() {
        return remindDateBegin;
    }

    public void setRemindDateBegin(Date remindDateBegin) {
        this.remindDateBegin = remindDateBegin;
    }

    public Date getRemindDateEnd() {
        return remindDateEnd;
    }

    public void setRemindDateEnd(Date remindDateEnd) {
        this.remindDateEnd = remindDateEnd;
    }

    /**
     * 设置创建时间
     */
    public void setCreateTime(Date value) {
        this.createTime = value;
    }

    /**
     * 获得创建时间
     */
    public Date getCreateTime() {
        return this.createTime;
    }
    /**
     * 设置创建人
     */
    public void setCreateUser(String value) {
        this.createUser = value;
    }

    /**
     * 获得创建人
     */
    public String getCreateUser() {
        return this.createUser;
    }

    /**
     * 设置修改时间
     */
    public void setUpdateTime(Date value) {
        this.updateTime = value;
    }

    /**
     * 获得修改时间
     */
    public Date getUpdateTime() {
        return this.updateTime;
    }
    /**
     * 设置更新人
     */
    public void setUpdateUser(String value) {
        this.updateUser = value;
    }

    /**
     * 获得更新人
     */
    public String getUpdateUser() {
        return this.updateUser;
    }
    /**
     * 设置版本号
     */
    public void setSysVersion(Integer value) {
        this.sysVersion = value;
    }

    /**
     * 获得版本号
     */
    public Integer getSysVersion() {
        return this.sysVersion;
    }
    /**
     * 设置删除标识
     */
    public void setIsDelete(Boolean value) {
        this.isDelete = value;
    }

    /**
     * 获得删除标识
     */
    public Boolean getIsDelete() {
        return this.isDelete;
    }

    /**
     * 设置ts
     */
    public void setTs(Date value) {
        this.ts = value;
    }

    /**
     * 获得ts
     */
    public Date getTs() {
        return this.ts;
    }
      
}
