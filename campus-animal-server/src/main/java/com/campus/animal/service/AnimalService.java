package com.campus.animal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.animal.dto.AnimalQueryDTO;
import com.campus.animal.dto.AnimalSaveDTO;
import com.campus.animal.vo.AnimalDetailVO;
import com.campus.animal.vo.AnimalVO;

import java.util.List;

public interface AnimalService {
    AnimalVO create(AnimalSaveDTO dto);
    AnimalVO update(Long id, AnimalSaveDTO dto);
    void delete(Long id);
    IPage<AnimalVO> page(AnimalQueryDTO query);
    AnimalDetailVO getDetail(Long id);
    List<String> getTypes();
}
