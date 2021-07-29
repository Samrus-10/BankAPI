package sam.rus.bankapi.util;

import java.util.Base64;

public class Base64Coder {
    private static Base64.Encoder encoder;
    private static Base64.Decoder decoder;

    static {
        encoder = Base64.getEncoder();
        decoder = Base64.getDecoder();
    }

    public static String toEncoder(String string) {
        return new String(
                encoder.encode(string.getBytes())
        );
    }

    public static String toDecoder(String string) {
        return new String(
                decoder.decode(string)
        );
    }
}
