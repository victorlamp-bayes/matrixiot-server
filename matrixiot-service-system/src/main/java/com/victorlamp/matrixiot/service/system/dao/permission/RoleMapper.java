package com.victorlamp.matrixiot.service.system.dao.permission;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.framework.mybatis.core.dataobject.BaseDO;
import com.victorlamp.matrixiot.service.framework.mybatis.core.mapper.BaseMapperX;
import com.victorlamp.matrixiot.service.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.victorlamp.matrixiot.service.system.controller.permission.vo.role.RoleExportReqVO;
import com.victorlamp.matrixiot.service.system.controller.permission.vo.role.RolePageReqVO;
import com.victorlamp.matrixiot.service.system.entity.permission.RoleDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapperX<RoleDO> {

    default PageResult<RoleDO> selectPage(RolePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RoleDO>()
                .likeIfPresent(RoleDO::getName, reqVO.getName())
                .likeIfPresent(RoleDO::getCode, reqVO.getCode())
                .eqIfPresent(RoleDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(BaseDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(RoleDO::getId));
    }

    default List<RoleDO> selectList(RoleExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<RoleDO>()
                .likeIfPresent(RoleDO::getName, reqVO.getName())
                .likeIfPresent(RoleDO::getCode, reqVO.getCode())
                .eqIfPresent(RoleDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(BaseDO::getCreateTime, reqVO.getCreateTime()));
    }

    default RoleDO selectByName(String name) {
        return selectOne(RoleDO::getName, name);
    }

    default RoleDO selectByCode(String code) {
        return selectOne(RoleDO::getCode, code);
    }

    default List<RoleDO> selectListByStatus(@Nullable Collection<Integer> statuses) {
        return selectList(RoleDO::getStatus, statuses);
    }

}
