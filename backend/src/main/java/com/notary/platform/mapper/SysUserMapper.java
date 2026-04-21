package com.notary.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.notary.platform.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
