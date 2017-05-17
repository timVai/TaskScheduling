package com.jd.uwp.domain.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * domain层基类，包含基础 的6个系统字段
 * (createPin、createDate、updatePin、updateDate、sysVersion、yn)
 * 默认值：
 * yn=true
 *
 * @author wenjun
 */
@XmlRootElement(name = "BaseDomain")
public class BaseDomain implements Serializable {
    private static final long serialVersionUID = -1044579091100261195L;
    protected static final String DATE_FORMAT = "yyyy-MM-dd";
    protected static final String TIME_FORMAT = "HH:mm:ss";
    protected static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    protected static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
    public static long DEFAULT_NULL = -1L;
}