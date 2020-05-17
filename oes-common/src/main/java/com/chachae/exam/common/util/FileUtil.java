package com.chachae.exam.common.util;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * File工具类，扩展 HuTool 工具包
 *
 * @author chachae
 * @since 2020/1/26 15:18
 */
@Slf4j
public class FileUtil extends cn.hutool.core.io.FileUtil {

  /**
   * 定义GB的计算常量
   */
  private static final int GB = 1024 * 1024 * 1024;
  /**
   * 定义MB的计算常量
   */
  private static final int MB = 1024 * 1024;
  /**
   * 定义KB的计算常量
   */
  private static final int KB = 1024;

  /**
   * 格式化小数
   */
  private static final DecimalFormat DF = new DecimalFormat("0.00");

  /**
   * MultipartFile转File
   */
  public static File toFile(MultipartFile multipartFile) {
    // 获取文件名
    String fileName = multipartFile.getOriginalFilename();
    // 获取文件后缀
    String prefix = StrUtil.DOT + getExtensionName(fileName);
    File file = null;
    try {
      // 用uuid作为文件名，防止生成的临时文件重复
      file = File.createTempFile(IdUtil.simpleUUID(), prefix);
      // MultipartFile to File
      multipartFile.transferTo(file);
    } catch (IOException e) {
      log.error(ExceptionUtil.stacktraceToString(e));
    }
    return file;
  }

  /**
   * 获取文件扩展名，不带 .
   */
  public static String getExtensionName(String filename) {
    if ((filename != null) && (filename.length() > 0)) {
      int dot = filename.lastIndexOf(StrUtil.C_DOT);
      if ((dot > -1) && (dot < (filename.length() - 1))) {
        return filename.substring(dot + 1);
      }
    }
    return filename;
  }

  /**
   * Java文件操作 获取不带扩展名的文件名
   */
  public static String getFileNameNoEx(String filename) {
    if ((filename != null) && (filename.length() > 0)) {
      int dot = filename.lastIndexOf(StrUtil.C_DOT);
      if ((dot > -1) && (dot < (filename.length()))) {
        return filename.substring(0, dot);
      }
    }
    return filename;
  }

  /**
   * 文件大小转换
   */
  public static String getSize(long size) {
    String resultSize;
    if (size / GB >= 1) {
      // 如果当前Byte的值大于等于1GB
      resultSize = DF.format(size / (float) GB) + "GB   ";
    } else if (size / MB >= 1) {
      // 如果当前Byte的值大于等于1MB
      resultSize = DF.format(size / (float) MB) + "MB   ";
    } else if (size / KB >= 1) {
      // 如果当前Byte的值大于等于1KB
      resultSize = DF.format(size / (float) KB) + "KB   ";
    } else {
      resultSize = size + "B   ";
    }
    return resultSize;
  }
}
