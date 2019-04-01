package net.slavus.logcock.tail;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

@Configuration
public class WebSocketConfig {



  @Autowired
  private FileTailControlerWSHandler tailWebSocketHandler;

  @Bean
  public HandlerMapping webSocketHandlerMapping() {
      Map<String, WebSocketHandler> map = new HashMap<>();
      map.put("/tail/**", tailWebSocketHandler);

      SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
      handlerMapping.setOrder(1);
      handlerMapping.setUrlMap(map);
      return handlerMapping;
  }

  @Bean
  public WebSocketHandlerAdapter handlerAdapter() {
      return new WebSocketHandlerAdapter();
  }

}