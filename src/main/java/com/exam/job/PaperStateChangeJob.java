package com.exam.job;

import cn.hutool.log.Log;
import com.exam.constant.SysConsts;
import com.exam.entity.Paper;
import com.exam.mapper.PaperMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 定时任务
 *
 * @author yzn
 * @date 2020/2/3
 */
@Component
public class PaperStateChangeJob {

  private Log log = Log.get();

  @Resource private PaperMapper paperMapper;

  /*
   * 2020-02-09 23:30:00 2020-02-10 00:00:00 2020-02-10 00:30:00 2020-02-10 01:00:00 2020-02-10
   * 01:30:00 2020-02-10 02:00:00 2020-02-10 02:30:00 2020-02-10 03:00:00 2020-02-10 03:30:00
   * 2020-02-10 04:00:00
   */

  /** 定时任务，给结束考试的试卷进行状态改变，每 30 分钟执行一次 */
  @Scheduled(cron = "0 0,30 * * * ? ")
  public void run() {
    List<Paper> paperList = paperMapper.selectList(null);
    for (Paper paper : paperList) {
      if (paper.isEnd()) {
        paper.setPaperState(SysConsts.PAPER.PAPER_STATE_END);
        paperMapper.updateById(paper);
        log.info("试卷:{} 状态被修改:{}", paper.getPaperName(), paper.getPaperState());
      }
    }
  }
}
