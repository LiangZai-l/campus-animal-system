package com.campus.animal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.animal.common.BusinessException;
import com.campus.animal.common.ResultCode;
import com.campus.animal.dto.AnimalQueryDTO;
import com.campus.animal.dto.AnimalSaveDTO;
import com.campus.animal.entity.Animal;
import com.campus.animal.entity.CheckIn;
import com.campus.animal.entity.User;
import com.campus.animal.mapper.AnimalMapper;
import com.campus.animal.mapper.CheckInMapper;
import com.campus.animal.mapper.UserMapper;
import com.campus.animal.security.SecurityUtils;
import com.campus.animal.service.AnimalService;
import com.campus.animal.vo.AnimalDetailVO;
import com.campus.animal.vo.AnimalVO;
import com.campus.animal.vo.CheckInVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 动物档案服务实现类。
 * <p>
 * 核心业务流程：
 * <ul>
 *   <li><b>新增</b>：DTO → Entity（设置 createdBy = 当前用户） → insert → toVO 返回</li>
 *   <li><b>修改</b>：查库 → 不存在抛异常 → 更新字段 → updateById → toVO 返回</li>
 *   <li><b>删除</b>：查库 → 不存在抛异常 → deleteById（级联删除打卡由数据库外键处理）</li>
 *   <li><b>分页查询</b>：构建 LambdaQueryWrapper（模糊+精确） → selectPage → Entity→VO 转换</li>
 *   <li><b>详情</b>：查动物 → 不存在抛异常 → 查打卡 → 批量查用户 → 组装 timeline</li>
 *   <li><b>类型列表</b>：查全部 → Stream.map→distinct 去重</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService {

    private final AnimalMapper animalMapper;
    private final CheckInMapper checkInMapper;
    private final UserMapper userMapper;

    /**
     * 新增动物档案。
     * <p>
     * createdBy 从 SecurityContext 获取当前登录用户 ID，不从前端传入，
     * 防止伪造。status 默认为 1（在校）。
     */
    @Override
    public AnimalVO create(AnimalSaveDTO dto) {
        Animal animal = new Animal();
        animal.setName(dto.getName());
        animal.setType(dto.getType());
        animal.setArea(dto.getArea());
        animal.setDescription(dto.getDescription());
        animal.setCoverImage(dto.getCoverImage());
        // 状态默认为 1（在校），如果 DTO 中传了值则使用 DTO 的值
        animal.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        // 创建人从安全上下文获取，不从 DTO 传入（安全性）
        animal.setCreatedBy(SecurityUtils.getCurrentUserId());

        animalMapper.insert(animal);
        return toVO(animal);
    }

    /**
     * 修改动物档案。
     * <p>
     * 先查后改：确保要修改的记录存在，不存在时抛出 NOT_FOUND。
     * status 字段特殊处理：如果 DTO 没传则保持原值不变。
     */
    @Override
    public AnimalVO update(Long id, AnimalSaveDTO dto) {
        // 先查询确保存在
        Animal animal = animalMapper.selectById(id);
        if (animal == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "动物档案不存在");
        }

        // 更新字段
        animal.setName(dto.getName());
        animal.setType(dto.getType());
        animal.setArea(dto.getArea());
        animal.setDescription(dto.getDescription());
        animal.setCoverImage(dto.getCoverImage());
        if (dto.getStatus() != null) {
            animal.setStatus(dto.getStatus());
        }

        animalMapper.updateById(animal);
        return toVO(animal);
    }

    /**
     * 删除动物档案。
     * <p>
     * 先查后删。级联删除打卡记录由数据库外键约束（ON DELETE CASCADE）自动处理，
     * 无需在代码中手动删除 check_in 记录。
     */
    @Override
    public void delete(Long id) {
        Animal animal = animalMapper.selectById(id);
        if (animal == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "动物档案不存在");
        }
        animalMapper.deleteById(id);
    }

    /**
     * 分页查询动物列表。
     * <p>
     * 使用 LambdaQueryWrapper 构建动态查询条件：
     * <ul>
     *   <li>name 不为空 → LIKE '%name%' 模糊搜索</li>
     *   <li>type 不为空 → 精确匹配</li>
     * </ul>
     * 结果按创建时间倒序排列（最新录入的排在前面）。
     * <p>
     * MyBatis-Plus 分页流程：
     * 1. 创建 Page 对象（page, size）
     * 2. selectPage 返回的 Page 对象中包含 total、records、pages 等信息
     * 3. 将 Entity 列表转为 VO 列表后重新封装到 Page 对象中
     */
    @Override
    public IPage<AnimalVO> page(AnimalQueryDTO query) {
        // 构建动态查询条件
        LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Animal::getName, query.getName());  // LIKE '%keyword%'
        }
        if (StringUtils.hasText(query.getType())) {
            wrapper.eq(Animal::getType, query.getType());    // 精确匹配
        }
        wrapper.orderByDesc(Animal::getCreatedAt);            // 按创建时间倒序

        // 执行分页查询
        Page<Animal> page = new Page<>(query.getPage(), query.getSize());
        Page<Animal> result = animalMapper.selectPage(page, wrapper);

        // Entity → VO 转换
        List<AnimalVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        // 重新封装分页数据（保留 total 等分页信息）
        Page<AnimalVO> voPage = new Page<>(query.getPage(), query.getSize(), result.getTotal());
        voPage.setRecords(records);
        return voPage;
    }

    /**
     * 查看动物详情（含打卡时间轴）。
     * <p>
     * 重点：避免 N+1 查询问题。实现方式：
     * <ol>
     *   <li>查动物档案（1 次查询）</li>
     *   <li>查该动物的所有打卡记录（1 次查询）</li>
     *   <li>收集所有打卡的 userId → 去重 → 批量查用户表（1 次查询）</li>
     *   <li>构建 userId→username 的 Map，组装 CheckInVO 时直接查 Map</li>
     * </ol>
     * 总共 3 次数据库查询，无论有多少条打卡记录。
     * 如果每条打卡都单独查用户（N+1），N 条打卡就需要 N+1 次查询。
     */
    @Override
    public AnimalDetailVO getDetail(Long id) {
        // 1. 查动物档案
        Animal animal = animalMapper.selectById(id);
        if (animal == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "动物档案不存在");
        }

        // 2. 实体属性复制到 VO
        AnimalDetailVO detail = new AnimalDetailVO();
        BeanUtils.copyProperties(animal, detail);

        // 3. 查该动物的所有打卡记录（时间倒序）
        List<CheckIn> checkIns = checkInMapper.selectList(
                new LambdaQueryWrapper<CheckIn>()
                        .eq(CheckIn::getAnimalId, id)
                        .orderByDesc(CheckIn::getCreatedAt));

        if (!checkIns.isEmpty()) {
            // 4. 收集所有打卡人的 userId，去重
            Set<Long> userIds = checkIns.stream()
                    .map(CheckIn::getUserId)
                    .collect(Collectors.toSet());

            // 5. 批量查询用户，构建 userId → username 映射
            Map<Long, String> usernameMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(
                            User::getId,
                            u -> u.getUsername() != null ? u.getUsername() : "",
                            (a, b) -> a));  // 处理重复 key（不会发生，但语法需要）

            // 6. 组装 timeline（CheckIn → CheckInVO，填充 username）
            List<CheckInVO> timeline = checkIns.stream().map(ci -> {
                CheckInVO vo = new CheckInVO();
                BeanUtils.copyProperties(ci, vo);
                vo.setUsername(usernameMap.get(ci.getUserId()));  // 从 Map 取用户名，O(1)
                return vo;
            }).collect(Collectors.toList());
            detail.setTimeline(timeline);
        } else {
            detail.setTimeline(List.of());  // 空列表，前端据此显示"暂无打卡记录"
        }

        return detail;
    }

    /**
     * 获取所有动物类型（去重）。
     * <p>
     * 实现方式：查全部动物 → 提取 type 字段 → distinct 去重。
     * 优点：简单直接，自动反映数据库中的实际数据。
     * 缺点：数据量大时效率低，但校园动物数据量通常很小（几十到几百条），完全可接受。
     */
    @Override
    public List<String> getTypes() {
        return animalMapper.selectList(null).stream()
                .map(Animal::getType)
                .distinct()   // Java Stream 去重
                .collect(Collectors.toList());
    }

    /**
     * Entity → VO 转换（私有辅助方法）。
     * 使用 Spring BeanUtils.copyProperties 进行同名属性复制。
     */
    private AnimalVO toVO(Animal animal) {
        AnimalVO vo = new AnimalVO();
        BeanUtils.copyProperties(animal, vo);
        return vo;
    }
}
