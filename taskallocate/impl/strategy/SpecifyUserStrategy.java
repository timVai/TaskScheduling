package com.jd.uwp.service.taskallocate.impl.strategy;

import com.jd.uwp.brace.jsf.user.dto.UserStatusVo;
import com.jd.uwp.common.enums.user.UserStatusEnum;
import com.jd.uwp.common.util.UwpServiceLocator;
import com.jd.uwp.domain.remote.UwpAfsUserInfoExport;
import com.jd.uwp.domain.remote.UwpUser;
import com.jd.uwp.domain.user.UwpUserDomain;
import com.jd.uwp.rpc.brace.UwpUserRpc;
import com.jd.uwp.service.afsuser.AfsUser;
import com.jd.uwp.service.taskallocate.IUserPoolStrategy;
import com.jd.uwp.service.taskallocate.exception.PoolLackException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 指定客服
 * Created by fanfengshi on 2017/3/3.
 */
@Component
public class SpecifyUserStrategy implements IUserPoolStrategy{

    UwpUserDomain user;

    private SpecifyUserStrategy(){}
    public SpecifyUserStrategy(UwpUserDomain user){
        if(user == null){
            throw new PoolLackException("指定客服参数错误");
        }
        uwpUserRpc = (UwpUserRpc)UwpServiceLocator.getBean("uwpUserRpc");
        afsUser = (AfsUser)UwpServiceLocator.getBean("afsUser");
        this.user = user;
    }

    UwpUserRpc uwpUserRpc;
    AfsUser afsUser;
    @Override
    public List<UwpUserDomain> init() throws PoolLackException {
        List<UwpUserDomain> userDomains = new ArrayList<UwpUserDomain>();
        userDomains.add(user);
        return userDomains;
    }

    @Override
    public List<UwpUserDomain> compensate() throws PoolLackException {
        throw new NotImplementedException("指定用户后不能再补偿");
    }
}
