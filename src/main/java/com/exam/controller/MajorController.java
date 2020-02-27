package com.exam.controller;

import com.exam.entity.Major;
import com.exam.service.MajorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chachae
 * @since 2020/2/27 16:02
 */
@RestController
@RequestMapping("/major")
public class MajorController {

  @Resource private MajorService majorService;

  @GetMapping
  public List<Major> majorList() {
    return this.majorService.list();
  }

  @GetMapping("/academy/{academyId}")
  public List<Major> majorList(@PathVariable Integer academyId) {
    return this.majorService.listByAcademyId(academyId);
  }
}
