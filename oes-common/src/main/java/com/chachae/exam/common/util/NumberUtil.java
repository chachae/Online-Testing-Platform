package com.chachae.exam.common.util;

import cn.hutool.core.util.StrUtil;

/**
 * 拓展 HuTool NumberUtil
 *
 * @author chachae
 * @since 2020/2/6 21:24
 */
public class NumberUtil extends cn.hutool.core.util.NumberUtil {

  public static Integer strToInteger(String s) {
    return StrUtil.isNotEmpty(s) ? Integer.parseInt(s) : 0;
  }

  public static Double strToDouble(String s) {
    return StrUtil.isNotEmpty(s) ? Double.parseDouble(s) : 0.0;
  }
}
