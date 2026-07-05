package org.example;

import java.util.Arrays;

//class with the constants of the protocol
public class Protocol {
    public static final int MAGIC_BYTE = 0xDEADBEEF;
    public static final byte VERSION = 1;
    public static final byte HEADER_LENGTH = 24;
    public static final int MAX_PAYLOAD_SIZE =16 * 1024 * 1024;
    public static long REQUEST_ID = 123; //TODO: Make this dynamic

    public static boolean compareFrames(Frame frame1, Frame frame2) {
        return frame1.frameType() == frame2.frameType() && Arrays.equals(frame1.payload(), frame2.payload()) &&
                frame1.flags() == frame2.flags() && frame1.version() == frame2.version() && frame1.requestId() == frame2.requestId() &&
                frame1.payloadLength() == frame2.payloadLength();
    }

    public static void printFrameDetails(Frame f) {
        System.out.println("Frame Type: " + f.frameType());
        System.out.println("Frame Version: " + f.version());
        System.out.println("Frame RequestId: " + f.requestId());
        System.out.println("Frame Payload Length: " + f.payloadLength());
        System.out.println("Frame Payload: " + f.payload());
        System.out.println("Frame Flags: " + f.flags());

    }
}
