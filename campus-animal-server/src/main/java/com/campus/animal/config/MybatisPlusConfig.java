package com.campus.animal.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件配置。
 * <p>
 * 当前仅注册了分页插件。MyBatis-Plus 3.5.9 将分页插件拆分到了
 * 独立模块 {@code mybatis-plus-jsqlparser}，因此 pom.xml 需同时引入：
 * <ul>
 *   <li>{@code mybatis-plus-spring-boot3-starter}</li>
 *   <li>{@code mybatis-plus-jsqlparser}（分页插件依赖的 SQL 解析器）</li>
 * </ul>
 * <p>
 * 分页插件工作原理：
 * 拦截 SQL 执行前将其改写，自动追加 LIMIT 和 COUNT 查询。
 * {@code DbType.MYSQL} 告诉插件使用 MySQL 方言生成分页 SQL。
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 拦截器，添加 MySQL 分页插件。
     * <p>
     * 使用时只需调用 MyBatis-Plus 的 {@code Page<T>} 对象，
     * 拦截器会自动将查询改写为分页 SQL。
     *
     * @return MybatisPlusInterceptor 实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // PaginationInnerInterceptor 负责分页 SQL 改写
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
