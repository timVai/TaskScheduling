package com.jd.uwp.messagecenter.service;


import com.jd.uwp.domain.model.Message;
import com.jd.uwp.domain.model.MessageRemind;
import com.jd.uwp.messagecenter.Token;
import org.jboss.netty.channel.Channel;

/**
 * Created by qichongqun on 2017/3/7.
 */
public interface MessageService {

    /*
    发送字符串消息到channel
     */
    void sendMessageToClient(Channel channel, String message);
    /*
    发送提醒消息到客户端
     */
    void sendMessageToClient(MessageRemind messageRemind);

    /*
    发送消息到服务端mq
     */
    void sendMessageToService(Token token);

    /*
    发送异常关闭消息到服务端mq
     */
    void sendCloseMessageToService(MessageRemind messageRemind);

    /*
    发送系统消息到客户端
     */
    void sendMessage(Channel channel, Message message);
}
