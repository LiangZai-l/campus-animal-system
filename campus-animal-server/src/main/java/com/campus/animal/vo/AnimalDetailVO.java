package com.campus.animal.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 动物详情响应 VO，继承 {@link AnimalVO} 的所有基础字段。
 * <p>
 * 额外包含：
 * <ul>
 *   <li>{@code timeline}：该动物的打卡时间轴列表（按时间倒序）</li>
 * </ul>
 * <p>
 * 使用继承而非组合：利用多态，详情页可以直接当做 AnimalVO 使用，
 * 同时通过 timeline 字段获取额外的打卡数据。
 * <p>
 * {@code @EqualsAndHashCode(callSuper = true)} 确保 equals/hashCode
 * 方法会包含父类字段。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnimalDetailVO extends AnimalVO {

    /** 打卡时间轴（时间倒序，每条记录包含发布者用户名） */
    private List<CheckInVO> timeline;
}
