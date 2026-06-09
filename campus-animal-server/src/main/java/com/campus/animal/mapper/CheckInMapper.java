package com.campus.animal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.animal.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打卡动态表数据访问层。
 * <p>
 * 继承 {@link BaseMapper}{@code <CheckIn>} 获得内置 CRUD 能力。
 * 目前所有查询（按动物 ID 查时间轴、按用户 ID 查历史）
 * 均通过 {@code LambdaQueryWrapper} 条件构造器实现，无需手写 SQL。
 * <p>
 * 按动物 ID 查询时间轴示例：
 * <pre>{@code
 *   LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
 *   wrapper.eq(CheckIn::getAnimalId, animalId)
 *          .orderByDesc(CheckIn::getCreatedAt);
 *   checkInMapper.selectList(wrapper);
 * }</pre>
 */
@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {
}
