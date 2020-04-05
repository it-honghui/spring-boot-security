package cn.gathub.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyExpiredSessionStrategy implements SessionInformationExpiredStrategy {


    private static ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "您已经在另外一台电脑或浏览器登录，被迫下线！");

        event.getResponse().setContentType("application/json;charset=UTF-8");
        event.getResponse().getWriter().write(
                objectMapper.writeValueAsString(map)
        );

    }
}
