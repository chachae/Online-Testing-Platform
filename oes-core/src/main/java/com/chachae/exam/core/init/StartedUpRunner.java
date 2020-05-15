package com.chachae.exam.core.init;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.model.Paper;
import com.chachae.exam.service.PaperService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 项目初始化
 *
 * @author chachae
 * @since 2020/2/7 15:41
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartedUpRunner implements ApplicationRunner {

  private final PaperService paperService;
  private final ConfigurableApplicationContext context;
  private final Environment environment;

  @Value("${server.servlet.context-path:/}")
  private String contextPath;

  @Override
  public void run(ApplicationArguments args) {

    // 系统启动时检查试卷是否正常结束
    log.info("开始检测所有考试是否正常结束");
    List<Paper> papers = this.paperService.list();
    for (Paper paper : papers) {
      // 只对正式考试做检测
      boolean type = paper.getPaperType().equals(SysConsts.Paper.PAPER_TYPE_FORMAL);
      // 只保留未开始状态的检测
      boolean state = paper.getPaperState().equals(SysConsts.Paper.PAPER_STATE_START);
      if (type && state && paper.isEnd()) {
        paper.setPaperState(SysConsts.Paper.PAPER_STATE_END);
        paperService.updateById(paper);
        log.info("试卷:{} 状态被修改:{}", paper.getPaperName(), paper.getPaperState());
      }
    }

    // 项目启动设置
    if (context.isActive()) {
      // 格式化地址字符串
      String url = String.format("http://%s:%s", NetUtil.getLocalhostStr(),
          environment.getProperty("server.port"));
      if (StrUtil.isNotBlank(contextPath)) {
        url += contextPath;
      }
      String banner = "-----------------------------------------\n" +
          "服务启动成功，当前时间：" + LocalDateTime.now() + "\n" +
          "服务名称：" + environment.getProperty("spring.application.name") + "\n" +
          "服务访问地址：" + url + "\n" +
          "-----------------------------------------";
      System.out.println(banner);
    }
  }
}
