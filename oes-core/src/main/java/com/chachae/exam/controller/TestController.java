package com.chachae.exam.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.http.HttpUtil;
import com.chachae.exam.service.ScoreService;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chachae
 * @since 2020/5/19 21:27
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {

  private final ScoreService scoreService;

}
