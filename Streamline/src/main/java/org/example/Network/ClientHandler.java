package org.example.Network;

import org.example.Frames.*;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ClientHandler {
    private static final int BUFFER_SIZE = 8192;
    byte[] buffer = new byte[BUFFER_SIZE];
    FrameDecoder frameDecoder = new FrameDecoder();
    FrameEncoder frameEncoder = new FrameEncoder();

    public void handleClient(Socket clientSocket) throws IOException {
        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {

            int byteread = 0;
            while((byteread = inputStream.read(buffer)) != -1){
                byte[] exactPayload = Arrays.copyOf(buffer, byteread);
                List<Frame> frames = frameDecoder.feed(exactPayload);

                //TODO: Change this harcoded temp response
                for(Frame frame : frames){
                    Frame resp = craftResponseFrame(frame);
                    byte[] encoded_bytes = frameEncoder.encodeFrame(resp, frame.requestId());
                    outputStream.write(encoded_bytes);
                    outputStream.flush();
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    private Frame craftResponseFrame(Frame frame){
        return new Frame(frame.version(), frame.headerLength(), FrameType.RESPONSE,
                Flags.NONE, frame.requestId(), frame.payload(), frame.payloadLength());
    }
}
