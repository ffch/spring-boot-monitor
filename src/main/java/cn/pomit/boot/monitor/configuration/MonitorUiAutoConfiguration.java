package cn.pomit.boot.monitor.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;

import cn.pomit.boot.monitor.event.InstanceEventLog;
import cn.pomit.boot.monitor.model.Application;
import cn.pomit.boot.monitor.model.UserInfo;
import cn.pomit.boot.monitor.ui.ApplicationController;
import cn.pomit.boot.monitor.ui.UiController;

@Configuration
public class MonitorUiAutoConfiguration {
	@Value("${spring.application.name:localhost}")
	private String name;
	@Value("${spring.boot.monitor.username:#{null}}")
	private String userName;
	@Value("${spring.boot.monitor.password:#{null}}")
	private String password;
	@Value("${spring.boot.monitor.salt:pomit}")
	private String salt;

	@Bean
	@ConditionalOnMissingBean
	public UiController homeUiController() {
		return new UiController(userInfo());
	}

	@Bean
	public ApplicationController applicationController(WebEndpointsSupplier webEndpointsSupplier,
			ServletEndpointsSupplier servletEndpointsSupplier,
			ControllerEndpointsSupplier controllerEndpointsSupplier) {
		List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
		Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
		allEndpoints.addAll(webEndpoints);
		allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
		allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
		return new ApplicationController(allEndpoints, application(), instanceEventLog(), userInfo());
	}

	@Bean
	public InstanceEventLog instanceEventLog() {
		InstanceEventLog instanceEventLog = new InstanceEventLog();
		return instanceEventLog;
	}

	@Bean
	public Application application() {
		Application application = new Application();
		application.setName(name);
		return application;
	}

	@Bean
	public UserInfo userInfo() {
		UserInfo userInfo = new UserInfo();
		userInfo.setPassword(password);
		userInfo.setUserName(userName);
		userInfo.setSalt(salt);
		if (userName != null) {
			String userNameToken = DigestUtils.md5DigestAsHex((userName + salt).getBytes());
			userInfo.setUserNameToken(userNameToken);
		}
		return userInfo;
	}
}
