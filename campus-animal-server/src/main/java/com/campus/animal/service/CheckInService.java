package com.campus.animal.service;

import com.campus.animal.dto.CheckInSaveDTO;
import com.campus.animal.vo.CheckInVO;

import java.util.List;

/**
 * 打卡动态服务接口。
 * <p>
 * 核心业务能力：
 * <ul>
 *   <li><b>发布打卡</b>：验证动物存在 → 获取当前用户 → 创建打卡记录</li>
 *   <li><b>动物时间轴</b>：按动物 ID 查询所有打卡记录（时间倒序），附带发布者用户名</li>
 *   <li><b>我的历史</b>：查询当前用户发布的所有打卡记录</li>
 * </ul>
 * <p>
 * 关键设计：CheckInVO 中包含 username 字段，该字段不是 check_in 表的列，
 * 而是通过批量查询 sys_user 表关联得到。Service 层使用"先收集 userId 列表 →
 * 批量查询 → 构建 Map"的方式避免 N+1 查询问题。
 */
public interface CheckInService {

    /**
     * 发布一条打卡动态。
     * @param dto 打卡内容（动物 ID + 内容 + 心情 + 地点）
     * @return 创建成功的打卡记录
     */
    CheckInVO create(CheckInSaveDTO dto);

    /**
     * 查询某动物的打卡时间轴（按时间倒序）。
     * @param animalId 动物 ID
     * @return 打卡记录列表（含发布者用户名）
     */
    List<CheckInVO> getTimelineByAnimalId(Long animalId);

    /**
     * 查询当前登录用户的打卡历史。
     * @return 打卡记录列表（含用户名）
     */
    List<CheckInVO> getMyHistory();

    /**
     * 删除当前用户的一条打卡记录。
     * @param checkInId 打卡记录 ID
     */
    void deleteMyCheckIn(Long checkInId);

    /**
     * 查询当日打卡最多的动物及其位置路径。
     * @return 包含动物名、打卡次数、位置路径
     */
    Object getTodayTopAnimal();
}
