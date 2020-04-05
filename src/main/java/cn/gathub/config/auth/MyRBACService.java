package cn.gathub.config.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component("rabcService")
public class MyRBACService {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Resource
    private MyRBACServiceMapper myRBACServiceMapper;

    /**
     * 判断某用户是否具有该request资源的访问权限
     */
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            UserDetails userDetails = ((UserDetails) principal);
            List<GrantedAuthority> authorityList =
                    AuthorityUtils.commaSeparatedStringToAuthorityList(request.getRequestURI());

//            return userDetails.getAuthorities().contains(authorityList.get(0));

            String username = ((UserDetails) principal).getUsername();
            List<String> urls = myRBACServiceMapper.findUrlsByUserName(username);
            return urls.stream().anyMatch(
                    url -> antPathMatcher.match(url, request.getRequestURI())
            );
        }
        return false;
    }
}
