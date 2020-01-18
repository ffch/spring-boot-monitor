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

import cn.pomit.boot.monitor.event.InstanceEventLog;
import cn.pomit.boot.monitor.model.Application;
import cn.pomit.boot.monitor.ui.ApplicationController;
import cn.pomit.boot.monitor.ui.UiController;

@Configuration
public class MonitorUiAutoConfiguration {
	@Value("${spring.application.name:localhost}")
	private String name;

	@Bean
	@ConditionalOnMissingBean
	public UiController homeUiController() {
		return new UiController();
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
		return new ApplicationController(allEndpoints, application(), instanceEventLog());
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
}
