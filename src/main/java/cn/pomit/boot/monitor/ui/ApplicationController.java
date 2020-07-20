package cn.pomit.boot.monitor.ui;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebOperation;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.pomit.boot.monitor.event.InstanceEndpointsDetectedEvent;
import cn.pomit.boot.monitor.event.InstanceEvent;
import cn.pomit.boot.monitor.event.InstanceEventLog;
import cn.pomit.boot.monitor.event.InstanceRegisteredEvent;
import cn.pomit.boot.monitor.event.InstanceStatusChangedEvent;
import cn.pomit.boot.monitor.model.Application;
import cn.pomit.boot.monitor.model.Endpoints;
import cn.pomit.boot.monitor.model.Instance;
import cn.pomit.boot.monitor.model.Registration;
import cn.pomit.boot.monitor.model.UserInfo;

@RestController
@RequestMapping("/monitor")
public class ApplicationController {

	private Collection<? extends ExposableEndpoint<?>> endpoints;

	private Application application;

	private UserInfo userInfo;

	private InstanceEventLog instanceEventLog;

	public ApplicationController(Collection<? extends ExposableEndpoint<?>> endpoints, Application application,
			InstanceEventLog instanceEventLog, UserInfo userInfo) {
		this.endpoints = endpoints;
		this.application = application;
		this.instanceEventLog = instanceEventLog;
		this.userInfo = userInfo;
	}

	@ResponseBody
	@GetMapping(path = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Application> applications(@CookieValue(name = "moni_id", required = false) String monitorId,
			HttpServletRequest request, HttpServletResponse response) {
		// 不设置用户名密码直接返回成功
		if (userInfo.getUserName() != null && userInfo.getPassword() != null) {
			if (monitorId == null || !monitorId.equals(userInfo.getUserNameToken())) {
				response.setStatus(401);
				return null;
			}
		}
		initApplicationInfo(request);
		return Arrays.asList(application);
	}

	/**
	 * 组装application信息
	 * 
	 * @param request
	 */
	public void initApplicationInfo(HttpServletRequest request) {
		String normalizedUrl = normalizeRequestUrl(request);
		Map<String, Link> links = new LinkedHashMap<>();
		for (ExposableEndpoint<?> endpoint : this.endpoints) {
			if (endpoint instanceof ExposableWebEndpoint) {
				collectLinks(links, (ExposableWebEndpoint) endpoint, normalizedUrl);
			} else if (endpoint instanceof PathMappedEndpoint) {
				links.put(endpoint.getId(), createLink(normalizedUrl, ((PathMappedEndpoint) endpoint).getRootPath()));
			}
		}
		Endpoints endpoints = new Endpoints(links);
		Registration registration = new Registration(application.getName(), normalizedUrl, normalizedUrl + "/health",
				hostUrl(request), "http-api");
		Instance instance = new Instance(application.getName(), registration, endpoints);
		application.setInstances(Arrays.asList(instance));

		if (!instanceEventLog.isHasInit()) {
			InstanceRegisteredEvent instanceRegisteredEvent = new InstanceRegisteredEvent(application.getName(), 0,
					Instant.now(), registration);
			instanceEventLog.add(instanceRegisteredEvent);
			InstanceStatusChangedEvent instanceStatusChangedEvent = new InstanceStatusChangedEvent(
					application.getName(), 1, Instant.now(), instance.getStatusInfo());
			instanceEventLog.add(instanceStatusChangedEvent);
			InstanceEndpointsDetectedEvent instanceEndpointsDetectedEvent = new InstanceEndpointsDetectedEvent(
					application.getName(), 2, Instant.now(), endpoints);
			instanceEventLog.add(instanceEndpointsDetectedEvent);
			instanceEventLog.setHasInit(true);
		}
	}

	@ResponseBody
	@GetMapping(path = "/applications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public List<Application> applicationsStream(HttpServletRequest request, HttpServletResponse response) {
		initApplicationInfo(request);
		return Arrays.asList(application);
	}

	@ResponseBody
	@GetMapping(path = "instances/events", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<InstanceEvent> events(HttpServletRequest request, HttpServletResponse response) {
		if (!instanceEventLog.isHasInit()) {
			return Collections.emptyList();
		}
		return instanceEventLog.getEventList();
	}

	@ResponseBody
	@GetMapping(path = "instances/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public List<ServerSentEvent<InstanceEvent>> eventsStream(HttpServletRequest request, HttpServletResponse response) {
		if (!instanceEventLog.isHasInit()) {
			return Collections.emptyList();
		}
		List<ServerSentEvent<InstanceEvent>> retList = new ArrayList<>();
		for (InstanceEvent event : instanceEventLog.getEventList()) {
			retList.add(ServerSentEvent.builder(event).build());
		}
		return retList;
	}

	private String normalizeRequestUrl(HttpServletRequest request) {
		String uri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();

		if (!uri.endsWith("/")) {
			uri = uri + "/";
		}

		return uri + "actuator";
	}

	private String hostUrl(HttpServletRequest request) {
		String uri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();

		if (!uri.endsWith("/")) {
			uri = uri + "/";
		}

		return uri;
	}

	private void collectLinks(Map<String, Link> links, ExposableWebEndpoint endpoint, String normalizedUrl) {
		for (WebOperation operation : endpoint.getOperations()) {
			links.put(operation.getId(), createLink(normalizedUrl, operation));
		}
	}

	private Link createLink(String requestUrl, WebOperation operation) {
		return createLink(requestUrl, operation.getRequestPredicate().getPath());
	}

	private Link createLink(String requestUrl, String path) {
		return new Link(requestUrl + (path.startsWith("/") ? path : "/" + path));
	}
}
