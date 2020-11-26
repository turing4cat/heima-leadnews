package com.heima.utils.threadlocal;

import com.heima.model.user.pojo.ApUser;
import com.heima.model.wemedia.pojos.WmUser;

/**
 * 用户登录的时候把用户数据存储到线程当中
 */
public class AppThreadLocalUtils {
    private static final ThreadLocal<ApUser> USER_THREAD_LOCAL=new ThreadLocal<ApUser>();

    //设置方法
    public static void setUser(ApUser apUser){
        USER_THREAD_LOCAL.set(apUser);
    }
    //获取值得方法
    public static ApUser getUser(){
        return USER_THREAD_LOCAL.get();
    }
}
