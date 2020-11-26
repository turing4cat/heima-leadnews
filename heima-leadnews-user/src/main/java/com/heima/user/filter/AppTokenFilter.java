package com.heima.user.filter;

import com.heima.model.user.pojo.ApUser;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import com.heima.utils.threadlocal.WmThreadLocalUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(1)
@WebFilter(filterName = "appTokenFilter",urlPatterns = "/*")
@Log4j2
public class AppTokenFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //请求发来的时候把用户的数据存储到线程
        //1.获取请求和响应对象
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取请求头信息
        String userId = request.getHeader("userId");
        if (userId!=null) {
            ApUser apUser = new ApUser();
            apUser.setId(Integer.valueOf(userId));
            AppThreadLocalUtils.setUser(apUser);
        }
        //放行
        filterChain.doFilter(request,response);
    }
}
