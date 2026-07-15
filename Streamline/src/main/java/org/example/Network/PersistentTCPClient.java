package org.example.Network;

import org.example.Frames.Frame;
import org.example.Frames.FrameDecoder;
import org.example.Frames.FrameEncoder;
import org.example.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO: Migrate to netty

public class PersistentTCPClient {
    private static final int BUFFER_SIZE = 8192;
    private final String serverHost;
    FrameEncoder encoder = new FrameEncoder();
    byte[] buffer = new byte[BUFFER_SIZE];
    FrameDecoder decoder = new FrameDecoder();
    Socket socket;
    private int port;

    public PersistentTCPClient(String host, int port){
        this.serverHost = host;
        this.port = port;
    }

    public void connect() throws IOException {
        try{
            this.socket = new Socket(serverHost, port);
        }catch(UnknownHostException e){
            e.printStackTrace();
        }

    }

    public List<Frame> sendAndWait(Frame frame) throws Exception {
        int retry = 0;
        boolean sent = false;
        int delay = 200;
        int backoff = 2; //exponential

        do{
            try{
                if(!socketConnected()) connect();

                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();

                byte[] encoded_bytes = encoder.encodeFrame(frame, frame.requestId());
                out.write(encoded_bytes);
                out.flush();

                int byteRead = 0;
                List<Frame> responses = new ArrayList<>();
                while (responses.isEmpty()) {
                    byteRead = in.read(buffer);
                    if(byteRead == -1) throw new IOException("Socket closed");
                    byte[] exactPayload = Arrays.copyOf(buffer, byteRead);
                    responses.addAll(decoder.feed(exactPayload));
                }
                sent = true;
                return responses;

            } catch (IOException ex) {
                Thread.sleep(delay);
                connect();
                retry ++;
                delay *= backoff;
                ex.printStackTrace();
            }
        } while(!sent && retry < 3);

        throw  new Exception("Connection to " + serverHost + " timed out");
    }

    private boolean socketConnected(){
        try{
            if(socket == null || !socket.isConnected() || socket.isClosed() ||
            socket.isInputShutdown() || socket.isOutputShutdown()){
                return false;}{}
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
