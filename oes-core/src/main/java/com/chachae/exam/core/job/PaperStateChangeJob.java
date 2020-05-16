package com.chachae.exam.core.job;

import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.dao.PaperDAO;
import com.chachae.exam.common.model.Paper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 定时任务
 *
 * @author chachae
 * @date 2020/2/3
 */
@Slf4j
@Component
public class PaperStateChangeJob {

  @Resource
  private PaperDAO paperDAO;

  /* 执行情况如下
  2020-02-16 21:45:00
  2020-02-16 22:00:00
  2020-02-16 22:15:00
  2020-02-16 22:30:00
  2020-02-16 22:45:00
  2020-02-16 23:00:00
  2020-02-16 23:15:00
  2020-02-16 23:30:00
  2020-02-16 23:45:00
  2020-02-17 00:00:00
     */

  /**
   * 定时任务，给结束考试的试卷进行状态改变，每 15 分钟执行一次
   */
  @Scheduled(cron = "0 0/15 * * * ? ")
  public void run() {
    log.info("执行考试状态检查任务");
    List<Paper> paperList = paperDAO.selectList(null);
    for (Paper paper : paperList) {
      // 改变所有考试时间已过期且状态仍为未开始的试卷状态（更改为"已结束"）
      // 只对正式考试做检测
      boolean type = paper.getPaperType().equals(SysConsts.Paper.PAPER_TYPE_FORMAL);
      // 只保留未开始状态的检测
      boolean state = paper.getPaperState().equals(SysConsts.Paper.PAPER_STATE_START);
      if (type && state && paper.isEnd()) {
        paper.setPaperState(SysConsts.Paper.PAPER_STATE_END);
        paper.setBeginTime(null);
        paper.setEndTime(null);
        paperDAO.updateById(paper);
        log.info("试卷:[{}] 状态被修改:[{}]", paper.getPaperName(), paper.getPaperState());
      }
    }
  }
}
