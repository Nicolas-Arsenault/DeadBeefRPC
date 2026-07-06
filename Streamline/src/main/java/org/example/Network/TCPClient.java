package org.example.Network;

import org.example.Frames.Frame;
import org.example.Frames.FrameDecoder;
import org.example.Frames.FrameEncoder;
import org.example.Protocol;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TCPClient {
    private static final int BUFFER_SIZE = 8192;
    private static final String SERVER_HOST = "localhost";
    FrameEncoder encoder = new FrameEncoder();
    byte[] buffer = new byte[BUFFER_SIZE];
    FrameDecoder decoder = new FrameDecoder();

    public List<Frame> sendFrame(Frame frame) {
        try (Socket socket = new Socket(SERVER_HOST, Protocol.SERVER_PORT);
             InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            byte[] encoded_bytes = encoder.encodeFrame(frame,frame.requestId());
            out.write(encoded_bytes);
            out.flush();

            int byteRead = 0;
            List<Frame> responses = new ArrayList<>();
            while(responses.isEmpty()) {
                byteRead = in.read(buffer);
                byte[] exactPayload = Arrays.copyOf(buffer, byteRead);
                responses.addAll(decoder.feed(exactPayload));
            }

            return responses;

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
