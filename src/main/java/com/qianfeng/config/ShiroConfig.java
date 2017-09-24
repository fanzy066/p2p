package com.qianfeng.config;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/24 0024.
 */
@Configuration
public class ShiroConfig {
    @Bean
    public JdbcRealm createRealm(){
        JdbcRealm realm = new JdbcRealm();
        realm.setPermissionsLookupEnabled(true);
        realm.setAuthenticationQuery("select pwd from users where name=?");
        realm.setUserRolesQuery("select rolename from role LEFT JOIN user_role using(rid) LEFT JOIN users u USING(id) WHERE u.name=?");
        realm.setPermissionsQuery("SELECT perms from res LEFT JOIN role_res USING(id) LEFT JOIN role r using(rid) WHERE r.rolename=?");
        return realm;
    }
    @Bean
    public CacheManager cm(){
        return new MemoryConstrainedCacheManager();
    }
    @Bean
    public DefaultWebSecurityManager desw(JdbcRealm realm, CacheManager cm){
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(realm);
        manager.setCacheManager(cm);
        return manager;
    }
    @Bean
    public ShiroFilterFactoryBean crateFilt(DefaultWebSecurityManager manager){
        ShiroFilterFactoryBean fb = new ShiroFilterFactoryBean();
        fb.setSecurityManager(manager);
        fb.setLoginUrl("/");
        Map<String,String> map=new HashMap<>();
        map.put("/","anon");

        map.put("/**","anon");
        fb.setFilterChainDefinitionMap(map);
        return fb;
    }

}
