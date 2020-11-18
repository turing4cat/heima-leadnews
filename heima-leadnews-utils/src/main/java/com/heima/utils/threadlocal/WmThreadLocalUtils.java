package com.heima.utils.threadlocal;

import com.heima.model.wemedia.pojos.WmUser;

/**
 * 用户登录的时候把用户数据存储到线程当中
 */
public class WmThreadLocalUtils {
    private static final ThreadLocal<WmUser> USER_THREAD_LOCAL=new ThreadLocal<WmUser>();

    //设置方法
    public static void setUser(WmUser wmUser){
        USER_THREAD_LOCAL.set(wmUser);
    }
    //获取值得方法
    public static WmUser getUser(){
        return USER_THREAD_LOCAL.get();
    }
}
