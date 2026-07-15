package org.example.Frames;

public enum FrameType {
    REQUEST((byte)1),
    RESPONSE((byte)2),
    ERROR((byte)3),
    HEARTBEAT((byte)4),;
    public final byte code;

    FrameType(byte code) {
        this.code = code;
    }

    public static FrameType fromByte(byte b){
        for(FrameType frameType : FrameType.values()){
            if(frameType.code == b)return frameType;
        }
        throw new IllegalArgumentException("Unknown Frame Type: " + b);
    }

}
