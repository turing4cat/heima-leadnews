package com.heima.wemedia.gateway.filter;

import com.heima.wemedia.gateway.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Component
@Log4j2
public class AuthorizeFilter implements GlobalFilter , Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //判断用户是否是执行的登录
        if (request.getURI().getPath().contains("/login/in")) {
            //如果是登录操作 放行
            return chain.filter(exchange);
        }

        //获取请求头是否包含token
        String jwtToken = request.getHeaders().getFirst("token");
        //判断token是否存在
        if (StringUtils.isEmpty(jwtToken)) {
            //不存在  发送信息到客户端
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        try {
            //令牌存在  解析令牌 判断令牌是否合法
            Claims claimsBody = AppJwtUtil.getClaimsBody(jwtToken);
            int  res= AppJwtUtil.verifyToken(claimsBody);
            //判断是否合法
            if (res ==0 || res==-1) {
                //令牌合法
                //获取到id  发送给前端
                Integer id = (Integer) claimsBody.get("id");
                ServerHttpRequest serverHttpRequest = request.mutate().headers(new Consumer<HttpHeaders>() {
                    @Override
                    public void accept(HttpHeaders httpHeaders) {
                        httpHeaders.add("userId",id+"");
                    }
                }).build();
                exchange.mutate().request(serverHttpRequest).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //出现异常  相应状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return  response.setComplete();
        }
        //放行
        return chain.filter(exchange);
    }

    /**
     * 优先级越小越先执行
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
