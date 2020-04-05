package cn.gathub.controller;

import cn.gathub.config.auth.MyUserDetails;
import cn.gathub.config.auth.MyUserDetailsServiceMapper;
import cn.gathub.config.auth.smscode.SmsCode;
import cn.gathub.config.exception.AjaxResponse;
import cn.gathub.config.exception.CustomException;
import cn.gathub.config.exception.CustomExceptionType;
import cn.gathub.util.MyContants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
public class SmsController {

    @Resource
    MyUserDetailsServiceMapper myUserDetailsServiceMapper;

    @RequestMapping(value = "/smscode", method = RequestMethod.GET)
    public AjaxResponse sms(@RequestParam String mobile, HttpSession session) {

        MyUserDetails myUserDetails = myUserDetailsServiceMapper.findByUserName(mobile);
        if (myUserDetails == null) {
            return AjaxResponse.error(
                    new CustomException(CustomExceptionType.USER_INPUT_ERROR,
                            "您输入的手机号未曾注册")
            );
        }

        SmsCode smsCode = new SmsCode(
                RandomStringUtils.randomNumeric(4), 60, mobile
        );

        //TODO 调用短信服务提供商的接口发送短信
        log.info(smsCode.getCode() + "+>" + mobile);

        session.setAttribute(MyContants.SMS_SESSION_KEY, smsCode);

        return AjaxResponse.success("短信验证码已经发送");
    }

}
