package test.xyz.srclab.grpc.spring.boot.client;

import org.springframework.stereotype.Component;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.test.TestMarker;

import java.util.LinkedList;
import java.util.List;

@Component
public class TraceService {

    private final TestMarker testMarker = TestMarker.newTestMarker();

    public void addInterceptorTrace(String interceptorName) {
        @Nullable List<String> value = testMarker.getMark("interceptorTraces");
        if (value == null) {
            List<String> newList = new LinkedList<>();
            newList.add(interceptorName);
            testMarker.mark("interceptorTraces", newList);
        } else {
            value.add(interceptorName);
        }
    }

    public List<String> getInterceptorTraces() {
        return testMarker.getMark("interceptorTraces");
    }
}
