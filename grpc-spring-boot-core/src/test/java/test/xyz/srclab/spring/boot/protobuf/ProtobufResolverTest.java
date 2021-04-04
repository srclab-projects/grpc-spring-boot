package test.xyz.srclab.spring.boot.protobuf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.bean.BeanResolver;
import xyz.srclab.common.bean.BeanType;
import xyz.srclab.common.bean.Beans;
import xyz.srclab.common.serialize.json.JsonSerials;
import xyz.srclab.spring.boot.proto.MessageData;
import xyz.srclab.spring.boot.proto.RequestMessage;
import xyz.srclab.spring.boot.protobuf.ProtobufBeans;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProtobufResolverTest {

    private static final Logger logger = LoggerFactory.getLogger(ProtobufResolverTest.class);

    @Test
    public void testProtobufResolver() {
        MessageData messageData = MessageData.newBuilder()
                .setType(MessageData.Type.TYPE_0)
                .setMessage("666")
                .addAllNumber(Arrays.asList("7", "8", "9"))
                .putEntry("m1", "mm1")
                .putEntry("m2", "mm2")
                .build();
        RequestMessage requestMessage = RequestMessage.newBuilder()
                .setId("123")
                .setData(messageData)
                .build();

        BeanResolver beanResolver = ProtobufBeans.PROTOBUF_BEAN_RESOLVER;
        BeanType dataType = beanResolver.resolve(messageData.getClass());
        Assert.assertEquals(
                dataType.properties().keySet().toArray(),
                new Object[]{"type", "message", "numberList", "entryMap"}
        );
        BeanType requestType = beanResolver.resolve(requestMessage.getClass());
        Assert.assertEquals(
                requestType.properties().keySet().toArray(),
                new Object[]{"id", "data"}
        );

        JavaMessageData javaMessageData = new JavaMessageData();
        Beans.copyProperties(messageData, javaMessageData, ProtobufBeans.PROTOBUF_BEAN_COPY_OPTIONS);
        logger.info("javaMessageData: {}", JsonSerials.toJsonString(javaMessageData));
        Assert.assertEquals(javaMessageData.getType(), messageData.getType());
        Assert.assertEquals(javaMessageData.getMessage(), messageData.getMessage());
        Assert.assertEquals(javaMessageData.getNumberList(), messageData.getNumberList());
        Assert.assertEquals(javaMessageData.getEntryMap(), messageData.getEntryMap());

        JavaRequestMessage javaRequestMessage = new JavaRequestMessage();
        Beans.copyProperties(requestMessage, javaRequestMessage, ProtobufBeans.PROTOBUF_BEAN_COPY_OPTIONS);
        logger.info("javaRequestMessage: {}", JsonSerials.toJsonString(javaRequestMessage));
        Assert.assertEquals(javaRequestMessage.getId(), requestMessage.getId());
        Assert.assertEquals(javaRequestMessage.getData().getType(), requestMessage.getData().getType());
        Assert.assertEquals(javaRequestMessage.getData().getMessage(), requestMessage.getData().getMessage());
        Assert.assertEquals(javaRequestMessage.getData().getNumberList(), requestMessage.getData().getNumberList());
        Assert.assertEquals(javaRequestMessage.getData().getEntryMap(), requestMessage.getData().getEntryMap());

        MessageData.Builder messageDataBuilder = MessageData.newBuilder();
    }

    public static class JavaRequestMessage {

        private String id;
        private JavaMessageData data;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public JavaMessageData getData() {
            return data;
        }

        public void setData(JavaMessageData data) {
            this.data = data;
        }
    }

    public static class JavaMessageData {

        private MessageData.Type type;
        private String message;
        private List<String> numberList;
        private Map<String, String> entryMap;

        public MessageData.Type getType() {
            return type;
        }

        public void setType(MessageData.Type type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<String> getNumberList() {
            return numberList;
        }

        public void setNumberList(List<String> numberList) {
            this.numberList = numberList;
        }

        public Map<String, String> getEntryMap() {
            return entryMap;
        }

        public void setEntryMap(Map<String, String> entryMap) {
            this.entryMap = entryMap;
        }
    }
}
