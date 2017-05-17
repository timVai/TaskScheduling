package com.jd.uwp.messagecenter.cache;

import org.jboss.netty.channel.Channel;

import java.util.List;

/**
 * 缓存服务
 */
public interface CacheService {
	Channel getChannel(String agentId);

	void setChannel(String agentId, Channel channel);

	void removeChannel(String agentId);
	
	boolean isLoggedOn(String agentId);
	
	List<Channel> getChannels();

	void removeChannelById(Integer channelId);

	String getAgentId(Channel channel);

}
