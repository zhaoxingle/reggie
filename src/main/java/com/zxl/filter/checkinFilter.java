package com.zxl.filter;


import com.alibaba.fastjson.JSON;
import com.zxl.common.BaseContext;
import com.zxl.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.PushBuilder;
import java.io.IOException;

/**
 * 这个过滤器用来用户直接串改网址进入页面，从而跳过登录界面。
 */
@Slf4j
@WebFilter(filterName = "logincheckinFilter",urlPatterns = "/*") //创建过滤器，拦截那些路径urlPatterns
public class checkinFilter implements Filter{//实现接口里面的doFilter方法。
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();//判断是否匹配，路径匹配器，支持通配符
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        //需要放行的网址
        String[] urls=new String[]{ //"/backend/index.html",也会被放行，但是由于html里面解释 视频p17  4：50  主要是/employee/page会被拒接，导致看不到数据
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };
        //request.getRequestURI()和request.getRequestURL()区别，找错误找了10分钟，写错了
        String requestURL = request.getRequestURI();
        log.info("get :"+requestURL);
        boolean check = check(urls, requestURL); //判断当前网址是否需要放行
        log.info("check"+check);
        if(check){
            //放行
            filterChain.doFilter(request,response);
            return;
        }
        //判断是否登录
        if (request.getSession().getAttribute("employee")!=null){
            //已经登录
            //放行
            log.info("已登录");
            Long employeeid = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(employeeid);

            filterChain.doFilter(request,response);
            return;
        }
        if (request.getSession().getAttribute("user")!=null){
            //已经登录
            //放行
            log.info("已登录");
            Long userid = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userid);

            filterChain.doFilter(request,response);
            return;
        }

        //如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据。回到/backend/js/request.js 里的response拦截器
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }


    public boolean check(String[] urls,String requestURL){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURL);//通过这个类，可以实现利用通配符去匹配requestURL
            if (match){
                return  true;
            }
        }
        return false;
    }
}
