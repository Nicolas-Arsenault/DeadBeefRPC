package org.example;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FrameEncoder {

    public byte[] encodeFrame(Frame frame, long requestId) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buffer);

        dos.writeInt(Protocol.MAGIC_BYTE);
        dos.write(Protocol.VERSION);
        dos.write(Protocol.HEADER_LENGTH);
        dos.write(frame.frameType().code); //frame type
        dos.write(frame.flags());
        dos.writeLong(requestId);
        if(frame.payload().length > Protocol.MAX_PAYLOAD_SIZE) {throw new RuntimeException("Payload too large.");}
        dos.writeInt(frame.payloadLength()); //TODO: Replace this with the actual payload size.
        dos.writeInt(calculateChecksum(frame.payload()));
        dos.write(frame.payload());

        dos.flush();
        return buffer.toByteArray();
    }

    /// DO NOT USE THIS FUNCTION IT IS FOR TESTING ONLY
    @Deprecated(forRemoval = false)
    public byte[] encodeFrame(Frame frame, long requestId, int magicByte) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buffer);

        dos.writeInt(magicByte);
        dos.write(Protocol.VERSION);
        dos.write(Protocol.HEADER_LENGTH);
        dos.write(frame.frameType().code); //frame type
        dos.write(frame.flags());
        dos.writeLong(requestId);
        if(frame.payload().length > Protocol.MAX_PAYLOAD_SIZE) {throw new IllegalArgumentException("Payload too large.");}
        dos.writeInt(frame.payloadLength()); //TODO: Replace this with the actual payload size.
        dos.writeInt(calculateChecksum(frame.payload()));
        dos.write(frame.payload());

        dos.flush();
        return buffer.toByteArray();
    }

    private int calculateChecksum(byte[] payload) {
        return 1; //TODO: implement
    }
}
