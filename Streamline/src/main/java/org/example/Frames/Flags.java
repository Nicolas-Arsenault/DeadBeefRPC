package org.example.Frames;

//8 bits for that
public final class Flags {
    public static final byte NONE = 0;
    public static final byte COMPRESSED = 1;
    public static final byte REQUIRES_RESPONSE = 1 << 1;
    public static final byte ERROR = 1 << 2;

    private Flags(){}
}
