package com.jd.uwp.messagecenter.jmq;

import com.jd.jmq.client.producer.MessageProducer;
import com.jd.jmq.common.message.Message;
import com.jd.uwp.domain.model.MessageRemind;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by qichongqun on 2017/3/7.
 */
@Service
public class SendJmqMessageServiceImpl implements SendJmqMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendJmqMessageServiceImpl.class);

    @Resource
    MessageProducer messageProducer;
    @Value("${jmq.answer.remind.topic}")
    String answerRemindMessage="answerRemindMessage";

    private static ObjectMapper mapper = new ObjectMapper();


    @Override
    public void sendRequestAnswerMessageToMq(MessageRemind messageRemind) {
        try {
            LOGGER.info("发送MQ到提醒反馈，入参：" + mapper.writeValueAsString(messageRemind));
            Message message = new Message(answerRemindMessage,mapper.writeValueAsString(messageRemind),"1");
            messageProducer.send(message);
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("发送MQ到提醒反馈,异常：",e);
        }
    }

}
