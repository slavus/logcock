package net.slavus.logcock.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.slavus.logcock.LogcockProperties;
import reactor.core.publisher.Mono;

/**
 * @author slavus
 *
 */
@Controller
public class FilesController {

  @Autowired
  private LogcockProperties properties;

  @Autowired
  private ServerProperties serverProperties;

  @Autowired
  private MaskedDownloadService secretDownloadsService;

  @Autowired
  Files files;

  @GetMapping("/")
  public String index() {
    return "redirect:/b/";
  }

  @GetMapping("/b")
  public String browseFolder(Model model) {
    model.addAttribute("basePath", "/");
    model.addAttribute("baseId", "");
    model.addAttribute("filesInDir", properties.getFolders());
    model.addAttribute("breadcrumbs", new ArrayList<>());
    return "index";
  }

  @GetMapping("/b/{id}/{*webPath}")
  public String browseFolder(@PathVariable Integer id, @PathVariable String webPath, Model model) {
    String basePath = properties.getFolders().get(id).getBasePath();

    String path = basePath + File.separator + webPath;
    model.addAttribute("basePath", basePath);
    model.addAttribute("baseId", id);
    model.addAttribute("filesInDir", fileList(path));
    model.addAttribute("breadcrumbs", breadcrumbs(path, basePath));
    return "index";
  }


  @ResponseBody
  @GetMapping(path = "/b/{id}/{*webPath}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public List<String> jsonBrowseFolder(@PathVariable Integer id, @PathVariable String webPath, Model model, ServerHttpRequest req) {
    String basePath = properties.getFolders().get(id).getBasePath();

    String scheme = req.getURI().getScheme();
    String host = req.getHeaders().getHost().toString();        // includes server name and server port

    String serverPath = scheme + "://" + host + StringUtils.defaultString(serverProperties.getServlet().getContextPath());

    String path = basePath + File.separator + webPath;
    return fileList(path)
        .stream().map(f->serverPath + "/d/" + id +  files.linkPath(f, basePath))
        .collect(Collectors.toList());
  }


  @GetMapping("/d/{id}/{*webPath}")
  @ResponseBody
  private Mono<ResponseEntity<Resource>> downloadFile(@PathVariable Integer id, @PathVariable String webPath, Model model) {
    String basePath = properties.getFolders().get(id).getBasePath();
    String filePath = basePath + File.separator + webPath;
    FileSystemResource fileSystemResource = new FileSystemResource(filePath);
    return Mono.just(ResponseEntity
        .ok()
        .header("Content-Type", "application/octet-stream")
        .header("Content-Disposition", "attachment; filename=" + fileSystemResource.getFilename())
        .body(fileSystemResource)
       );
  }

  @GetMapping("/md/{id}/{*webPath}")
  @ResponseBody
  private Mono<ResponseEntity<?>> downloadSplitFile(@PathVariable Integer id, @PathVariable String webPath, @RequestParam(defaultValue = "-1") Integer part, Model model) {
    String basePath = properties.getFolders().get(id).getBasePath();
    String filePath = basePath + File.separator + webPath;
    if(part ==-1) {
      MaskedDownloadInfo downloadInfo = secretDownloadsService.maskedDownloadInfo(filePath);
      return Mono.just( ResponseEntity.ok(downloadInfo));
    }

    MaskedDownload maskedDownload = secretDownloadsService.maskedDownload(filePath, part);
    return Mono.just(ResponseEntity.ok(maskedDownload));
  }


  private List<File> breadcrumbs(String path, String basePath) {
    final List<File> crumbs = new ArrayList<>();
    File f = new File(path);
    while(StringUtils.startsWith(f.getAbsolutePath(), basePath)) {
      crumbs.add(f);
      f = f.getParentFile();
    }
    Collections.reverse(crumbs);
    return crumbs;
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
