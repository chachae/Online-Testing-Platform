package com.exam.controller;

import com.exam.entity.Academy;
import com.exam.service.AcademyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chachae
 * @since 2020/2/27 16:47
 */
@Controller
@RequestMapping("/academy")
public class AcademyController {

  @Resource private AcademyService academyService;

  @ResponseBody
  @GetMapping
  public List<Academy> list() {
    return this.academyService.list();
  }
}
