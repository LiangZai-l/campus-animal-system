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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final CheckInMapper checkInMapper;
    private final AnimalMapper animalMapper;
    private final UserMapper userMapper;

    @Override
    public CheckInVO create(CheckInSaveDTO dto) {
        if (animalMapper.selectById(dto.getAnimalId()) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "动物档案不存在");
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setAnimalId(dto.getAnimalId());
        checkIn.setUserId(SecurityUtils.getCurrentUserId());
        checkIn.setContent(dto.getContent());
        checkIn.setImages(dto.getImages());
        checkIn.setMood(dto.getMood());
        checkIn.setLocation(dto.getLocation());

        checkInMapper.insert(checkIn);
        return toVO(checkIn);
    }

    @Override
    public List<CheckInVO> getTimelineByAnimalId(Long animalId) {
        if (animalMapper.selectById(animalId) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "动物档案不存在");
        }

        List<CheckIn> checkIns = checkInMapper.selectList(
                new LambdaQueryWrapper<CheckIn>()
                        .eq(CheckIn::getAnimalId, animalId)
                        .orderByDesc(CheckIn::getCreatedAt));

        return toVOList(checkIns);
    }

    @Override
    public List<CheckInVO> getMyHistory() {
        List<CheckIn> checkIns = checkInMapper.selectList(
                new LambdaQueryWrapper<CheckIn>()
                        .eq(CheckIn::getUserId, SecurityUtils.getCurrentUserId())
                        .orderByDesc(CheckIn::getCreatedAt));

        return toVOList(checkIns);
    }

    private List<CheckInVO> toVOList(List<CheckIn> checkIns) {
        if (checkIns.isEmpty()) {
            return List.of();
        }

        Set<Long> userIds = checkIns.stream()
                .map(CheckIn::getUserId)
                .collect(Collectors.toSet());
        Map<Long, String> usernameMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u.getUsername() != null ? u.getUsername() : "", (a, b) -> a));

        return checkIns.stream().map(ci -> {
            CheckInVO vo = new CheckInVO();
            BeanUtils.copyProperties(ci, vo);
            vo.setUsername(usernameMap.get(ci.getUserId()));
            return vo;
        }).collect(Collectors.toList());
    }

    private CheckInVO toVO(CheckIn checkIn) {
        CheckInVO vo = new CheckInVO();
        BeanUtils.copyProperties(checkIn, vo);
        User user = userMapper.selectById(checkIn.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        return vo;
    }
}
