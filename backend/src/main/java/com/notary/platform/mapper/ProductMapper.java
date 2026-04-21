package com.notary.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.notary.platform.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
