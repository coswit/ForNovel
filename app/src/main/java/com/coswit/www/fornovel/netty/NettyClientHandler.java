package com.bdzhd.www.zhcxdriver.netty;

import android.text.TextUtils;

import com.bdzhd.www.zhcxdriver.Bean.NettyOrderStatusBean;
import com.bdzhd.www.zhcxdriver.Bean.NettyReceivedOrderBean;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;


public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    public static NettyClientHandler nettyClientHandler;

    public Flowable<NettyReceivedOrderBean> mReceiveOrderFlowable;
    public Flowable<NettyOrderStatusBean> mOrderStatusFlowable;
    //发射器
    public Emitter mReceiveOrderEmitter;
    public Emitter mOrderStatusEmitter;


    public static NettyClientHandler getInstance() {
        if (nettyClientHandler == null) {
            synchronized (NettyClientHandler.class) {
                if (nettyClientHandler == null) {
                    nettyClientHandler = new NettyClientHandler();
                }
            }
        }
        return nettyClientHandler;
    }

    private NettyClientHandler() {
        mReceiveOrderFlowable = Flowable.create(new FlowableOnSubscribe<NettyReceivedOrderBean>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<NettyReceivedOrderBean> e) throws Exception {
                mReceiveOrderEmitter = e;
            }
        }, BackpressureStrategy.DROP).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        mOrderStatusFlowable = Flowable.create(new FlowableOnSubscribe<NettyOrderStatusBean>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<NettyOrderStatusBean> e) throws Exception {
                mOrderStatusEmitter = e;

            }
        }, BackpressureStrategy.DROP).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

        byte[] bytes = new byte[msg.readableBytes()];
        ByteBuf byteBuf = msg.readBytes(bytes);
        String message = new String(bytes, "UTF-8");
        Logger.d("接收netty消息：" + message);
        //处理接收到的消息
        if (message != null) {
            String type = message.substring(0, 7);
            //抢单
            if (TextUtils.equals(type, "QRO 100")) {
                String substring = message.substring(8, message.length() - 2);
                String endKey = message.substring(message.length() - 2, message.length());
                //结尾符判断
                if (TextUtils.equals(endKey, "$_")) {
                    Gson gson = new Gson();
                    NettyReceivedOrderBean receivedOrder = gson.fromJson(substring, NettyReceivedOrderBean.class);
                    if (receivedOrder.getData() != null) {
                        mReceiveOrderEmitter.onNext(receivedOrder);
                    }
                }
            }
            //监听订单状态
            if (TextUtils.equals(type, "QRS 100")) {
                String substring = message.substring(8, message.length() - 2);
                String endKey = message.substring(message.length() - 2, message.length());
                //结尾符判断
                if (TextUtils.equals(endKey, "$_")) {
                    Gson gson = new Gson();
                    NettyOrderStatusBean bean = gson.fromJson(substring, NettyOrderStatusBean.class);
                    if (bean.getData() != null) {
                        mOrderStatusEmitter.onNext(bean);
                    }
                }
            }


        }

    }


    //channel绑定就绪
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        getInstance();
    }

    //断开
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }


}
