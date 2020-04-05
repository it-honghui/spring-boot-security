package cn.gathub.config;

import cn.gathub.config.auth.*;
import cn.gathub.config.auth.imagecode.CaptchaCodeFilter;
import cn.gathub.config.auth.smscode.SmsCodeSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // 开发方法级别权限控制
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // 自定义登录成功处理Handler
    @Resource
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    // 自定义登录失败处理Handler
    @Resource
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    // 退出登录成功的Handler
    @Resource
    MyLogoutSuccessHandler myLogoutSuccessHandler;

    // 动态加载用户和权限数据使用
    @Resource
    MyUserDetailsService myUserDetailsService;

    // 记住我保存数据库使用
    @Resource
    private DataSource datasource;

    // 验证码过滤器
    @Resource
    private CaptchaCodeFilter captchaCodeFilter;

    // 手机验证码登录
    @Resource
    private SmsCodeSecurityConfig smsCodeSecurityConfig;

//    开启httpbasic认证 最简单的登陆认证模式 在yml配置用户名和密码
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.httpBasic()
//                .and()
//                .authorizeRequests()
//                .anyRequest()
//                .authenticated();//所有请求都需要登录认证才能访问
//    }

//    formLogin + defaultSuccessUrl + failureUrl 方式登录
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable() // 禁用跨站csrf攻击防御，后面的章节会专门讲解
//                .formLogin()
//                .loginPage("/login.html") // 用户未登录时，访问任何资源都转跳到该路径，即登录页面
//                .loginProcessingUrl("/login") // 登录表单form中action的地址，也就是处理认证请求的路径
//                .usernameParameter("username") // 登录表单form中用户名输入框input的name名，不修改的话默认是username
//                .passwordParameter("password") // form中密码输入框input的密码，不修改的话默认是password
//                .defaultSuccessUrl("/index") // 登录认证成功后默认转跳的路径
//                .failureUrl("/login.html") // 登录失败后默认转跳的路径
//                .and()
//                .authorizeRequests()
//                .antMatchers("/login.html", "/login").permitAll() // 不需要通过登录验证就可以被访问的资源路径
//                .antMatchers("/biz1", "/biz2") // 需要对外暴露的资源路径
//                .hasAnyAuthority("ROLE_user", "ROLE_admin") // user角色和admin角色都可以访问
//                .antMatchers("/syslog", "/sysuser")
//                .hasAnyRole("admin") // admin角色可以访问
//                //.antMatchers("/syslog").hasAuthority("sys:log")
//                //.antMatchers("/sysuser").hasAuthority("sys:user")
//                .anyRequest().authenticated();
//    }

//    formLogin + successHandler + failureHandler 登录
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable() // 禁用跨站csrf攻击防御，后面的章节会专门讲解
//                .formLogin()
//                .loginPage("/login.html") // 用户未登录时，访问任何资源都转跳到该路径，即登录页面
//                .loginProcessingUrl("/login") // 登录表单form中action的地址，也就是处理认证请求的路径
//                .usernameParameter("uname") // 登录表单form中用户名输入框input的用户名-uname，不修改的话默认是username
//                .passwordParameter("pword") // form中密码输入框input的密码-pword，不修改的话默认是password
////                .defaultSuccessUrl("/index") // 登录认证成功后默认转跳的路径
////                .failureUrl("/login.html") // 登录失败后默认转跳的路径
//                .successHandler(myAuthenticationSuccessHandler) // 登录认证成功后默认转跳的Handler，和上面的defaultSuccessUrl冲突，保留一个
//                .failureHandler(myAuthenticationFailureHandler) // 登录失败后默认转跳的Handler，和上面的failureUrl冲突，保留一个
//                .and()
//                .authorizeRequests()
//                .antMatchers("/login.html", "/login").permitAll() // 不需要通过登录验证就可以被访问的资源路径
//                .antMatchers("/biz1", "/biz2") // 需要对外暴露的资源路径
//                .hasAnyAuthority("ROLE_user", "ROLE_admin") // user角色和admin角色都可以访问
//                .antMatchers("/syslog", "/sysuser")
//                .hasAnyRole("admin") // admin角色可以访问
//                //.antMatchers("/syslog").hasAuthority("sys:log")
//                //.antMatchers("/sysuser").hasAuthority("sys:user")
//                .anyRequest().authenticated();
//    }

//    静态加载用户权限资源
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable() // 禁用跨站csrf攻击防御，后面的章节会专门讲解
//                .formLogin()
//                .loginPage("/login.html") // 用户未登录时，访问任何资源都转跳到该路径，即登录页面
//                .loginProcessingUrl("/login") // 登录表单form中action的地址，也就是处理认证请求的路径
//                .usernameParameter("uname") // 登录表单form中用户名输入框input的用户名-uname，不修改的话默认是username
//                .passwordParameter("pword") // form中密码输入框input的密码-pword，不修改的话默认是password
//
////                .defaultSuccessUrl("/index") // 登录认证成功后默认转跳的路径
////                .failureUrl("/login.html") // 登录失败后默认转跳的路径
//                .successHandler(myAuthenticationSuccessHandler) // 登录认证成功后默认转跳的Handler，和上面的defaultSuccessUrl冲突，保留一个
//                .failureHandler(myAuthenticationFailureHandler) // 登录失败后默认转跳的Handler，和上面的failureUrl冲突，保留一个
//
//                .and()
//                .authorizeRequests()
//                .antMatchers("/login.html", "/login").permitAll() // 不需要通过登录验证就可以被访问的资源路径
//                .antMatchers("/biz1", "/biz2") // 需要对外暴露的资源路径
//                .hasAnyAuthority("ROLE_common", "ROLE_admin") // common角色和admin角色都可以访问
////                .antMatchers("/syslog", "/sysuser")
////                .hasAnyRole("admin") // admin角色可以访问
//                .antMatchers("/syslog").hasAuthority("/sys_log") // 同数据库中权限字段一致
//                .antMatchers("/sysuser").hasAuthority("/sys_user") // 同数据库中权限字段一致
//                .anyRequest().authenticated()
//
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                .invalidSessionUrl("/login.html") // 非法超时session跳转页面
//                .maximumSessions(1) // 表示同一个用户最大的登录数量
//                .maxSessionsPreventsLogin(false) // 提供两种session保护策略：true表示已经登录就不予许再次登录，false表示允许再次登录但是之前的登录会下线
//                .expiredSessionStrategy(new MyExpiredSessionStrategy()); // 表示自定义一个session被下线(超时)之后的处理策略;
//    }

    //    动态加载资源鉴权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(captchaCodeFilter, UsernamePasswordAuthenticationFilter.class) // 验证码使用

                .logout() // 退出登录
                .logoutUrl("/signout") // 配置改变退出请求的默认路径
//                .logoutSuccessUrl("/aftersignout.html") // 显式指定退出之后的跳转页面，不配置就默认跳转登录页
                .deleteCookies("JSESSIONID") // s删除指定的cookie，参数为cookie的名称
                .logoutSuccessHandler(myLogoutSuccessHandler) // 退出登录成功的Handler

                .and()
                .rememberMe() // 记住密码
                .rememberMeParameter("remember-me-new") // 参数
                .rememberMeCookieName("remember-me-cookie") // cookie的名称
                .tokenValiditySeconds(2 * 24 * 60 * 60) // 有效期
                .tokenRepository(persistentTokenRepository()) // 记住我保存数据库使用

                .and()
                .csrf().disable() // 禁用跨站csrf攻击防御，后面的章节会专门讲解

                .formLogin()
                .loginPage("/login.html") // 用户未登录时，访问任何资源都转跳到该路径，即登录页面
                .loginProcessingUrl("/login") // 登录表单form中action的地址，也就是处理认证请求的路径
                .usernameParameter("uname") // 登录表单form中用户名输入框input的用户名-uname，不修改的话默认是username
                .passwordParameter("pword") // form中密码输入框input的密码-pword，不修改的话默认是password

//                .defaultSuccessUrl("/index") // 登录认证成功后默认转跳的路径
//                .failureUrl("/login.html") // 登录失败后默认转跳的路径
                .successHandler(myAuthenticationSuccessHandler) // 登录认证成功后默认转跳的Handler，和上面的defaultSuccessUrl冲突，保留一个
                .failureHandler(myAuthenticationFailureHandler) // 登录失败后默认转跳的Handler，和上面的failureUrl冲突，保留一个

                .and()
                .apply(smsCodeSecurityConfig) // 手机验证码登录

                .and()
                .authorizeRequests()
                .antMatchers("/login.html", "/login", "/aftersignout.html", "/kaptcha", "/smscode", "/smslogin").permitAll() // 不需要通过登录验证就可以被访问的资源路径
                .antMatchers("/index").authenticated()
                .anyRequest().access("@rabcService.hasPermission(request, authentication)") // 动态加载资源鉴权，anyRequest对所有请求生效

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/login.html") // 非法超时session跳转页面
                .maximumSessions(1) // 表示同一个用户最大的登录数量
                .maxSessionsPreventsLogin(false) // 提供两种session保护策略：true表示已经登录就不予许再次登录，false表示允许再次登录但是之前的登录会下线
                .expiredSessionStrategy(new MyExpiredSessionStrategy()); // 表示自定义一个session被下线(超时)之后的处理策略;
    }

//    静态加载用户权限数据
//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("user")
//                .password(passwordEncoder().encode("123456"))
//                .roles("user")
//                .and()
//                .withUser("admin")
//                .password(passwordEncoder().encode("123456"))
//                //.authorities("sys:log","sys:user")
//                .roles("admin")
//                .and()
//                .passwordEncoder(passwordEncoder());//配置BCrypt加密
//    }

    // 动态加载用户权限数据
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        //将项目中静态资源路径开放出来
        web.ignoring().antMatchers("/css/**", "/fonts/**", "/img/**", "/js/**");
    }

    // 记住我保存数据库使用
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(datasource);
        return tokenRepository;
    }
}

