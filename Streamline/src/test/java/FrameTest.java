import org.example.*;
import org.example.Frames.*;
import org.example.Network.PersistentTCPClient;
import org.example.Network.TCPServer;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FrameTest {
    @Test
    public void testEncodeDecodeOneFrame() throws Exception {
        System.out.println("---- Test encode decode One Frame ----");
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

    //TODO: finish implementing tests
    @Test
    public void testFeedPartialFrame() throws Exception {
        System.out.println("---- Test encode decode Partial Frame----");
        String payload = "Hello!";

        int payloadLength = payload.getBytes(StandardCharsets.UTF_8).length;
        Frame frame = new Frame(Protocol.VERSION, Protocol.HEADER_LENGTH, FrameType.REQUEST,
                Flags.NONE, Protocol.REQUEST_ID,payload.getBytes(StandardCharsets.UTF_8) ,payloadLength);

        FrameEncoder encoder = new FrameEncoder();

        FrameDecoder decoder = new FrameDecoder();

        byte[] encoded = encoder.encodeFrame(frame, 123);

        byte[] part1 = Arrays.copyOfRange(encoded, 0, 10);
        byte[] part2 = Arrays.copyOfRange(encoded, 10, encoded.length);

        assertTrue(decoder.feed(part1).isEmpty());
        assertEquals(1, decoder.feed(part2).size());
    }

    @Test
    public void testCheckSum() throws Exception {
        System.out.println("---- Test checksum ----");
        String payload = "Hello!";
        int payloadLength = payload.getBytes(StandardCharsets.UTF_8).length;

        Frame frame = new Frame(Protocol.VERSION, Protocol.HEADER_LENGTH, FrameType.REQUEST,
                Flags.NONE, Protocol.REQUEST_ID,payload.getBytes(StandardCharsets.UTF_8) ,payloadLength);

        FrameEncoder encoder = new FrameEncoder();
        FrameDecoder decoder = new FrameDecoder();
        byte[] encoded_bytes = encoder.encodeFrame(frame,123);
        encoded_bytes[Protocol.HEADER_LENGTH] ^= 1;

        assertThrows(RuntimeException.class, ()->{decoder.feed(encoded_bytes);});
    }

    @Test
    public void testFeedMultipleFrames() throws Exception {
        System.out.println("---- Test multiple frames ----");

        String payload = "Hello!";
        int payloadLength = payload.getBytes(StandardCharsets.UTF_8).length;
        String payload2 = "This is a test...";
        int payload2Length = payload2.getBytes(StandardCharsets.UTF_8).length;

        Frame frame1 = new Frame(Protocol.VERSION, Protocol.HEADER_LENGTH, FrameType.REQUEST,
                Flags.NONE, Protocol.REQUEST_ID,payload.getBytes(StandardCharsets.UTF_8) ,payloadLength);

        Frame frame2 = new Frame(Protocol.VERSION, Protocol.HEADER_LENGTH, FrameType.REQUEST,
                Flags.NONE, Protocol.REQUEST_ID,payload2.getBytes(StandardCharsets.UTF_8) ,payload2Length);

        FrameEncoder encoder = new FrameEncoder();
        byte[] encoded_bytes1 = encoder.encodeFrame(frame1, Protocol.REQUEST_ID);
        byte[] encoded_bytes2 = encoder.encodeFrame(frame2, Protocol.REQUEST_ID);
        byte[] combined = ByteBuffer.allocate(encoded_bytes1.length + encoded_bytes2.length)
                .put(encoded_bytes1)
                .put(encoded_bytes2)
                .array();

        FrameDecoder decoder = new FrameDecoder();

        List<Frame> decoded = decoder.feed(combined);
        boolean two = decoded.size() == 2;
        assertTrue(two);
    }

    @Test
    public void testInvalidMagic() throws Exception {
        System.out.println("---- Test invalid magic ----");
        String payload = "Hello!";
        int payloadLength = payload.getBytes(StandardCharsets.UTF_8).length;

        Frame frame1 = new Frame(Protocol.VERSION, Protocol.HEADER_LENGTH, FrameType.REQUEST,
                Flags.NONE, Protocol.REQUEST_ID,payload.getBytes(StandardCharsets.UTF_8) ,payloadLength);

        FrameEncoder encoder = new FrameEncoder();
        byte[] encoded_bytes = encoder.encodeFrame(frame1, 123, 0xDEADBEED);
        FrameDecoder decoder = new FrameDecoder();

        assertThrowsExactly(RuntimeException.class,()->{
            List<Frame> decoded = decoder.feed(encoded_bytes);
        });
    }

    @Test
    public void testPayloadTooLarge() throws Exception {
        System.out.println("---- Test Large payload ----");
        int sizeInBytes = 17 * 1024 * 1024; //too large
        byte[] payload = new byte[sizeInBytes];

        Frame frame1 = new Frame(Protocol.VERSION, Protocol.HEADER_LENGTH, FrameType.REQUEST,
                Flags.NONE, Protocol.REQUEST_ID,payload ,payload.length);

        FrameEncoder encoder = new FrameEncoder();

        assertThrowsExactly(RuntimeException.class,()->{
            byte[] encoded_bytes = encoder.encodeFrame(frame1, 123);
        });
    }

    @Test
    public void testSendPacket() throws Exception {
        System.out.println("---- Test send packet ----");
        String payload = "Hello!";
        TCPServer tcpServer = new TCPServer(8052,40); //TODO: MAKE THIS A CONFIGURATION
        tcpServer.start(); //TODO: Make this in config
        PersistentTCPClient tcpClient = new PersistentTCPClient("127.0.0.1", 8052); //TODO: Make this a config

        Frame helloFrame = new Frame(Protocol.VERSION, Protocol.HEADER_LENGTH, FrameType.REQUEST,
                Flags.NONE, Protocol.REQUEST_ID,
                payload.getBytes(StandardCharsets.UTF_8) ,payload.length());
        Protocol.printFrameDetails(helloFrame);

        List<Frame> responses = tcpClient.sendAndWait(helloFrame);
        Frame extracted =  responses.get(0);
        Protocol.printFrameDetails(extracted);


        assertTrue(responses.size() == 1 && extracted.frameType() == FrameType.RESPONSE
        && extracted.requestId() == helloFrame.requestId());
    }
}
