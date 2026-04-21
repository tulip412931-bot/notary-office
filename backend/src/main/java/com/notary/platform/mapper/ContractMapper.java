package com.notary.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.notary.platform.entity.Contract;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ContractMapper extends BaseMapper<Contract> {
}
