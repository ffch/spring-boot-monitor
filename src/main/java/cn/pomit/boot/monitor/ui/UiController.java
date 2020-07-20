package cn.pomit.boot.monitor.ui;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.pomit.boot.monitor.model.UserInfo;

@Controller
public class UiController {
	private UserInfo userInfo;

	public UiController() {
	}

	public UiController(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	@GetMapping(path = "/monitor", produces = MediaType.TEXT_HTML_VALUE)
	public String index(@CookieValue(name = "moni_id", required = false) String monitorId) {
		// 不设置用户名密码直接返回成功
		if (userInfo.getUserName() == null || userInfo.getPassword() == null) {
			return "redirect:/monitor/index.html";
		}
		if (monitorId != null && monitorId.equals(userInfo.getUserNameToken())) {
			return "redirect:/monitor/index.html";
		}
		return "redirect:/monitor/login.html";
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

	@RequestMapping(value = "/monitor/login")
	public String setCookies(@RequestParam(name = "username", required = false) String reqUserName,
			@RequestParam(name = "password", required = false) String reqPassword, HttpServletResponse response) {
		// 不设置用户名密码直接返回成功
		if (userInfo.getUserName() == null || userInfo.getPassword() == null) {
			return "redirect:/monitor/index.html";
		}
		if (reqUserName == null || reqPassword == null) {
			return "redirect:/monitor/login.html";
		}
		if (!StringUtils.isEmpty(reqUserName) && !StringUtils.isEmpty(reqPassword)) {
			if (reqUserName.equals(userInfo.getUserName()) && reqPassword.equals(userInfo.getPassword())) {
				Cookie cookie = new Cookie("moni_id", userInfo.getUserNameToken());
				response.addCookie(cookie);
				return "redirect:/monitor/index.html";
			}
		}
		return "redirect:/monitor/login.html";
	}
}
