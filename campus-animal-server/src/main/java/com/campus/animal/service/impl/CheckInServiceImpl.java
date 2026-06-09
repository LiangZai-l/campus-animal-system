package com.campus.animal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.animal.common.BusinessException;
import com.campus.animal.common.ResultCode;
import com.campus.animal.dto.CheckInSaveDTO;
import com.campus.animal.entity.CheckIn;
import com.campus.animal.entity.User;
import com.campus.animal.mapper.AnimalMapper;
import com.campus.animal.mapper.CheckInMapper;
import com.campus.animal.mapper.UserMapper;
import com.campus.animal.security.SecurityUtils;
import com.campus.animal.service.CheckInService;
import com.campus.animal.vo.CheckInVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 打卡动态服务实现类。
 * <p>
 * 核心业务流程：
 * <ul>
 *   <li><b>发布打卡</b>：验证动物存在 → 从 SecurityContext 获取当前用户 → 创建打卡记录</li>
 *   <li><b>动物时间轴</b>：查动物存在 → 查打卡记录（时间倒序） → 批量查用户 → 组装 VO</li>
 *   <li><b>我的历史</b>：按当前用户 ID 查打卡记录 → 批量查用户 → 组装 VO</li>
 * </ul>
 * <p>
 * 重点：避免 N+1 查询问题。toVOList() 方法使用"收集 userId → 批量查询 → 构建 Map"
 * 的方式，将 N 条打卡的 username 填充从 N+1 次查询优化为 2 次查询。
 */
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final CheckInMapper checkInMapper;
    private final AnimalMapper animalMapper;
    private final UserMapper userMapper;

    /**
     * 发布打卡。
     * <p>
     * 关键安全设计：userId 从 SecurityContext 获取当前登录用户，
     * 不从前端传入。即使前端伪造了 userId，后端也以 JWT 令牌中的用户为准。
     */
    @Override
    public CheckInVO create(CheckInSaveDTO dto) {
        // 验证动物是否存在
        if (animalMapper.selectById(dto.getAnimalId()) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "动物档案不存在");
        }

        // 构建打卡记录
        CheckIn checkIn = new CheckIn();
        checkIn.setAnimalId(dto.getAnimalId());
        // userId 从安全上下文获取，不信任前端传入的值（安全性）
        checkIn.setUserId(SecurityUtils.getCurrentUserId());
        checkIn.setContent(dto.getContent());
        checkIn.setImages(dto.getImages());
        checkIn.setMood(dto.getMood());
        checkIn.setLocation(dto.getLocation());

        checkInMapper.insert(checkIn);
        return toVO(checkIn);
    }

    /**
     * 查询某动物的打卡时间轴（按时间倒序）。
     * <p>
     * 返回的每条打卡记录都包含发布者用户名，方便前端直接展示。
     */
    @Override
    public List<CheckInVO> getTimelineByAnimalId(Long animalId) {
        // 验证动物存在
        if (animalMapper.selectById(animalId) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "动物档案不存在");
        }

        // 查询该动物的所有打卡（时间倒序）
        List<CheckIn> checkIns = checkInMapper.selectList(
                new LambdaQueryWrapper<CheckIn>()
                        .eq(CheckIn::getAnimalId, animalId)
                        .orderByDesc(CheckIn::getCreatedAt));

        return toVOList(checkIns);
    }

    /**
     * 查询当前用户的打卡历史。
     * <p>
     * userId 从 SecurityContext 获取，因此不需要传参。
     * 用户只能查看自己的打卡记录。
     */
    @Override
    public List<CheckInVO> getMyHistory() {
        // 按当前用户 ID 查询打卡记录（时间倒序）
        List<CheckIn> checkIns = checkInMapper.selectList(
                new LambdaQueryWrapper<CheckIn>()
                        .eq(CheckIn::getUserId, SecurityUtils.getCurrentUserId())
                        .orderByDesc(CheckIn::getCreatedAt));

        return toVOList(checkIns);
    }

    /**
     * 删除当前用户的一条打卡记录。
     * <p>
     * 安全控制：校验打卡记录的 userId 必须等于当前登录用户，
     * 防止用户删除他人的打卡记录。
     */
    @Override
    public void deleteMyCheckIn(Long checkInId) {
        CheckIn checkIn = checkInMapper.selectById(checkInId);
        if (checkIn == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "打卡记录不存在");
        }
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!Objects.equals(checkIn.getUserId(), currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除他人的打卡记录");
        }
        checkInMapper.deleteById(checkInId);
    }

    /**
     * 查询当日打卡最多的动物及其位置路径。
     * <p>
     * 流程：
     * <ol>
     *   <li>查询今天所有打卡记录</li>
     *   <li>按动物 ID 分组统计次数</li>
     *   <li>找出打卡次数最多的动物</li>
     *   <li>按时间顺序收集该动物的打卡位置作为路径</li>
     * </ol>
     * 如果今天没有打卡记录或没有找到动物，返回 null。
     */
    @Override
    public Object getTodayTopAnimal() {
        LocalDate today = LocalDate.now();

        // 查询今天所有打卡记录
        List<CheckIn> todayCheckIns = checkInMapper.selectList(
                new LambdaQueryWrapper<CheckIn>()
                        .ge(CheckIn::getCreatedAt, today.atStartOfDay())
                        .lt(CheckIn::getCreatedAt, today.plusDays(1).atStartOfDay()));

        if (todayCheckIns.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("hasData", false);
            result.put("message", "今天还没有打卡记录");
            return result;
        }

        // 按 animalId 分组计数，找出打卡最多的动物
        Long topAnimalId = todayCheckIns.stream()
                .collect(Collectors.groupingBy(CheckIn::getAnimalId, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (topAnimalId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("hasData", false);
            result.put("message", "暂无数据");
            return result;
        }

        // 填充 username 映射
        Set<Long> userIds = todayCheckIns.stream()
                .filter(ci -> ci.getAnimalId().equals(topAnimalId))
                .map(CheckIn::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> usernameMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(
                        u -> u.getId(),
                        u -> u.getUsername() != null ? u.getUsername() : "",
                        (a, b) -> a));

        // 获取该动物的打卡位置路径（按时间顺序）
        List<Map<String, Object>> path = todayCheckIns.stream()
                .filter(ci -> ci.getAnimalId().equals(topAnimalId))
                .sorted(Comparator.comparing(CheckIn::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(ci -> {
                    Map<String, Object> point = new HashMap<>();
                    point.put("location", ci.getLocation());
                    LocalDateTime createdAt = ci.getCreatedAt();
                    point.put("time", createdAt != null ? createdAt.toString() : "");
                    point.put("username", usernameMap.getOrDefault(ci.getUserId(), ""));
                    return point;
                })
                .collect(Collectors.toList());

        var animal = animalMapper.selectById(topAnimalId);

        Map<String, Object> result = new HashMap<>();
        result.put("hasData", true);
        result.put("animalId", topAnimalId);
        result.put("animalName", animal != null ? animal.getName() : "未知");
        result.put("count", path.size());
        result.put("path", path);
        return result;
    }

    /**
     * 批量 Entity → VO 转换（核心优化：避免 N+1 查询）。
     * <p>
     * 工作流程：
     * <ol>
     *   <li>空列表直接返回，无需查数据库</li>
     *   <li>收集所有打卡记录的 userId，去重（Set）</li>
     *   <li><b>一次批量查询</b>（selectBatchIds）获取所有相关用户</li>
     *   <li>构建 userId → username 的 HashMap（O(1) 查找）</li>
     *   <li>组装 CheckInVO 时直接从 Map 取 username</li>
     * </ol>
     * <p>
     * 性能对比（假设 100 条打卡记录）：
     * <ul>
     *   <li>N+1 方式：100 次单独查询用户 → 101 次数据库访问</li>
     *   <li>批量方式：1 次批量查询用户 → 2 次数据库访问</li>
     * </ul>
     *
     * @param checkIns 打卡记录列表
     * @return 带用户名的 CheckInVO 列表
     */
    private List<CheckInVO> toVOList(List<CheckIn> checkIns) {
        if (checkIns.isEmpty()) {
            return List.of();  // JDK 9+ 不可变空列表
        }

        // 收集所有 userId 并去重
        Set<Long> userIds = checkIns.stream()
                .map(CheckIn::getUserId)
                .collect(Collectors.toSet());

        // 批量查询用户，构建 userId → username 映射
        Map<Long, String> usernameMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        u -> u.getUsername() != null ? u.getUsername() : "",
                        (a, b) -> a));  // 处理重复 key（逻辑上不会发生）

        // 组装 VO 列表
        return checkIns.stream().map(ci -> {
            CheckInVO vo = new CheckInVO();
            BeanUtils.copyProperties(ci, vo);
            // 从 Map 中取 username，O(1) 时间复杂度
            vo.setUsername(usernameMap.get(ci.getUserId()));
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 单条 Entity → VO 转换。
     * 用于创建打卡后的立即返回场景，此时只有一条记录，直接查用户即可。
     */
    private CheckInVO toVO(CheckIn checkIn) {
        CheckInVO vo = new CheckInVO();
        BeanUtils.copyProperties(checkIn, vo);
        // 单条查询用户（创建场景只有 1 条，不需要批量优化）
        User user = userMapper.selectById(checkIn.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        return vo;
    }
}
