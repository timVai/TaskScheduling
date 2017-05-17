package com.jd.uwp.messagecenter.factory;

import com.jd.uwp.messagecenter.service.FlashPolicyHandler;
import com.jd.uwp.messagecenter.service.WebSocketMessageHandler;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * 启动初始化
 */
public class WebSocketServiceFactory implements InitializingBean, DisposableBean {
    private static Logger logger = LoggerFactory.getLogger(WebSocketServiceFactory.class);
    private int port;
    private WebSocketMessageHandler messageHandler;
    private ServerBootstrap bootstrap;
    private Channel channel;

    public void setPort(int port) {
        this.port = port;
    }

    public void setMessageHandler(WebSocketMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public WebSocketServiceFactory(){
        super();
    }

    @Override
    public void destroy() throws Exception {
        logger.info("WebSocket Server stopping...");
        ChannelFuture future = channel.close();
        future.awaitUninterruptibly();
        if(future.isSuccess()) {
            logger.info("WebSocket Server stopped.");
        }
        bootstrap.releaseExternalResources();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start(port);
    }

    private void start(int port) {
        try {
            logger.info("WebSocket Server starting...");
            bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors
                    .newCachedThreadPool(), Executors.newCachedThreadPool()));
            bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
                public ChannelPipeline getPipeline() throws Exception {
                    ChannelPipeline pipeline = pipeline();
                    pipeline.addLast("flashPolicy", new FlashPolicyHandler());
                    pipeline.addLast("decoder", new HttpRequestDecoder());
                    pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
                    pipeline.addLast("encoder", new HttpResponseEncoder());
                    pipeline.addLast("handler", messageHandler);
                    return pipeline;
                }
            });
            bootstrap.setOption("child.tcpNoDelay", true);
            bootstrap.setOption("child.keepAlive", true);
            channel = bootstrap.bind(new InetSocketAddress(port));
            logger.info("WebSocket Server started, waiting for connections on port [{}] ...",
                    port);
        } catch (Exception e) {
            logger.error("WebSocket Server startup occurred error.", e);
        }
    }

    public void close() {
        if(this.channel != null && this.channel.isBound()) {
            ChannelFuture future = channel.close();
            future.awaitUninterruptibly();
            bootstrap.releaseExternalResources();
            if (future.isSuccess()) {
                logger.info("WebSocket server stopped.");
            }
        } else {
            logger.info("WebSocket Server has stopped.");
        }
    }

    public void open() {
        if(this.channel != null) {
            if(!this.channel.isBound()) {
                this.start(port);
            } else {
                logger.info("WebSocket Server has started.");
            }
        } else {
            logger.error("WebSocket Server can not bootstrap.");
        }
    }
}