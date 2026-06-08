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

@Service
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService {

    private final AnimalMapper animalMapper;
    private final CheckInMapper checkInMapper;
    private final UserMapper userMapper;

    @Override
    public AnimalVO create(AnimalSaveDTO dto) {
        Animal animal = new Animal();
        animal.setName(dto.getName());
        animal.setType(dto.getType());
        animal.setArea(dto.getArea());
        animal.setDescription(dto.getDescription());
        animal.setCoverImage(dto.getCoverImage());
        animal.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        animal.setCreatedBy(SecurityUtils.getCurrentUserId());

        animalMapper.insert(animal);
        return toVO(animal);
    }

    @Override
    public AnimalVO update(Long id, AnimalSaveDTO dto) {
        Animal animal = animalMapper.selectById(id);
        if (animal == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "动物档案不存在");
        }

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

    @Override
    public void delete(Long id) {
        Animal animal = animalMapper.selectById(id);
        if (animal == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "动物档案不存在");
        }
        animalMapper.deleteById(id);
    }

    @Override
    public IPage<AnimalVO> page(AnimalQueryDTO query) {
        LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Animal::getName, query.getName());
        }
        if (StringUtils.hasText(query.getType())) {
            wrapper.eq(Animal::getType, query.getType());
        }
        wrapper.orderByDesc(Animal::getCreatedAt);

        Page<Animal> page = new Page<>(query.getPage(), query.getSize());
        Page<Animal> result = animalMapper.selectPage(page, wrapper);

        List<AnimalVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        Page<AnimalVO> voPage = new Page<>(query.getPage(), query.getSize(), result.getTotal());
        voPage.setRecords(records);
        return voPage;
    }

    @Override
    public AnimalDetailVO getDetail(Long id) {
        Animal animal = animalMapper.selectById(id);
        if (animal == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "动物档案不存在");
        }

        AnimalDetailVO detail = new AnimalDetailVO();
        BeanUtils.copyProperties(animal, detail);

        List<CheckIn> checkIns = checkInMapper.selectList(
                new LambdaQueryWrapper<CheckIn>()
                        .eq(CheckIn::getAnimalId, id)
                        .orderByDesc(CheckIn::getCreatedAt));

        if (!checkIns.isEmpty()) {
            Set<Long> userIds = checkIns.stream()
                    .map(CheckIn::getUserId)
                    .collect(Collectors.toSet());
            Map<Long, String> usernameMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u.getUsername() != null ? u.getUsername() : "", (a, b) -> a));

            List<CheckInVO> timeline = checkIns.stream().map(ci -> {
                CheckInVO vo = new CheckInVO();
                BeanUtils.copyProperties(ci, vo);
                vo.setUsername(usernameMap.get(ci.getUserId()));
                return vo;
            }).collect(Collectors.toList());
            detail.setTimeline(timeline);
        } else {
            detail.setTimeline(List.of());
        }

        return detail;
    }

    @Override
    public List<String> getTypes() {
        return animalMapper.selectList(null).stream()
                .map(Animal::getType)
                .distinct()
                .collect(Collectors.toList());
    }

    private AnimalVO toVO(Animal animal) {
        AnimalVO vo = new AnimalVO();
        BeanUtils.copyProperties(animal, vo);
        return vo;
    }
}
