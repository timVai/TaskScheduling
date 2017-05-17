package com.jd.uwp.mq;


import com.jd.common.util.StringUtils;
import com.jd.jmq.client.consumer.MessageListener;
import com.jd.jmq.common.message.Message;
import com.jd.uwp.common.utils.JsonUtil;
import com.jd.uwp.domain.model.MessageRemind;
import com.jd.uwp.messagecenter.cache.CacheService;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * 提醒消息JMQ接收
 * @author: qichongqun
 * @version: 2017年5月5日09:28:35
 */
public class RemindMessageListener implements MessageListener{
    private static final Logger logger = LoggerFactory.getLogger(RemindMessageListener.class);

    @Resource
    private CacheService cacheService;

    @Override
    public void onMessage(List<Message> messages) throws Exception {
        for (Message message : messages) {
            logger.info("*****JMQ接收到消息内容****"+message.getText());
            consumer(message);
        }
    }

    private void consumer(Message message) throws Exception {
        if(null == message || StringUtils.isEmpty(message.getText())) {
            logger.info("RemindMessageListener消费端messages为空");
        }else{
            MessageRemind messageRemind = null;
            try {
                messageRemind = com.alibaba.fastjson.JSON.parseObject(message.getText(), MessageRemind.class);
            } catch (Exception e) {
                logger.error("消息提醒JSON串转换异常，message:"+message.getText());
            }
            if(messageRemind != null && messageRemind.getRemindUser() != null){
                Channel channel = cacheService.getChannel(messageRemind.getRemindUser());
                if(channel != null){
                    sendMessageToClient(channel, JsonUtil.toJson(messageRemind));
                    logger.info("推送消息成功，内容："+JsonUtil.toJson(messageRemind));
                }else{
                    logger.error("提醒消息服务器中未取到通知人："+JsonUtil.toJson(messageRemind));
                }
            }
        }

    }

    public void sendMessageToClient(Channel channel, String message) {
        if(channel != null){
            channel.write(new TextWebSocketFrame(message));
        }
    }
}

