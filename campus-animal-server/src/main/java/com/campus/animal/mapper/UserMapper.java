package com.campus.animal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.animal.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表数据访问层。
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}{@code <User>}，自动获得以下 CRUD 能力：
 * <ul>
 *   <li>{@code insert(T)} — 插入一条记录</li>
 *   <li>{@code deleteById(Serializable)} — 按主键删除</li>
 *   <li>{@code updateById(T)} — 按主键更新</li>
 *   <li>{@code selectById(Serializable)} — 按主键查询</li>
 *   <li>{@code selectList(Wrapper<T>)} — 条件查询列表</li>
 *   <li>{@code selectPage(Page<T>, Wrapper<T>)} — 分页查询</li>
 *   <li>{@code selectCount(Wrapper<T>)} — 条件计数</li>
 * </ul>
 * 共 17 个内置方法，无需编写任何 XML 或 SQL。
 * <p>
 * 如需自定义 SQL（如多表关联查询），在此接口中声明方法并使用 {@code @Select} 注解。
 * <p>
 * {@code @Mapper} 注解告诉 MyBatis 这是一个需要被扫描的 Mapper 接口。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
