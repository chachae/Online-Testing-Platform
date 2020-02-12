package com.exam.init;

import cn.hutool.core.net.NetUtil;
import cn.hutool.log.Log;
import com.exam.constant.SysConsts;
import com.exam.entity.Paper;
import com.exam.service.PaperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 项目初始化
 *
 * @author yzn
 * @since 2020/2/7 15:41
 */
@Component
public class ProgramInitialization implements ApplicationRunner {

  /** 获取日志系统 */
  private Log log = Log.get();

  @Resource private PaperService paperService;
  @Resource private ConfigurableApplicationContext context;

  @Value("${server.port:8080}")
  private String port;

  @Value("${server.servlet.context-path:}")
  private String contextPath;

  @Value("${spring.profiles.active}")
  private String active;

  @Override
  public void run(ApplicationArguments args) {

    // 系统启动时检查试卷是否正常结束
    log.info("开始检测所有考试是否正常结束");
    List<Paper> papers = this.paperService.list();
    for (Paper paper : papers) {
      if (paper.isEnd() && paper.getPaperState().equals(SysConsts.PAPER.PAPER_STATE_START)) {
        paper.setPaperState(SysConsts.PAPER.PAPER_STATE_END);
        paperService.updateById(paper);
        log.info("试卷:{} 状态被修改:{}", paper.getPaperName(), paper.getPaperState());
      }
    }

    // 项目启动设置
    if (context.isActive()) {
      // 获取本地主机地址
      String localhost = NetUtil.getLocalhostStr();
      // 格式化地址字符串
      String url = String.format("http://%s:%s", localhost, port);
      if (StringUtils.isNotBlank(contextPath)) {
        url += contextPath;
      }
      // 信息提示
      log.info("成功加载 [{}] 级别的项目配置文件", active);
      log.info("[毕业设计 | 基于 JSP 的网上考试系统] 启动完毕");
      log.info("教师 | 学生访问地址：{}", url);
      log.info("管理员访问地址：{}", url + "/admin/login");
    }
  }
}
