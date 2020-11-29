package com.heima.article.filter;

import com.heima.model.user.pojo.ApUser;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebFilter(filterName = "appTokenFilter",urlPatterns = "/*")
@Order(1)
public class AppTokenFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        //获取头文件中的用户id
        String userId = request.getHeader("userId");
        if (userId!=null && Integer.valueOf(userId).intValue()!=0){
//            创建user对象存储到线程对想当中
            ApUser apUser = new ApUser();
            apUser.setId(Integer.valueOf(userId));
            AppThreadLocalUtils.setUser(apUser);
        }
        //放行
        filterChain.doFilter(request,response);
    }
}
