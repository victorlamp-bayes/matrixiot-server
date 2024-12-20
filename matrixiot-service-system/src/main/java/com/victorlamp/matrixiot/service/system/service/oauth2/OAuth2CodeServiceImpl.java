package com.victorlamp.matrixiot.service.system.service.oauth2;

import cn.hutool.core.util.IdUtil;
import com.victorlamp.matrixiot.common.util.date.DateUtils;
import com.victorlamp.matrixiot.service.system.api.oauth2.OAuth2CodeService;
import com.victorlamp.matrixiot.service.system.dao.oauth2.OAuth2CodeMapper;
import com.victorlamp.matrixiot.service.system.entity.oauth2.OAuth2CodeDO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.system.enums.ErrorCodeConstants.OAUTH2_CODE_EXPIRE;
import static com.victorlamp.matrixiot.service.system.enums.ErrorCodeConstants.OAUTH2_CODE_NOT_EXISTS;

/**
 * OAuth2.0 授权码 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class OAuth2CodeServiceImpl implements OAuth2CodeService {

    /**
     * 授权码的过期时间，默认 5 分钟
     */
    private static final Integer TIMEOUT = 5 * 60;

    @Resource
    private OAuth2CodeMapper oauth2CodeMapper;

    private static String generateCode() {
        return IdUtil.fastSimpleUUID();
    }

    @Override
    public OAuth2CodeDO createAuthorizationCode(Long userId, Integer userType, String clientId,
                                                List<String> scopes, String redirectUri, String state) {
        OAuth2CodeDO codeDO = new OAuth2CodeDO();

        codeDO.setCode(generateCode());
        codeDO.setUserId(userId);
        codeDO.setUserType(userType);
        codeDO.setClientId(clientId);
        codeDO.setScopes(scopes);
        codeDO.setExpiresTime(LocalDateTime.now().plusSeconds(TIMEOUT));
        codeDO.setRedirectUri(redirectUri);
        codeDO.setState(state);
        oauth2CodeMapper.insert(codeDO);

        return codeDO;
    }

    @Override
    public OAuth2CodeDO consumeAuthorizationCode(String code) {
        OAuth2CodeDO codeDO = oauth2CodeMapper.selectByCode(code);
        if (codeDO == null) {
            throw exception(OAUTH2_CODE_NOT_EXISTS);
        }
        if (DateUtils.isExpired(codeDO.getExpiresTime())) {
            throw exception(OAUTH2_CODE_EXPIRE);
        }
        oauth2CodeMapper.deleteById(codeDO.getId());
        return codeDO;
    }

}
