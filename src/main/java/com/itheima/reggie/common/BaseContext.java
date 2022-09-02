package com.itheima.reggie.common;

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 在线程中设置当前用户id值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 在线程中取当前用户id值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
