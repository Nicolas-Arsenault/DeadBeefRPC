package org.example.Frames;

public class Frame {
    private final byte version;
    private final byte headerLength;
    private final FrameType frameType;
    private final byte flags;
    private final long requestId;
    private final byte[] payload;
    private final int payloadLength;

    //TODO: remove the payload length from the constructor its not arbitrary.
    //TODO: Review building of the frame, probably a cleaner way to do it.
    public Frame(byte version, byte headerLength, FrameType frameType, byte flags, long requestId, byte[] payload,  int payloadLength) {
        this.version = version;
        this.headerLength = headerLength;
        this.frameType = frameType;
        this.flags = flags;
        this.requestId = requestId;
        this.payload = payload == null ? new byte[0] : payload;
        this.payloadLength = payloadLength;
    }

    public int payloadLength() {
        return payloadLength;
    }

    public byte version() {
        return version;
    }

    public byte headerLength() {
        return headerLength;
    }

    public FrameType frameType() {
        return frameType;
    }

    public byte flags() {
        return flags;
    }

    public long requestId() {
        return requestId;
    }

    public byte[] payload() {
        return payload;
    }
}
