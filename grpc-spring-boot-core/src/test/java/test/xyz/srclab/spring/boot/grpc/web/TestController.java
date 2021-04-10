package test.xyz.srclab.spring.boot.grpc.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.srclab.spring.boot.proto.MessageData;
import xyz.srclab.spring.boot.proto.ResponseMessage;

@RequestMapping("test")
@RestController
public class TestController {

    @RequestMapping("testProtobuf")
    public ResponseMessage testProtobuf(String code, String state, String message) {
        return ResponseMessage.newBuilder()
                .setCode(code)
                .setState(state)
                .setData(MessageData.newBuilder().setMessage(message).build())
                .build();
    }
}
