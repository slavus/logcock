package net.slavus.logcock;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.WebFilter;

@SpringBootApplication
public class LogcockAppApplication {



  public static void main(String[] args) {
    SpringApplication.run(LogcockAppApplication.class, args);
  }


  @Bean
  public WebFilter contextPathWebFilter(ServerProperties serverProperties) {
      String contextPath = StringUtils.defaultString(serverProperties.getServlet().getContextPath());
      return (exchange, chain) -> {
          ServerHttpRequest request = exchange.getRequest();
          if (request.getURI().getPath().startsWith(contextPath)) {
              return chain.filter(
                  exchange.mutate()
                  .request(request.mutate().contextPath(contextPath).build())
                  .build());
          }
          return chain.filter(exchange);
      };
  }

}
