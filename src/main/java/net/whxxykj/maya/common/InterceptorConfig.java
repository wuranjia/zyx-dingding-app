package net.whxxykj.maya.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Bean
    public SysInterceptor myInterceptor(){
        return new SysInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //不需要拦截的
        List<String> noAuths = Arrays.asList("/",
            "/error",
            "/mobile/sysm/login/**",
            "/webjars/**",
            "/*/msg/authError",
            "/static/**",
            "/smsm/**",
            "/mobile/aep/org/reg",
            "/mobile/sys/common/error",
            "/mobile/sysm/pubselect/*",
            "/mobile/sys/version/*",
            "/mobile/sysm/cpmpany/test",
            "/mobile/sysm/ding/callback",
            "/mobile/sysm/param/findParamList",
            "/mobile/sysm/pubselect/getCodeByTypes",
            "/mobile/sysm/file/findPageList"
           );

        registry.addInterceptor(myInterceptor()).addPathPatterns("/**").excludePathPatterns(noAuths);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                //设置允许跨域请求的域名
                .allowedOrigins(CorsConfiguration.ALL)
                //设置允许的头
                .allowedHeaders(CorsConfiguration.ALL)
                //是否允许证书 不再默认开启
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods(CorsConfiguration.ALL)
                //跨域允许时间
                .maxAge(3600);
    }
}
