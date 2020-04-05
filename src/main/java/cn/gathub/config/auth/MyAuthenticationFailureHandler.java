package cn.gathub.config.auth;

import cn.gathub.config.exception.AjaxResponse;
import cn.gathub.config.exception.CustomException;
import cn.gathub.config.exception.CustomExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.moki.ratelimitj.core.limiter.request.RequestLimitRule;
import es.moki.ratelimitj.core.limiter.request.RequestRateLimiter;
import es.moki.ratelimitj.inmemory.request.InMemorySlidingWindowRequestRateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${spring.security.loginType}")
    private String loginType;

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    MyUserDetailsService myUserDetailsService;

    @Resource
    MyUserDetailsServiceMapper myUserDetailsServiceMapper;

    // 规则定义：1小时之内5次机会，就触发限流行为，解锁的条件修改数据库enabled字段为1；时间达到设置的1小时，或者重启服务；
    Set<RequestLimitRule> rules =
            Collections.singleton(RequestLimitRule.of(1 * 60, TimeUnit.MINUTES, 5));
    RequestRateLimiter limiter = new InMemorySlidingWindowRequestRateLimiter(rules);

    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        // 从request或request.getSession中获取登录用户名
        String username = request.getParameter("uname");
        // 默认提示信息
        String errorMsg = "用户名或者密码输入错误!";

        if (exception instanceof LockedException) {
            errorMsg = "您已经多次登陆失败，账户已被锁定，请稍后再试！";
        } else {
            // 计数器加1，并判断该用户是否已经到了触发了锁定规则
            boolean reachLimit = limiter.overLimitWhenIncremented(username);
            // 如果触发了锁定规则，通过UserDetails告知Spring Security锁定账户
            if (reachLimit) {
                MyUserDetails user = (MyUserDetails) myUserDetailsService.loadUserByUsername(username);
                user.setAccountNonLocked(false);
                myUserDetailsServiceMapper.updateEnabledByUsername(user);
            }
        }

        if (exception instanceof SessionAuthenticationException) {
            errorMsg = exception.getMessage();
        }

        if (loginType.equalsIgnoreCase("JSON")) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(
                    AjaxResponse.error(new CustomException(
                            CustomExceptionType.USER_INPUT_ERROR,
                            errorMsg))
            ));
        } else {
            //跳转到登陆页面
            super.onAuthenticationFailure(request, response, exception);
        }

    }
}
