package com.jd.uwp.service.taskallocate.impl.strategy;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.usf.ams.service.ResourceFacade;
import com.jd.usf.context.Context;
import com.jd.usf.login.ErpLoginContextUtil;
import com.jd.usf.service.ResultMsg;
import com.jd.uwp.common.util.JsonHelper;
import com.jd.uwp.common.util.UwpServiceLocator;
import com.jd.uwp.domain.remote.UwpAfsUserInfoExport;
import com.jd.uwp.domain.remote.UwpUser;
import com.jd.uwp.domain.user.UwpUserDomain;
import com.jd.uwp.rpc.afs.AfsUserInfoRpc;
import com.jd.uwp.rpc.brace.UwpUserRpc;
import com.jd.uwp.service.afsuser.AfsUser;
import com.jd.uwp.service.taskallocate.IUserPoolStrategy;
import com.jd.uwp.service.taskallocate.exception.PoolLackException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by fanfengshi on 2017/3/3.
 */
@Component
public class UpgradeCategoryUsrPoolStrategy implements IUserPoolStrategy{

    private static final Logger LOGGER = LoggerFactory.getLogger(UpgradeCategoryUsrPoolStrategy.class);


    private UpgradeCategoryUsrPoolParam param;

    private UpgradeCategoryUsrPoolStrategy(){}
    public UpgradeCategoryUsrPoolStrategy(UpgradeCategoryUsrPoolParam param){
        uwpUserRpc = (UwpUserRpc)UwpServiceLocator.getBean("uwpUserRpc");
        afsUser = (AfsUser)UwpServiceLocator.getBean("afsUser");
        afsUserInfoRpc = (AfsUserInfoRpc)UwpServiceLocator.getBean("afsUserInfoRpc");
        resourceFacade = (ResourceFacade)UwpServiceLocator.getBean("resourceFacade");
        this.param = param;
    }

    UwpUserRpc uwpUserRpc;
    AfsUser afsUser;
    AfsUserInfoRpc afsUserInfoRpc;
    ResourceFacade resourceFacade;

    private static final Integer MAX_SKILL_COUNT = Integer.valueOf(3);

    private Integer userSkillCount = Integer.valueOf(1);

    private List<UwpUserDomain> allUsers = null;

    /**
     * 获取可以处理指定售后分类 且在线的用户
     */
    private void getCanFetchAndOnLineUsers(UpgradeCategoryUsrPoolParam param){
        LOGGER.info("按售后审核技能和是否升级加载用户池开始:" + param.getTaskAuthority()+",优先级："+param.getPriority());
        List<UwpAfsUserInfoExport> afsUsers = afsUserInfoRpc.getJINGDONGUserList(Integer.valueOf(param.getTaskAuthority()), Integer.valueOf(1));
        if(CollectionUtils.isEmpty(afsUsers)){
            throw new PoolLackException("无可审核此品类的客服" + param.getTaskAuthority());
        }
        //erp的stringList 方便传参数
        List<String> erpStrList = new ArrayList<String>();
        for(UwpAfsUserInfoExport u : afsUsers){
            erpStrList.add(u.getErpCode());
        }

        Map<String,UwpAfsUserInfoExport> afsUserMap = trans2Map(afsUsers);

// 处理升级单的权限
        List<String> upgradeUsers = Lists.newArrayList();
        Context ctx = new Context();
        ctx.setAppCode("uwp");
        ctx.setAppToken("e9afep52wov8vserhdya4n");
        ctx.setIp("");
        ResultMsg resultMsg = resourceFacade.hasPermissions(ctx, erpStrList, "uwp_business_upgrade");
        if(resultMsg!=null && resultMsg.isFlag()){
            upgradeUsers = resultMsg.getData();
        }else{
            LOGGER.warn("没有加载到升级权限的用户。"+erpStrList.size()+"；param:"+JsonHelper.toJSONString(param));
        }

        List<UwpUser> onLineUsers = uwpUserRpc.ifOnLineUsers(erpStrList,Lists.newArrayList(Integer.valueOf(1),Integer.valueOf(3)));

        if(CollectionUtils.isEmpty(onLineUsers)){
            throw new PoolLackException("无在线可接单客服" + param.getTaskAuthority());
        }

        List<UwpUserDomain> userDomains = new ArrayList<UwpUserDomain>();
        for(UwpUser u : onLineUsers){
            UwpUserDomain userDomain = new UwpUserDomain(u);
            UwpAfsUserInfoExport info = afsUserMap.get(u.getErp().trim());
            if(info == null){
                LOGGER.warn("售后用户的技能信息查询为空。erp:"+u.getErp()+"。");
                continue;
            }
            if(!upgradeUsers.contains(u.getErp().trim())){//没有升级权限
                continue;
            }

            userDomain.setTaskAuthorities(info.getCategoryIdRelation());
            userDomains.add(userDomain);
        }
        allUsers = userDomains;
        LOGGER.info("按售后审核技能加载用户池完毕:" + param.getTaskAuthority() + "。加载人数：" +allUsers.size());
    }

    @Override
    public List<UwpUserDomain> init() throws PoolLackException {
        LOGGER.info("初始化用户池，参数："+ JsonHelper.toJSONString(param));

        getCanFetchAndOnLineUsers(param);

        List<UwpUserDomain> returnUserDomains = new ArrayList<UwpUserDomain>();

        LOGGER.info("开始抓取技能个数为"+userSkillCount+"的客服。");
        for(UwpUserDomain user : allUsers) {

            if(contains(user.getTaskAuthorities(), param.getTaskAuthority())){//可以处理此任务权限的
                //优先 单一技能的
                if(userSkillCount.equals(user.getTaskAuthorities().size())){
                    returnUserDomains.add(user);
                }
            }
    }

        if(CollectionUtils.isNotEmpty(returnUserDomains)){
            LOGGER.info("返回技能个数为"+userSkillCount+"的客服。客服个数："+ returnUserDomains.size());
            return returnUserDomains;// 单一技能的
        }else{
            return compensate();
        }
    }

    @Override
    public List<UwpUserDomain> compensate() throws PoolLackException {
        List<UwpUserDomain> returnUserDomains = new ArrayList<UwpUserDomain>();

        userSkillCount = userSkillCount + 1;
        LOGGER.info("开始抓取技能个数为"+userSkillCount+"的客服。");

        if(userSkillCount <= MAX_SKILL_COUNT){
            for(UwpUserDomain user : allUsers){
                if(contains(user.getTaskAuthorities(), param.getTaskAuthority())){
                    if(userSkillCount.equals(user.getTaskAuthorities().size())){//技能个数
                        returnUserDomains.add(user);
                    }
                }
            }
        }else{
            LOGGER.info("无在线可接单客服，技能个数已抓取到：" + userSkillCount);
            throw new PoolLackException("无在线可接单客服，任务数据权限：" + param.getTaskAuthority());
        }
        if(CollectionUtils.isNotEmpty(returnUserDomains)){
            LOGGER.info("返回技能个数为"+userSkillCount+"的客服。客服个数："+ returnUserDomains.size());
            return returnUserDomains;
        }else{
            returnUserDomains = null;
            return compensate();
        }
    }

    private Boolean contains(Set<Integer> categories, String category){
        if(categories == null) return false;
        if(StringUtils.isEmpty(category)) return false;
        for(Integer c : categories){
            if(category.equals(c.toString())){
                return true;
            }
        }
        return false;
    }
    private Map<String,UwpAfsUserInfoExport> trans2Map(List<UwpAfsUserInfoExport> list){
        return Maps.uniqueIndex(list.iterator(), new Function<UwpAfsUserInfoExport, String>() {
            @Override
            public String apply(UwpAfsUserInfoExport info){
                return info.getErpCode().trim();
            }
        });
    }

}
