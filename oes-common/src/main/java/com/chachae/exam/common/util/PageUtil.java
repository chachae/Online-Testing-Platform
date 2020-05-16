package com.chachae.exam.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

/**
 * 分页工具
 *
 * @author chachae
 * @since 2019/12/21 11:01
 */
public class PageUtil {

  private PageUtil() {
  }

  /**
   * Page 数据处理，预防redis反序列化报错
   */
  public static <T> Map<String, Object> toPage(IPage<T> page) {
    Map<String, Object> map = Maps.newLinkedHashMap();
    map.put("list", page.getRecords());
    map.put("total", page.getTotal());
    return map;
  }

  /**
   * Page 数据处理，预防redis反序列化报错
   */
  public static <T> Map<String, Object> toPage(T records, long total) {
    Map<String, Object> map = Maps.newLinkedHashMap();
    map.put("list", records);
    map.put("total", total);
    return map;
  }

  /**
   * List<T> 分页
   */
  public static <T> List<T> toPage(long page, long size, List<T> list) {
    long start = (page - 1) * size;
    long end = start + size;
    if (start > list.size()) {
      return Lists.newArrayList();
    } else if (end >= list.size()) {
      return list.subList((int) start, list.size());
    } else {
      return list.subList((int) start, (int) end);
    }
  }
}
