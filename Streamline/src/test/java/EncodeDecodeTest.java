import org.example.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EncodeDecodeTest {
    @Test
    public void testEncodeDecodeOneFrame() throws Exception {
        long requestId = 123;
        String payload= "Hello!";
        Frame expected = new Frame(Protocol.VERSION, Protocol.HEADER_LENGTH, FrameType.REQUEST, Flags.NONE,
                requestId, payload.getBytes(StandardCharsets.UTF_8), payload.length());
        //test encoding
        FrameEncoder encoder = new FrameEncoder();
        byte[] encoded = encoder.encodeFrame(expected, requestId);
        System.out.println(encoded.length);

        //decode it
        FrameDecoder decoder = new FrameDecoder();
        List<Frame> decoded = decoder.feed(encoded);
        Frame actual = decoded.getFirst();
        Protocol.printFrameDetails(actual);
        Protocol.printFrameDetails(expected);
        boolean equal = Protocol.compareFrames(expected, actual);
        assertTrue(equal);
    }

    @Test
    public void testFeedPartialFrame() throws Exception {
        long requestId = 123;
        String payload= "Hello!";
    }

    @Test
    public void testFeedMultipleFrames() throws Exception {
        long requestId = 123;
        String payload= "Hello!";
    }

    @Test
    public void testInvalidMagic(){

    }

    @Test
    public void testPayloadTooLarge() throws Exception {}
}
