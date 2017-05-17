package com.jd.uwp.service.taskallocate;

import com.jd.uwp.domain.user.UwpUserDomain;
import com.jd.uwp.service.taskallocate.exception.PoolLackException;

import java.util.List;

/**
 * 人员池加载策略抽象
 * Created by fanfengshi on 2017/3/2.
 */
public interface IUserPoolStrategy {


    public List<UwpUserDomain> init() throws PoolLackException;

    public List<UwpUserDomain> compensate() throws PoolLackException;
}
