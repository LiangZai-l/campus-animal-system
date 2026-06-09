package com.campus.animal.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 动物列表项响应 VO（用于首页卡片列表和管理后台表格）。
 * <p>
 * 继承关系：{@link AnimalDetailVO} 继承此类，添加了打卡时间轴字段。
 * 这样设计的好处：
 * <ul>
 *   <li>列表查询不加载打卡时间轴（减少数据传输量）</li>
 *   <li>详情查询通过 AnimalDetailVO 附带打卡数据</li>
 * </ul>
 */
@Data
public class AnimalVO {

    /** 动物 ID */
    private Long id;

    /** 动物名称 */
    private String name;

    /** 动物类型：猫/狗/鸟/其他 */
    private String type;

    /** 常驻区域 */
    private String area;

    /** 特征描述 */
    private String description;

    /** 封面图片 URL */
    private String coverImage;

    /** 在校状态：1=在校，0=已离校 */
    private Integer status;

    /** 累计投喂次数 */
    private Integer feederCount;

    /** 录入时间 */
    private LocalDateTime createdAt;

    /** 最后更新时间 */
    private LocalDateTime updatedAt;
}
