package com.campus.animal.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.animal.common.Result;
import com.campus.animal.dto.AnimalQueryDTO;
import com.campus.animal.dto.AnimalSaveDTO;
import com.campus.animal.service.AnimalService;
import com.campus.animal.vo.AnimalDetailVO;
import com.campus.animal.vo.AnimalVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 动物档案控制器 — RESTful CRUD + 分页查询 + 类型枚举。
 * <p>
 * 路径前缀：{@code /api/animals}
 * <p>
 * 权限设计：
 * <ul>
 *   <li><b>ADMIN</b>：新增（POST）、修改（PUT）、删除（DELETE）— 通过 {@code @PreAuthorize} 保护</li>
 *   <li><b>所有登录用户</b>：查询（GET）— 只需认证，不限制角色</li>
 * </ul>
 * <p>
 * RESTful 风格：URL 使用名词复数 + HTTP 方法表达操作语义。
 * <pre>
 *   GET    /api/animals       → 列表查询
 *   GET    /api/animals/{id}  → 查看详情
 *   POST   /api/animals       → 新增
 *   PUT    /api/animals/{id}  → 修改
 *   DELETE /api/animals/{id}  → 删除
 * </pre>
 */
@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    /**
     * 新增动物档案（仅 ADMIN）。
     * <p>
     * POST /api/animals
     *
     * @param dto 动物信息
     * @return 创建成功的动物档案
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnimalVO> create(@Valid @RequestBody AnimalSaveDTO dto) {
        return Result.success(animalService.create(dto));
    }

    /**
     * 分页查询动物列表（所有登录用户）。
     * <p>
     * GET /api/animals?name=小花&type=猫&page=1&size=10
     * <p>
     * 分页查询参数通过 URL Query String 传递（非 @RequestBody），
     * 因为 GET 请求通常不携带 Body。AnimalQueryDTO 中的 page/size 有默认值。
     *
     * @param query 查询条件（名称模糊搜索 + 类型筛选 + 分页参数）
     * @return 分页结果（{total, pages, current, records}）
     */
    @GetMapping
    public Result<IPage<AnimalVO>> page(@Valid AnimalQueryDTO query) {
        return Result.success(animalService.page(query));
    }

    /**
     * 获取所有动物类型列表（所有登录用户）。
     * <p>
     * GET /api/animals/types
     * <p>
     * 返回去重后的类型列表，供前端下拉筛选框使用。
     *
     * @return 类型字符串列表（如 ["猫", "狗", "鸟"]）
     */
    @GetMapping("/types")
    public Result<List<String>> getTypes() {
        return Result.success(animalService.getTypes());
    }

    /**
     * 查看动物详情（所有登录用户）。
     * <p>
     * GET /api/animals/{id}
     * <p>
     * 返回动物档案信息 + 打卡时间轴（AnimalDetailVO 继承 AnimalVO 并添加 timeline）。
     *
     * @param id 动物 ID（路径变量）
     * @return 动物详情（含打卡时间轴）
     */
    @GetMapping("/{id}")
    public Result<AnimalDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(animalService.getDetail(id));
    }

    /**
     * 修改动物档案（仅 ADMIN）。
     * <p>
     * PUT /api/animals/{id}
     *
     * @param id  动物 ID（路径变量）
     * @param dto 要修改的字段（全部覆盖）
     * @return 修改后的动物档案
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnimalVO> update(@PathVariable Long id, @Valid @RequestBody AnimalSaveDTO dto) {
        return Result.success(animalService.update(id, dto));
    }

    /**
     * 删除动物档案（仅 ADMIN）。
     * <p>
     * DELETE /api/animals/{id}
     * <p>
     * 级联删除：数据库中 check_in 表的外键设置了 ON DELETE CASCADE，
     * 删除动物时会自动删除其所有打卡记录。
     *
     * @param id 动物 ID
     * @return 成功无数据
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        animalService.delete(id);
        return Result.success();
    }
}
