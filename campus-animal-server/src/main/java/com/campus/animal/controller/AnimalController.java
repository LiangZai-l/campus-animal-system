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

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnimalVO> create(@Valid @RequestBody AnimalSaveDTO dto) {
        return Result.success(animalService.create(dto));
    }

    @GetMapping
    public Result<IPage<AnimalVO>> page(@Valid AnimalQueryDTO query) {
        return Result.success(animalService.page(query));
    }

    @GetMapping("/types")
    public Result<List<String>> getTypes() {
        return Result.success(animalService.getTypes());
    }

    @GetMapping("/{id}")
    public Result<AnimalDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(animalService.getDetail(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnimalVO> update(@PathVariable Long id, @Valid @RequestBody AnimalSaveDTO dto) {
        return Result.success(animalService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        animalService.delete(id);
        return Result.success();
    }
}
