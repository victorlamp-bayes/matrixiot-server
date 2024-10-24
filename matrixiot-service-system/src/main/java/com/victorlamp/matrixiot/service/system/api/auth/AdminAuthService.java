package com.victorlamp.matrixiot.service.system.api.auth;

import com.victorlamp.matrixiot.service.system.controller.auth.vo.AuthLoginReqVO;
import com.victorlamp.matrixiot.service.system.controller.auth.vo.AuthLoginRespVO;
import com.victorlamp.matrixiot.service.system.controller.auth.vo.AuthSmsLoginReqVO;
import com.victorlamp.matrixiot.service.system.controller.auth.vo.AuthSmsSendReqVO;
import com.victorlamp.matrixiot.service.system.entity.user.AdminUserDO;

import javax.validation.Valid;

/**
 * 管理后台的认证 Service 接口
 * <p>
 * 提供用户的登录、登出的能力
 *
 * @author 芋道源码
 */
public interface AdminAuthService {

    /**
     * 验证账号 + 密码。如果通过，则返回用户
     *
     * @param username 账号
     * @param password 密码
     * @return 用户
     */
    AdminUserDO authenticate(String username, String password);

    /**
     * 账号登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AuthLoginRespVO login(@Valid AuthLoginReqVO reqVO);

    /**
     * 基于 token 退出登录
     *
     * @param token   token
     * @param logType 登出类型
     */
    void logout(String token, Integer logType);

    /**
     * 短信验证码发送
     *
     * @param reqVO 发送请求
     */
    void sendSmsCode(AuthSmsSendReqVO reqVO);

    /**
     * 短信登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AuthLoginRespVO smsLogin(AuthSmsLoginReqVO reqVO);

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 登录结果
     */
    AuthLoginRespVO refreshToken(String refreshToken);

}