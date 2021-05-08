package sample.java.xyz.srclab.grpc.spring.boot.client;

import io.grpc.Metadata;

public class TestConstants {

    public static final String CONTEXT_KEY_1 = "testKey1";
    public static final String CONTEXT_VALUE_1 = "testValue1";

    public static final String CONTEXT_KEY_2 = "testKey2";
    public static final String CONTEXT_VALUE_2 = "testValue2";

    public static final Metadata.AsciiMarshaller<String> HEADER_EXT_KEY = new HeaderExtKey();

    private static class HeaderExtKey implements Metadata.AsciiMarshaller<String> {

        @Override
        public String toAsciiString(String value) {
            return value;
        }

        @Override
        public String parseAsciiString(String serialized) {
            return serialized;
        }
    }
}
