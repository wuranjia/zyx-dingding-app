package net.whxxykj.maya.common;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.auth0.jwt.exceptions.JWTVerificationException;

import net.whxxykj.maya.MayaConstant;
import net.whxxykj.maya.common.entity.ManagerUser;
import net.whxxykj.maya.common.entity.UserCache;
import net.whxxykj.maya.common.runtime.Runtime;
import net.whxxykj.maya.common.util.JwtUtils;
import net.whxxykj.maya.common.util.StringUtil;
import net.whxxykj.maya.common.util.UrlUtil;
import net.whxxykj.maya.plugin.cache.RedisCacheService;

/************************************************
 * Copyright (c)  by whxxykj
 * All right reserved.
 * Create Author: JM
 * Create Date  : 2018/10/12
 * Last version : 1.0
 * Description  : 基础系统拦截器
 * Last Update Date: 2018/10/16
 * Change Log:
 **************************************************/
@Component
public class SysInterceptor implements HandlerInterceptor {

    protected static final Logger logger = LoggerFactory.getLogger(SysInterceptor.class);

    public static final String AUTH_TOKEN = "auth-token";
    public static final String OPTIONS = "OPTIONS";
    public static final String Authorization = "Authorization";

    @Autowired
    private RedisCacheService cacheService;
    
    @Value("/${runTimeConfig.appName}")
    private String appName;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	//获取用户访问的资源和系统类型
        String uri = UrlUtil.getCurrentRequestURI(request);
        String siteType = Runtime.get(MayaConstant.SysType.SITE_TYPE);
        String optId = null;
        String accessToken = null;
        String refreshToken = null;
        logger.info("系统拦截到请求：{}", uri);
        uri = uri.startsWith("//") ? uri.replaceFirst("//", "/") : uri;
        if (request.getMethod().equals(OPTIONS)) {
            return true;
        }
        //无权限控制,跳过验证
        List<String> noAuthUrls = cacheService.getMapValue(MayaConstant.CACHE.COMMON_AUTH_URL, siteType);
        if (!CollectionUtils.isEmpty(noAuthUrls) && noAuthUrls.contains(uri)) {
            return true;
        }
        accessToken = request.getHeader(Authorization);// 从 http 请求头中取出 token
        if(StringUtil.isEmpty(accessToken)) {//用户失效或未登录
            request.getRequestDispatcher(appName + "/msg/authError?type=4").forward(request,response);
            return false;
        }
        try {
            optId = JwtUtils.getSubject(accessToken);
            JwtUtils.verifyToken(accessToken, optId);
        } catch (JWTVerificationException e) {
            request.getRequestDispatcher(appName + "/msg/authError?type=4").forward(request,response);
            return false;
        }
        ManagerUser user =  cacheService.getMapValue(MayaConstant.CACHE.MANAGER_LOGIN_USER_KEY, optId);
        if(user  == null) {//用户失效或未登录
            request.getRequestDispatcher(appName + "/msg/authError?type=4").forward(request,response);
            return false;
        }
        if(JwtUtils.isExpires(accessToken)) {
            refreshToken = user.getRefreshToken();
            if(refreshToken == null) {
                request.getRequestDispatcher(appName + "/msg/authError?type=4").forward(request,response);
                return false;    
            }
            if(JwtUtils.isExpires(refreshToken)) {
                request.getRequestDispatcher(appName + "/msg/authError?type=4").forward(request,response);
                return false;    
            }else {
                accessToken = JwtUtils.createAccessToken(optId);
                refreshToken = JwtUtils.createRefreshToken(optId);
                user.setAccessToken(accessToken);
                user.setRefreshToken(refreshToken);
                cacheService.addMapValue(MayaConstant.CACHE.MANAGER_LOGIN_USER_KEY, optId, user);
                response.addHeader(Authorization, accessToken);
            }
        }
        UserCache.getInstance().bind(optId, user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }
}
