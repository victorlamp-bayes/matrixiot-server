package com.victorlamp.matrixiot.service.system.convert.auth;

import cn.hutool.core.collection.CollUtil;
import com.victorlamp.matrixiot.service.system.controller.auth.vo.AuthLoginRespVO;
import com.victorlamp.matrixiot.service.system.controller.auth.vo.AuthPermissionInfoRespVO;
import com.victorlamp.matrixiot.service.system.controller.auth.vo.AuthSmsLoginReqVO;
import com.victorlamp.matrixiot.service.system.controller.auth.vo.AuthSmsSendReqVO;
import com.victorlamp.matrixiot.service.system.dto.sms.SmsCodeSendReqDTO;
import com.victorlamp.matrixiot.service.system.dto.sms.SmsCodeUseReqDTO;
import com.victorlamp.matrixiot.service.system.entity.oauth2.OAuth2AccessTokenDO;
import com.victorlamp.matrixiot.service.system.entity.permission.MenuDO;
import com.victorlamp.matrixiot.service.system.entity.permission.RoleDO;
import com.victorlamp.matrixiot.service.system.entity.user.AdminUserDO;
import com.victorlamp.matrixiot.service.system.enums.permission.MenuTypeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.victorlamp.matrixiot.common.util.collection.CollectionUtils.convertSet;
import static com.victorlamp.matrixiot.common.util.collection.CollectionUtils.filterList;
import static com.victorlamp.matrixiot.service.system.entity.permission.MenuDO.ID_ROOT;

@Mapper
public interface AuthConvert {

    AuthConvert INSTANCE = Mappers.getMapper(AuthConvert.class);

    AuthLoginRespVO convert(OAuth2AccessTokenDO bean);

    default AuthPermissionInfoRespVO convert(AdminUserDO user, List<RoleDO> roleList, List<MenuDO> menuList) {
        return AuthPermissionInfoRespVO.builder()
                .user(AuthPermissionInfoRespVO.UserVO.builder().id(user.getId()).nickname(user.getNickname()).avatar(user.getAvatar()).build())
                .roles(convertSet(roleList, RoleDO::getCode))
                // 权限标识信息
                .permissions(convertSet(menuList, MenuDO::getPermission))
                // 菜单树
                .menus(buildMenuTree(menuList))
                .build();
    }

    AuthPermissionInfoRespVO.MenuVO convertTreeNode(MenuDO menu);

    /**
     * 将菜单列表，构建成菜单树
     *
     * @param menuList 菜单列表
     * @return 菜单树
     */
    default List<AuthPermissionInfoRespVO.MenuVO> buildMenuTree(List<MenuDO> menuList) {
        if (CollUtil.isEmpty(menuList)) {
            return Collections.emptyList();
        }
        // 移除按钮
        menuList.removeIf(menu -> menu.getType().equals(MenuTypeEnum.BUTTON.getType()));
        // 排序，保证菜单的有序性
        menuList.sort(Comparator.comparing(MenuDO::getSort));

        // 构建菜单树
        Map<Long, AuthPermissionInfoRespVO.MenuVO> treeNodeMap = new LinkedHashMap<>();
        menuList.forEach(menu -> treeNodeMap.put(menu.getId(), AuthConvert.INSTANCE.convertTreeNode(menu)));
        // 处理父子关系
        treeNodeMap.values().stream().filter(node -> !node.getParentId().equals(ID_ROOT)).forEach(childNode -> {
            // 获得父节点
            AuthPermissionInfoRespVO.MenuVO parentNode = treeNodeMap.get(childNode.getParentId());
            if (parentNode == null) {
                LoggerFactory.getLogger(getClass()).error("[buildRouterTree][resource({}) 找不到父资源({})]",
                        childNode.getId(), childNode.getParentId());
                return;
            }
            // 将自己添加到父节点中
            if (parentNode.getChildren() == null) {
                parentNode.setChildren(new ArrayList<>());
            }
            parentNode.getChildren().add(childNode);
        });
        // 获得到所有的根节点
        return filterList(treeNodeMap.values(), node -> ID_ROOT.equals(node.getParentId()));
    }

    SmsCodeSendReqDTO convert(AuthSmsSendReqVO reqVO);

    SmsCodeUseReqDTO convert(AuthSmsLoginReqVO reqVO, Integer scene, String usedIp);

}
