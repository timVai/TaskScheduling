package com.jd.uwp.messagecenter.cache;

import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存
 */
public class CacheServiceImpl implements CacheService {
	private static Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);
	private final ConcurrentMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
	
	@Override
	public Channel getChannel(final String agentId) {
		final Channel channel = channels.get(agentId);
		if (channel != null) {
			return channel;
		}
		return null;
	}

	@Override
	public void setChannel(final String agentId,
			final Channel channel) {
		channels.put(agentId, channel);
		logger.info("Cache channel {} by {}.", channel, agentId);
	}

	@Override
	public void removeChannel(final String agentId) {
		channels.remove(agentId);
		logger.info("Remove {}'s channel from cache.", agentId);
	}

	@Override
	public boolean isLoggedOn(final String agentId) {
		if (channels.containsKey(agentId)) {
			return true;
		}
		return false;
	}

	@Override
	public List<Channel> getChannels() {
		List<Channel> channelList = new ArrayList<Channel>();
		Iterator<Entry<String, Channel>> entries = channels.entrySet().iterator();
		while(entries.hasNext()) {
			Entry<String, Channel> entry = entries.next();
			channelList.add(entry.getValue());
		}
		return channelList;
	}

	@Override
	public void removeChannelById(Integer channelId) {

        Iterator<Map.Entry<String, Channel>> entries = channels.entrySet().iterator();
        while(entries.hasNext()) {
            Map.Entry<String, Channel> entry = entries.next();
            Channel channel = entry.getValue();
            if (channel != null && channel.getId() != null) {
                if(channel.getId().equals(channelId)) {
                    entries.remove();
                    logger.info("Remove channelId[{}]'s channel[{}] from cache.", channelId, channel);
                    break;
                }
            }
        }
	}

	@Override
	public String getAgentId(Channel channel) {
		Iterator<Map.Entry<String, Channel>> entries = channels.entrySet().iterator();
		while(entries.hasNext()) {
			Map.Entry<String, Channel> entry = entries.next();
			if (entry.getValue().equals(channel)) {
				return entry.getKey();
			}
		}
		return null;
	}

}