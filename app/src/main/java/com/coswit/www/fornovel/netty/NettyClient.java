package com.coswit.www.fornovel.netty;


import android.util.Log;

import com.blankj.utilcode.util.EncodeUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;


/**
 * Netty客户端
 */
public class NettyClient {

    private static NettyClient nettyClient;
    private static NioEventLoopGroup group;


    private static Bootstrap bootstrap;
    private static ChannelFuture channelFuture;
    private static boolean isChannelFutureConnected;
    private StringBuilder sb;

    public static NettyClient getInstance() {
        if (nettyClient == null) {
            synchronized (NettyClient.class) {
                if (nettyClient == null) {
                    nettyClient = new NettyClient();
                }
            }
        }
        return nettyClient;
    }

    private NettyClient() {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
//                .remoteAddress(new InetSocketAddress("192.168.1.123",8081))
//                .handler(NettyChannelInitialize.getInstance())
                .handler(NettyClientHandler.getInstance())
        ;


        channelFuture = bootstrap.connect("192.168.1.105",8081);
        channelFuture.channel().pipeline()
//                .addLast(new StringDecoder())
                .addLast(new StringEncoder());
        //监听是否连接成功
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                isChannelFutureConnected = future.isSuccess();
                Log.d("NettyClient", "isChannelFutureConnected:" + isChannelFutureConnected);
            }
        });

    }

    public void connect() {
        try {
            channelFuture.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
    }


    public static boolean isConnected() {
        return isChannelFutureConnected;
    }

    public void sendMsgToServer(String data) {
        channelFuture.channel().writeAndFlush(data);
        channelFuture.channel().read();
    }

    public void sendMsgToServer(String msg, String type) {
        sb = new StringBuilder();
        sb.append(type);
        sb.append(" ");
        sb.append(EncodeUtils.base64Encode2String(msg.getBytes()));
        sb.append("$_");
        Log.d("NettyClient发送消息", sb.toString());
        channelFuture.channel().writeAndFlush(sb.toString());
//        channelFuture.channel().read();

    }

    public void sendMsg(String msg) {

        Log.d("NettyClient发送消息", msg);
        Channel channel = channelFuture.channel();
        Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
        channel.writeAndFlush(msg);
//        channelFuture.channel().read();
    }
}
