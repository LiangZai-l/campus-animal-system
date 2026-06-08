package com.campus.animal.controller;

import com.campus.animal.common.Result;
import com.campus.animal.dto.CheckInSaveDTO;
import com.campus.animal.service.CheckInService;
import com.campus.animal.vo.CheckInVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkins")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @PostMapping
    public Result<CheckInVO> create(@Valid @RequestBody CheckInSaveDTO dto) {
        return Result.success(checkInService.create(dto));
    }

    @GetMapping("/animal/{animalId}")
    public Result<List<CheckInVO>> getTimeline(@PathVariable Long animalId) {
        return Result.success(checkInService.getTimelineByAnimalId(animalId));
    }

    @GetMapping("/my")
    public Result<List<CheckInVO>> getMyHistory() {
        return Result.success(checkInService.getMyHistory());
    }
}
