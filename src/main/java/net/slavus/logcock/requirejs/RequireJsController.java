package net.slavus.logcock.requirejs;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.webjars.RequireJS;

import reactor.core.publisher.Mono;

@Controller
public class RequireJsController {

  @ResponseBody
  @RequestMapping(value = "/webjarsjs", produces = "application/javascript")
  public Mono<String> webjarjs() {
    return Mono.just(RequireJS.getSetupJavaScript("/webjars/"));
  }
}
