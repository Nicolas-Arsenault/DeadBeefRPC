package org.example.Network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.example.Frames.*;
import org.example.Service.Executor;

import java.io.*;
import java.util.List;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final int BUFFER_SIZE = 8192;
    byte[] buffer = new byte[BUFFER_SIZE];
    FrameDecoder frameDecoder = new FrameDecoder();
    FrameEncoder frameEncoder = new FrameEncoder();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        ByteBuf in =  (ByteBuf) msg;

        try{
            while(in.isReadable()){
                byte[] data = new byte[in.readableBytes()];
                in.writeBytes(data);
                in.clear();
                List<Frame> frames = frameDecoder.feed(data);

                if(!frames.isEmpty()){
                    Executor.handleFrame(frames);
                }
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    private Frame craftResponseFrame(Frame frame){
        return new Frame(frame.version(), frame.headerLength(), FrameType.RESPONSE,
                Flags.NONE, frame.requestId(), frame.payload(), frame.payloadLength());
    }
}
