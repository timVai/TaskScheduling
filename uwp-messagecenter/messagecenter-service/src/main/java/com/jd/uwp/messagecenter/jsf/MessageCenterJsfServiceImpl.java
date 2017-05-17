package com.jd.uwp.messagecenter.jsf;


import com.jd.ump.annotation.JProfiler;
import com.jd.uwp.common.utils.BeanCopierUtils;
import com.jd.uwp.common.utils.JsonUtil;
import com.jd.uwp.domain.model.MessageRemind;
import com.jd.uwp.messagecenter.jsf.domain.MessageRemindDto;
import com.jd.uwp.messagecenter.jsf.service.MessageCenterJsfServiceProvider;
import com.jd.uwp.messagecenter.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by qichongqun on 2017/3/9.
 */
@Service
public class MessageCenterJsfServiceImpl implements MessageCenterJsfServiceProvider {

    private static Logger logger = LoggerFactory.getLogger(MessageCenterJsfServiceImpl.class);

    @Resource
    private MessageService messageService;

    @Override
    @JProfiler(jKey = "Uwp.MessageCenter.Method.MessageCenterJsfServiceImpl.sendMessageRemind")
    public void sendMessageRemind(MessageRemindDto messageRemindDto) {
        if(messageRemindDto != null){

            logger.info("收到提醒消息，内容：" + JsonUtil.toJson(messageRemindDto));

            MessageRemind messageRemind = new MessageRemind();
            BeanCopierUtils.copyProperties(messageRemindDto,messageRemind);

            messageService.sendMessageToClient(messageRemind);
        }
    }
}
