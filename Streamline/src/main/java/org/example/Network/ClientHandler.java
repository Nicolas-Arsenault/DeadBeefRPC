package org.example.Network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.Frames.Flags;
import org.example.Frames.Frame;
import org.example.Frames.FrameEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private volatile ChannelHandlerContext ctx = null;
    FrameEncoder encoder = new FrameEncoder();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = null;
        super.channelInactive(ctx);
    }

    public List<Frame> sendFrame(Frame frame) throws Exception {
        int retry = 0;
        boolean sent = false;
        int delay = 200;
        int backoff = 2; //exponential

        do{
            try{
                if(ctx != null && ctx.channel().isActive()){
                    byte[] encoded_bytes = encoder.encodeFrame(frame);
                    ctx.writeAndFlush(encoded_bytes);
                }

                //TODO: Check to return a promise here if there is a response waiting

                return responses;

            } catch (IOException ex) {
                Thread.sleep(delay);
                retry ++;
                delay *= backoff;
                ex.printStackTrace();
            }
        } while(!sent && retry < 3);

        throw  new Exception("Connection timed out");
    }
}
