package com.campus.animal.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 打卡动态响应 VO。
 * <p>
 * 注意：相比 CheckIn 实体，这里多了一个 {@code username} 字段。
 * username 不是 check_in 表的数据，而是通过批量查询 sys_user 表关联得到，
 * 避免了经典的 N+1 查询问题：
 * <p>
 * 错误做法（N+1）：
 * <pre>
 *   for (CheckIn checkIn : checkIns) {
 *       String username = userMapper.selectById(checkIn.getUserId()).getUsername();
 *   }
 * </pre>
 * 正确做法（批量查询）：
 * <pre>
 *   List&lt;Long&gt; userIds = checkIns.stream().map(CheckIn::getUserId).toList();
 *   Map&lt;Long, String&gt; userMap = userMapper.selectBatchIds(userIds)
 *       .stream().collect(toMap(User::getId, User::getUsername));
 * </pre>
 */
@Data
public class CheckInVO {

    /** 打卡 ID */
    private Long id;

    /** 关联的动物 ID */
    private Long animalId;

    /** 打卡人 ID */
    private Long userId;

    /** 打卡人用户名（从 sys_user 表关联查询，非 check_in 表字段） */
    private String username;

    /** 打卡内容 */
    private String content;

    /** 打卡图片 URL（多个逗号分隔） */
    private String images;

    /** 心情：happy/neutral/worried */
    private String mood;

    /** 打卡地点 */
    private String location;

    /** 打卡时间 */
    private LocalDateTime createdAt;
}
