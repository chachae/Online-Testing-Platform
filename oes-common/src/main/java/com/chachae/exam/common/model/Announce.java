package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 公告实体
 *
 * @author chachae
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Announce {

  @TableId
  private Integer id;

  /**
   * 公告标题
   */
  @NotBlank(message = "公告标题{required}")
  private String title;

  /**
   * 公告内容
   */
  @NotBlank(message = "公告内容{required}")
  private String content;

  /**
   * 作者id
   */
  private String authorId;

  /**
   * 作者身份id：教师_3,管理员_1
   */
  private Integer roleId;

  /**
   * 作者名
   */
  private String authorName;

  /**
   * 公告创建时间
   */
  private Date createTime;
}
