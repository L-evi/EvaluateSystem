package com.project.evaluate.realm;

import com.project.evaluate.entity.Faculty;
import com.project.evaluate.mapper.FacultyMapper;
import com.project.evaluate.service.FacultyService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/1 08:42
 */
public class CustomerRealm extends AuthorizingRealm {
    @Resource
    private FacultyMapper facultyMapper;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
//        获取账号密码
        String principal = (String) authenticationToken.getPrincipal();
//        从数据库中获取
        Faculty faculty = facultyMapper.selectByUserID(principal);
//        System.out.println(faculty.toString());
//        封装信息
        if (faculty != null) {
            return new SimpleAuthenticationInfo(principal, faculty.getPassword(), ByteSource.Util.bytes(faculty.getUserID()), this.getName());
        }
        return null;
    }
}
