package com.jd.uwp.messagecenter.service;


import com.jd.uwp.common.utils.JsonUtil;
import com.jd.uwp.domain.model.Message;
import com.jd.uwp.domain.model.MessageRemind;
import com.jd.uwp.domain.model.utils.ProtocolUtils;
import com.jd.uwp.messagecenter.Token;
import com.jd.uwp.messagecenter.cache.CacheService;
import com.jd.uwp.messagecenter.jmq.SendJmqMessageService;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * Created by qichongqun on 2017/3/7.
 */
public class MessageServiceImpl implements MessageService{

    private static Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private CacheService cacheService;
    @Resource
    private SendJmqMessageService sendJmqMessageService;

    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public void setSendJmqMessageService(SendJmqMessageService sendJmqMessageService) {
        this.sendJmqMessageService = sendJmqMessageService;
    }

    @Override
    public void sendMessageToClient(Channel channel, String message) {
        if(channel != null){
            channel.write(new TextWebSocketFrame(message));
        }
    }

    @Override
    public void sendMessageToClient(MessageRemind messageRemind) {
        if(messageRemind != null && messageRemind.getRemindType() != null){
            if(messageRemind.getRemindUser() != null){
                Channel channel = cacheService.getChannel(messageRemind.getRemindUser());
                if(channel != null){
                    sendMessageToClient(channel,JsonUtil.toJson(messageRemind));
                    logger.info("推送消息成功，内容："+JsonUtil.toJson(messageRemind));
                }else{
                    logger.error("提醒消息服务器中未取到通知人："+JsonUtil.toJson(messageRemind));
                }
            }else{
                logger.error("提醒消息中没有通知人："+JsonUtil.toJson(messageRemind));
            }
        }
    }

    @Override
    public void sendMessageToService(Token token) {
        final String message = token.getMessage();
        MessageRemind messageRemind = null;
        try {
            messageRemind = JsonUtil.fromJson(message, MessageRemind.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (messageRemind != null) {

            sendJmqMessageService.sendRequestAnswerMessageToMq(messageRemind);

        }
        logger.info("收到返回消息，并发送到应答消息队列，内容：" + JsonUtil.toJson(messageRemind));
    }

    @Override
    public void sendCloseMessageToService(MessageRemind messageRemind) {
        if (messageRemind != null) {
            sendJmqMessageService.sendRequestAnswerMessageToMq(messageRemind);
            logger.info("发送异常关闭消息更新状态，账号：" + messageRemind.getRemindUser());
        }
    }

    @Override
    public void sendMessage(final Channel channel, final Message event) {
        if (channel != null && event != null) {
            String strMessage = ProtocolUtils.toJSON(event);
            channel.write(new TextWebSocketFrame(strMessage));
        }
    }
}
