package com.notary.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.notary.platform.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
}
