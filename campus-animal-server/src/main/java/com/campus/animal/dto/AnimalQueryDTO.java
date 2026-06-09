package com.campus.animal.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 动物列表分页查询参数 DTO。
 * <p>
 * 支持两种筛选方式：
 * <ul>
 *   <li><b>按名称模糊搜索</b>：name 不为空时，用 LIKE '%name%' 匹配</li>
 *   <li><b>按类型精确筛选</b>：type 不为空时，精确匹配 type 字段</li>
 * </ul>
 * <p>
 * 分页参数 page 和 size 有默认值且加了 {@code @Min(1)} 校验，
 * 防止传入 0 或负数导致 MyBatis-Plus 分页 SQL 异常。
 */
@Data
public class AnimalQueryDTO {

    /** 搜索关键词（模糊匹配名称，选填） */
    private String name;

    /** 类型筛选（精确匹配，选填） */
    private String type;

    /** 当前页码（从 1 开始，默认第 1 页） */
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    /** 每页条数（默认 10 条） */
    @Min(value = 1, message = "每页条数必须大于0")
    private Integer size = 10;
}
