package com.project.evaluate.config;

import com.project.evaluate.realm.CustomerRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

//        配置系统受限资源
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
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    //    2、创建安全管理器
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(Realm realm) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
//        给安全管理器设置realm
        defaultWebSecurityManager.setRealm(realm);
        return defaultWebSecurityManager;
    }
//    3、创建自定义Realm

    @Bean
    public Realm getRealm() {
        CustomerRealm customerRealm = new CustomerRealm();
//        设置加密算法
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
//        设置散列次数
        hashedCredentialsMatcher.setHashIterations(1024);
        customerRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        return customerRealm;
    }
}
