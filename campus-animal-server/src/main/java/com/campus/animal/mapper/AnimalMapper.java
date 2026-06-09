package com.campus.animal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.animal.entity.Animal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动物档案表数据访问层。
 * <p>
 * 继承 {@link BaseMapper}{@code <Animal>} 获得 17 个内置 CRUD 方法。
 * 配合 MyBatis-Plus 的 {@code LambdaQueryWrapper} 和分页插件，
 * 无需编写任何 SQL 即可实现复杂的分页查询和条件筛选。
 * <p>
 * 分页使用示例：
 * <pre>{@code
 *   Page<Animal> page = new Page<>(1, 10);
 *   LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<>();
 *   wrapper.like(Animal::getName, "小花").eq(Animal::getType, "猫");
 *   animalMapper.selectPage(page, wrapper);
 * }</pre>
 */
@Mapper
public interface AnimalMapper extends BaseMapper<Animal> {
}
