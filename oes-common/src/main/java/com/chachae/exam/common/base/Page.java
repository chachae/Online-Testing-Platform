package com.chachae.exam.common.base;

/**
 * 分页数据
 *
 * @author chachae
 * @since 2020/2/6 18:06
 */
public class Page {

  /** 默认第一页 */
  private Integer p = 1;

  public Integer getPageNo() {
    return p;
  }

  public void setP(Integer p) {
    this.p = p;
  }
}
