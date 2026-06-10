package com.campus.animal.controller;

import com.campus.animal.common.Result;
import com.campus.animal.dto.CheckInSaveDTO;
import com.campus.animal.service.CheckInService;
import com.campus.animal.vo.CheckInVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 打卡动态控制器 — 发布打卡 + 查看时间轴 + 查看个人历史。
 * <p>
 * 路径前缀：{@code /api/checkins}
 * <p>
 * 所有接口都需要认证（登录用户才能操作）。
 * 权限策略：普通用户可以发布打卡和查看打卡历史，无需 ADMIN 角色。
 */
@RestController
@RequestMapping("/api/checkins")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    /**
     * 发布打卡。
     * <p>
     * POST /api/checkins（需认证）
     * <p>
     * animalId 由前端通过下拉选择动物后传入，
     * userId 在 Service 层从 SecurityContext 获取（不从 DTO 传入，防伪造）。
     *
     * @param dto 打卡内容
     * @return 创建成功的打卡记录（含发布者用户名）
     */
    @PostMapping
    public Result<CheckInVO> create(@Valid @RequestBody CheckInSaveDTO dto) {
        return Result.success(checkInService.create(dto));
    }

    /**
     * 查询某动物的打卡时间轴（按时间倒序）。
     * <p>
     * GET /api/checkins/animal/{animalId}（需认证）
     *
     * @param animalId 动物 ID
     * @return 打卡记录列表（每条含发布者用户名、心情、地点）
     */
    @GetMapping("/animal/{animalId}")
    public Result<List<CheckInVO>> getTimeline(@PathVariable Long animalId) {
        return Result.success(checkInService.getTimelineByAnimalId(animalId));
    }

    /**
     * 查询当前用户的打卡历史。
     * <p>
     * GET /api/checkins/my（需认证）
     * <p>
     * 不需要传 userId，Service 层从 SecurityContext 获取当前用户。
     * 用户只能看到自己的打卡记录。
     *
     * @return 当前用户的打卡记录列表（时间倒序）
     */
    @GetMapping("/my")
    public Result<List<CheckInVO>> getMyHistory() {
        return Result.success(checkInService.getMyHistory());
    }

    /**
     * 删除当前用户的一条打卡记录。
     * <p>
     * DELETE /api/checkins/{id}（需认证）
     * <p>
     * 只能删除自己的打卡记录，不能删除他人的。
     *
     * @param id 打卡记录 ID
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        checkInService.deleteMyCheckIn(id);
        return Result.success();
    }

    /**
     * 管理员删除任意打卡记录。
     * <p>
     * DELETE /api/checkins/admin/{id}（需 ADMIN 角色）
     * <p>
     * 管理员可在动物详情页删除不当打卡内容，无需所有权校验。
     *
     * @param id 打卡记录 ID
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteByAdmin(@PathVariable Long id) {
        checkInService.deleteByAdmin(id);
        return Result.success();
    }

    /**
     * 查询当日打卡最多的动物及其位置路径。
     * <p>
     * GET /api/checkins/today-top（需认证）
     *
     * @return 包含动物名称、打卡次数、位置路径列表
     */
    @GetMapping("/today-top")
    public Result<?> getTodayTop() {
        return Result.success(checkInService.getTodayTopAnimal());
    }
}
