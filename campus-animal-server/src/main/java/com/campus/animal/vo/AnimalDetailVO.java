package com.campus.animal.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnimalDetailVO extends AnimalVO {
    private List<CheckInVO> timeline;
}
