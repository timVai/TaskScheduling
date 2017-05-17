package com.jd.uwp.messagecenter.jmq;

import com.jd.uwp.domain.model.MessageRemind;

/**
 * Created by qichongqunn on 2017/3/8.
 * 发送消息
 */
public interface SendJmqMessageService {

    public void sendRequestAnswerMessageToMq(MessageRemind messageRemind);
}
