package apm.admin.init;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author chachae
 * @since 2020/04/25
 */
@Component
public class StartedUpRunner implements ApplicationRunner {

  private final ConfigurableApplicationContext context;
  private final Environment environment;

  @Autowired
  public StartedUpRunner(ConfigurableApplicationContext context, Environment environment) {
    this.context = context;
    this.environment = environment;
  }

  @Override
  public void run(ApplicationArguments args) {
    // 项目启动设置
    if (context.isActive()) {
      String banner = "-----------------------------------------\n" +
          "服务启动成功，当前时间：" + LocalDateTime.now() + "\n" +
          "服务名称：" + environment.getProperty("spring.application.name") + "\n" +
          "服务端口：" + environment.getProperty("server.port") + "\n" +
          "-----------------------------------------";
      System.out.println(banner);
    }
  }
}
