package org.example.Frames;

import org.example.Protocol;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

//TODO: migrate to netty

public class FrameDecoder {
    ByteBuffer byteBuffer = ByteBuffer.allocate(8192); //8KB

    public List<Frame> feed(byte[] data){
        byteBuffer.put(data);
        byteBuffer.flip(); //read mode
        List<Frame> frames = new ArrayList<>();
        //verify if a frame header is present in the buffer
        while(true){


            if(byteBuffer.remaining() < 24){
                break;
            }
            byteBuffer.mark();

            int magic = byteBuffer.getInt();
            byte version =  byteBuffer.get();
            byte headerLen = byteBuffer.get();
            byte type = byteBuffer.get();
            byte flag = byteBuffer.get();
            long requestId = byteBuffer.getLong();
            int payloadSize = byteBuffer.getInt();
            int checkSum = byteBuffer.getInt();

            if(magic != Protocol.MAGIC_BYTE) throw new RuntimeException("magic byte mismatch");
            if(version != Protocol.VERSION) throw new RuntimeException("version mismatch");
            if(headerLen != Protocol.HEADER_LENGTH) throw new RuntimeException("headerLen mismatch");
            if(payloadSize < 0 || payloadSize > Protocol.MAX_PAYLOAD_SIZE) throw new RuntimeException("payloadSize mismatch");

            //verification if the whole frame has arrived
            if(byteBuffer.remaining() < payloadSize){
                byteBuffer.reset();
                break;
            }

            //extract payload
            byte[] payload = new byte[payloadSize];
            byteBuffer.get(payload);

            int payloadCheckSum = Protocol.calculateChecksum(payload);
            if(payloadCheckSum != checkSum){throw new RuntimeException("checkSum mismatch");}

            frames.add(new Frame(version,headerLen,FrameType.fromByte(type),flag,requestId, payload, payloadSize));
        }
        byteBuffer.compact();
        return frames;
    }
}
