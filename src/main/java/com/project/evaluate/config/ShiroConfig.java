package com.project.evaluate.config;

import com.project.evaluate.filter.JwtFilter;
import com.project.evaluate.realm.CustomerRealm;
import com.project.evaluate.realm.JwtRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description 用来整合Shiro框架相关的配置类
 * @since 2023/1/1 08:36
 */
@Configuration
public class ShiroConfig {
    //    1、shiro filter：负责拦截所有请求
    @Bean(name = "shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
//        给filter设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);

/*//        配置系统受限资源
        Map<String, String> map = new HashMap<String, String>();
//        authc是请求这个资源需要认证和授权
        map.put("/**", "authc");
//        设置登录页面匿名访问：没有拦截
        map.put("/user/login", "anon");
        map.put("/user/logout", "anon");
        map.put("/user/register", "anon");
        map.put("/api/verify/**", "anon");
//        默认认证路径
        shiroFilterFactoryBean.setLoginUrl("/login");
//        设置错误路径
        shiroFilterFactoryBean.setUnauthorizedUrl("/error");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);*/

//        添加自定义过滤器
        Map<String, Filter> filterMap = new HashMap<String, Filter>();
        filterMap.put("jwt", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filterMap);

//        配置系统受限资源
        Map<String, String> map = new HashMap<String, String>();
        map.put("/user/login", "anon");
        map.put("/user/register", "anon");
        map.put("/api/verify/**", "anon");
        map.put("/user/logout", "anon");
        map.put("/**", "jwt");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    //    2、创建安全管理器
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(JwtRealm realm) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
//        给安全管理器设置realm
        defaultWebSecurityManager.setRealm(realm);
//        关闭session
        DefaultSubjectDAO defaultSubjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        defaultSubjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        defaultWebSecurityManager.setSubjectDAO(defaultSubjectDAO);
        return defaultWebSecurityManager;
    }
}
