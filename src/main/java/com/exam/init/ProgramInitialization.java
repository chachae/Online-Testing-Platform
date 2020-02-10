package com.exam.init;

import cn.hutool.log.Log;
import com.exam.constant.SysConsts;
import com.exam.entity.Paper;
import com.exam.service.PaperService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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

  @Override
  public void run(ApplicationArguments args) {
    // 系统启动时检查试卷是否正常结束
    log.info("开始检测所有考试是否正常结束");
    List<Paper> papers = this.paperService.list();
    for (Paper paper : papers) {
      if (paper.isEnd()) {
        paper.setPaperState(SysConsts.PAPER.PAPER_STATE_END);
        paperService.updateById(paper);
        log.info("试卷:{} 状态被修改:{}", paper.getPaperName(), paper.getPaperState());
      }
    }
  }
}
