package com.jd.uwp.messagecenter.jsf.service;


import com.jd.uwp.messagecenter.jsf.domain.MessageRemindDto;

/**
 * Created by qichongqun on 2017/3/9.
 */
public interface MessageCenterJsfServiceProvider {

    /**
     *发送一条提醒
     * @param messageRemindDto
     * @return
     */
    public void sendMessageRemind(MessageRemindDto messageRemindDto);
}
