package com.campus.animal.service;

import com.campus.animal.dto.CheckInSaveDTO;
import com.campus.animal.vo.CheckInVO;

import java.util.List;

public interface CheckInService {
    CheckInVO create(CheckInSaveDTO dto);
    List<CheckInVO> getTimelineByAnimalId(Long animalId);
    List<CheckInVO> getMyHistory();
}
