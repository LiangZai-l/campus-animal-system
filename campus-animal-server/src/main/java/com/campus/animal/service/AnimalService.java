package com.campus.animal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.animal.dto.AnimalQueryDTO;
import com.campus.animal.dto.AnimalSaveDTO;
import com.campus.animal.vo.AnimalDetailVO;
import com.campus.animal.vo.AnimalVO;

import java.util.List;

/**
 * 动物档案服务接口。
 * <p>
 * 核心业务能力：
 * <ul>
 *   <li>CRUD 操作（新增、修改、删除）— 仅 ADMIN 角色可执行</li>
 *   <li>分页查询 — 支持按名称模糊搜索 + 类型精确筛选</li>
 *   <li>详情查询 — 聚合返回动物档案 + 打卡时间轴</li>
 *   <li>类型枚举 — 返回所有不重复的动物类型列表</li>
 * </ul>
 * <p>
 * 权限控制不在 Service 层，而在 Controller 层通过 {@code @PreAuthorize} 实现。
 * 这样 Service 层保持纯粹，可以被不同权限等级的调用方复用。
 */
public interface AnimalService {

    /** 新增动物档案（仅 ADMIN） */
    AnimalVO create(AnimalSaveDTO dto);

    /** 修改动物档案（仅 ADMIN） */
    AnimalVO update(Long id, AnimalSaveDTO dto);

    /** 删除动物档案（仅 ADMIN，级联删除打卡记录） */
    void delete(Long id);

    /** 分页查询动物列表（支持名称模糊搜索 + 类型筛选） */
    IPage<AnimalVO> page(AnimalQueryDTO query);

    /** 查看动物详情（含打卡时间轴） */
    AnimalDetailVO getDetail(Long id);

    /** 获取所有动物类型（去重），用于前端筛选下拉框 */
    List<String> getTypes();
}
