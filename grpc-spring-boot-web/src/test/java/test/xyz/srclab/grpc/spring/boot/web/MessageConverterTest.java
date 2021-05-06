package test.xyz.srclab.grpc.spring.boot.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.protobuf.ProtobufJsons;
import xyz.srclab.spring.boot.proto.MessageData;
import xyz.srclab.spring.boot.proto.ResponseMessage;

import javax.annotation.Resource;

@SpringBootTest(
    classes = Starter.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class MessageConverterTest extends AbstractTestNGSpringContextTests {

    private static final Logger logger = LoggerFactory.getLogger(MessageConverterTest.class);

    @LocalServerPort
    private int port;

    @Resource
    private TestRestTemplate restTemplate;

    @Test
    public void testMessageConverter() throws Exception {
        String result = restTemplate.getForObject(
            "http://localhost:" + port + "/test/testProtobuf?code=123&state=456&message=666",
            String.class
        );
        logger.info("/test/testProtobuf?code=123&state=456&message=666: " + result);
        Assert.assertEquals(
            result,
            ProtobufJsons.PROTOBUF_JSON_SERIALIZER.toJsonString(
                ResponseMessage.newBuilder()
                    .setCode("123")
                    .setState("456")
                    .setData(MessageData.newBuilder().setMessage("666").build())
                    .build()
            )
        );
    }
}
