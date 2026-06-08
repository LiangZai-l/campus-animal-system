package com.campus.animal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.animal.entity.Animal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnimalMapper extends BaseMapper<Animal> {
}
