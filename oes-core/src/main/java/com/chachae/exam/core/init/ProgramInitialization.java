package com.chachae.exam.core.init;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.model.Paper;
import com.chachae.exam.service.PaperService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 项目初始化
 *
 * @author chachae
 * @since 2020/2/7 15:41
 */
@Component
public class ProgramInitialization implements ApplicationRunner {

  /**
   * 获取日志系统
   */
  private Log log = Log.get();

  @Resource
  private PaperService paperService;
  @Resource
  private ConfigurableApplicationContext context;

  @Value("${server.port:8080}")
  private String port;

  @Value("${server.servlet.context-path:/}")
  private String contextPath;

  @Value("${spring.profiles.active}")
  private String active;

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
      String url = String.format("http://%s:%s", NetUtil.getLocalhostStr(), port);
      if (StrUtil.isNotBlank(contextPath)) {
        url += contextPath;
      }
      // 信息提示
      log.info("成功加载 [{}] 级别的项目配置文件", active);
      log.info("[基于 spring-boot 2.X 的在线考试系统] 完成启动");
      log.info("访问地址：{}", url);
    }
  }
}
