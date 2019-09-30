package cn.pomit.boot.monitor.ui;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UiController {

	@GetMapping(path = "/monitor", produces = MediaType.TEXT_HTML_VALUE)
	public String index() {
		return "redirect:monitor/index.html";
	}

	@ResponseBody
	@GetMapping(path = "/monitor/context")
	public String context(HttpServletRequest request) {
		String uri = request.getContextPath();

		if (!uri.endsWith("/")) {
			uri = uri + "/";
		}
		return uri;
	}
}
