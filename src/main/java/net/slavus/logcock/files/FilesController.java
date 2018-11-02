package net.slavus.logcock.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import net.slavus.logcock.LogcockProperties;
import reactor.core.publisher.Mono;

@Controller
public class FilesController {

  @Autowired
  private LogcockProperties properties;

  @GetMapping("/")
  public String index() {
    return "redirect:/b/";
  }

  @GetMapping("/b/{*webPath}")
  public String browseFolder(@PathVariable String webPath, Model model) {
    String path = properties.getBasePath() + File.separator + webPath;
    model.addAttribute("basePath", properties.getBasePath());
    model.addAttribute("filesInDir", fileList(path));
    model.addAttribute("breadcrumbs", breadcrumbs(webPath));
    return "index";
  }

  private List<String> breadcrumbs(String webPath) {
    String[] paths = webPath.split(File.separator);
    List<String> asList = new ArrayList<>(Arrays.asList(paths));
    asList.add(0, ".");
    return asList;
  }

  @GetMapping("/d/{*webPath}")
  @ResponseBody
  private Mono<Resource> downloadFile(@PathVariable String webPath, Model model) {
    String filePath = properties.getBasePath() + File.separator + webPath;
    return Mono.just(new FileSystemResource(filePath));
  }

  public static List<File> fileList(String path) {
    File dir = new File(path);
    if(!dir.exists()) {
      return new ArrayList<>();
    }
    List<File> files = Arrays.stream(dir.listFiles()).sorted((o1, o2) -> {
      boolean o1dir = o1.isDirectory();
      boolean o2dir = o2.isDirectory();

      if (o1dir == o2dir)
        return 0;
      if (o1dir)
        return -1;
      if (o2dir)
        return 1;
      return 0;
    }).collect(Collectors.toList());
    return dir.exists() ? files : new ArrayList<>();
  }

}
