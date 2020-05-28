package net.slavus.logcock.requirejs;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.webjars.RequireJS;

import reactor.core.publisher.Mono;

@Controller
public class RequireJsController {

  @Autowired
  ServerProperties serverProperties;

  @ResponseBody
  @RequestMapping(value = "/webjarsjs", produces = "application/javascript")
  public Mono<String> webjarjs() {
    String contextPath = StringUtils.defaultString(serverProperties.getServlet().getContextPath());
    return Mono.just(RequireJS.getSetupJavaScript(contextPath + "/webjars/"));
  }
}
