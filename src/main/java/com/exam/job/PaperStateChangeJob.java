package com.exam.job;

import com.exam.constant.SysConsts;
import com.exam.entity.Paper;
import com.exam.mapper.PaperMapper;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class PaperStateChangeJob {

  @Resource private PaperMapper paperMapper;

  /*
   * 2020-02-09 23:30:00 2020-02-10 00:00:00 2020-02-10 00:30:00 2020-02-10 01:00:00 2020-02-10
   * 01:30:00 2020-02-10 02:00:00 2020-02-10 02:30:00 2020-02-10 03:00:00 2020-02-10 03:30:00
   * 2020-02-10 04:00:00
   */

  /** 定时任务，给结束考试的试卷进行状态改变，每 30 分钟执行一次 */
  @Scheduled(cron = "0 0,30 * * * ? ")
  public void run() {
    log.info("执行考试状态检查任务......");
    List<Paper> paperList = paperMapper.selectList(null);
    for (Paper paper : paperList) {
      // 改变所有考试时间已过期且状态仍为未开始的试卷状态（更改为"已结束"）
      if (paper.isEnd() && paper.getPaperState().equals(SysConsts.PAPER.PAPER_STATE_START)) {
        paper.setPaperState(SysConsts.PAPER.PAPER_STATE_END);
        paperMapper.updateById(paper);
        log.info("试卷:[{}] 状态被修改:[{}]", paper.getPaperName(), paper.getPaperState());
      }
    }
  }
}
