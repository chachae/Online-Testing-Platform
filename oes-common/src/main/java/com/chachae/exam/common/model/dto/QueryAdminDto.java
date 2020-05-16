package com.chachae.exam.common.model.dto;

import com.chachae.exam.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chachae
 * @since 2020/5/16 17:33
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class QueryAdminDto extends BaseEntity {

  private String key;

  private Integer academyId;

}