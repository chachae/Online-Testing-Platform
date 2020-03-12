package com.chachae.exam.common.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chachae
 * @since 2020/3/12 11:50
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class QueryPaperDto {

    // 开始结束情况 1：开始 0：结束
    private Integer state;

    private String paperType;

    private Integer majorId;

    private Integer teacherId;

    private Integer courseId;

    private String paperName;


}
