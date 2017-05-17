package com.jd.uwp.messagecenter.service;

import com.jd.uwp.domain.model.MessageRemind;
import com.jd.uwp.domain.model.events.EventLinkConnected;
import com.jd.uwp.domain.model.events.EventPong;
import com.jd.uwp.domain.model.events.EventWelcome;
import com.jd.uwp.messagecenter.Token;
import com.jd.uwp.messagecenter.cache.CacheService;
import flexjson.JSONDeserializer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.*;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.net.SocketAddress;
import java.util.concurrent.*;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 *  WebSocket 处理
 */
public class WebSocketMessageHandler extends SimpleChannelUpstreamHandler implements InitializingBean {
	private static Logger logger = LoggerFactory.getLogger(WebSocketMessageHandler.class);
	private static final String WEBSOCKET_PATH = "/webSocket";
	private WebSocketServerHandshaker handshaker;
	private CacheService cacheService;
    private MessageService messageService;
	private final ScheduledExecutorService scheduledExecutor;
	private final ConcurrentMap<Integer, Long> heartbeats;

	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

	public WebSocketMessageHandler() {
		super();
		this.scheduledExecutor = Executors.newScheduledThreadPool(1);
		this.heartbeats = new ConcurrentHashMap<Integer, Long>();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object message = e.getMessage();
		if (message instanceof HttpRequest) {
			handleHttpRequest(ctx, (HttpRequest) message);
		} else if (message instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) message);
		}
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req)
			throws Exception {
		if (req.getMethod() != GET
				|| (!"/".equals(req.getUri())
						&& !WEBSOCKET_PATH.equals(req.getUri()))) {
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1,
					FORBIDDEN));
			return;
		}
		if ("/".equals(req.getUri())) {
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, OK));
		}
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				getWebSocketLocation(req), null, false);
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
		} else {
			handshaker.handshake(ctx.getChannel(), req).addListener(
					WebSocketServerHandshaker.HANDSHAKE_LISTENER);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, ExceptionEvent arg1)
			throws Exception {
		super.exceptionCaught(arg0, arg1);
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) {
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.getChannel(), (CloseWebSocketFrame) frame);
			return;
		} else if (frame instanceof PingWebSocketFrame) {
			ctx.getChannel().write(
					new PongWebSocketFrame(frame.getBinaryData()));
			return;
		} else if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format(
					"%s frame types not supported", frame.getClass().getName()));
		}

		final String requestMessage = ((TextWebSocketFrame) frame).getText();
		logger.debug("Received requestMessage from client {}:\n{}", ctx.getChannel(), requestMessage);
		try {
			final Token token = new JSONDeserializer<Token>().deserialize(
					requestMessage, Token.class);
			if ("login".equals(token.getType())) {
				final String agentId = token.getAgentId();
				cacheService.setChannel(agentId, ctx.getChannel());
				SocketAddress remoteAddress = ctx.getChannel().getRemoteAddress();
				int pos = remoteAddress.toString().indexOf(":");
				String host = remoteAddress.toString().substring(1, pos);
				String port = remoteAddress.toString().substring(pos + 1);
				final EventLinkConnected eventLinkConnected = new EventLinkConnected(
						ctx.getChannel().getId(), host, Integer.parseInt(port));
				eventLinkConnected.setAgentId(agentId);
                messageService.sendMessage(ctx.getChannel(), eventLinkConnected);
			} else if ("welcome".equals(token.getType())) {
				final EventWelcome eventWelcome = new EventWelcome();
				final String agentId = token.getAgentId();
				eventWelcome.setAgentId(agentId);
				eventWelcome.setMessage("ok");
                messageService.sendMessage(ctx.getChannel(), eventWelcome);
				updateHeartBeat(ctx.getChannel().getId());
			} else if ("ping".equals(token.getType())) {
				final EventPong eventPong = new EventPong();
				final String agentId = token.getAgentId();
				eventPong.setAgentId(agentId);
				eventPong.setMessage("ok");
                messageService.sendMessage(ctx.getChannel(), eventPong);
				updateHeartBeat(ctx.getChannel().getId());
				if (!cacheService.isLoggedOn(agentId)) {
					cacheService.setChannel(agentId, ctx.getChannel());
				}
			} else if("request".equals(token.getType())){
                logger.info("收到应答消息：账号：" + token.getAgentId());
                //发送客户端返回消息
                messageService.sendMessageToService(token);
			}
		} catch (Exception ex) {
			logger.error("Handle WebSocketFrame error, ", ex);
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	private static void sendHttpResponse(ChannelHandlerContext ctx,
			HttpRequest req, HttpResponse res) {
		if (res.getStatus().getCode() != 200) {
			res.setContent(ChannelBuffers.copiedBuffer(res.getStatus()
					.toString(), CharsetUtil.UTF_8));
			setContentLength(res, res.getContent().readableBytes());
		}

		ChannelFuture f = ctx.getChannel().write(res);
		if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private static String getWebSocketLocation(HttpRequest req) {
		return "ws://" + req.getHeader(HttpHeaders.Names.HOST) + WEBSOCKET_PATH;
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        final Channel channel = e.getChannel();
        final String agentId = cacheService.getAgentId(channel);
        logger.info("关闭 连接 ： " + agentId);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {

		final Channel channel = e.getChannel();
		final String agentId = cacheService.getAgentId(channel);
        logger.info("关闭 通道 ： " + agentId);
		if(agentId != null) {
			cacheService.removeChannel(agentId);

            //发送关闭消息到应答队列
            MessageRemind messageRemind = new MessageRemind();
            messageRemind.setRemindUser(agentId);
            messageService.sendCloseMessageToService(messageRemind);
		}
	}

	private Long getHeartBeat(Integer channelId) {
		return heartbeats.get(channelId);
	}

	private void updateHeartBeat(Integer channelId) {
		heartbeats.put(channelId, System.currentTimeMillis());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		scheduledExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				for(Integer channelId : heartbeats.keySet()) {
					Long hbtTime = getHeartBeat(channelId);
					Long nowTime = System.currentTimeMillis();
					Long hbInterval = nowTime - hbtTime;
					if(hbInterval >= 45000) {
						cacheService.removeChannelById(channelId);
					}
				}
			}
		}, 15, 15, TimeUnit.SECONDS);
	}
}