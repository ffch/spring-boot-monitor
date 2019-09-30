package cn.pomit.boot.monitor.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebOperation;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.pomit.boot.monitor.model.Application;
import cn.pomit.boot.monitor.model.Endpoints;
import cn.pomit.boot.monitor.model.Instance;
import cn.pomit.boot.monitor.model.Registration;

@RestController
@RequestMapping("/monitor")
public class ApplicationController {
	Collection<? extends ExposableEndpoint<?>> endpoints;
	@Value("${spring.application.name:localhost}")
	private String name;

	public ApplicationController(Collection<? extends ExposableEndpoint<?>> endpoints) {
		this.endpoints = endpoints;
	}

	@ResponseBody
	@GetMapping(path = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Application> applications(HttpServletRequest request,HttpServletResponse response) {
		String normalizedUrl = normalizeRequestUrl(request);
		Map<String, Link> links = new LinkedHashMap<>();
		for (ExposableEndpoint<?> endpoint : this.endpoints) {
			if (endpoint instanceof ExposableWebEndpoint) {
				collectLinks(links, (ExposableWebEndpoint) endpoint, normalizedUrl);
			} else if (endpoint instanceof PathMappedEndpoint) {
				links.put(endpoint.getId(), createLink(normalizedUrl, ((PathMappedEndpoint) endpoint).getRootPath()));
			}
		}

		Application application = new Application();
		application.setName(name);
		Endpoints endpoints = new Endpoints(links);
		Registration registration = new Registration(name, normalizedUrl, normalizedUrl + "/health", hostUrl(request),
				"http-api");
		Instance instance = new Instance(name, registration, endpoints);
		application.setInstances(Arrays.asList(instance));
		return Arrays.asList(application);
	}
	
	@ResponseBody
	@GetMapping(path = "/applications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public List<ServerSentEvent<Application>> applicationsStream(HttpServletRequest request,HttpServletResponse response) {
		String normalizedUrl = normalizeRequestUrl(request);
		Map<String, Link> links = new LinkedHashMap<>();
		for (ExposableEndpoint<?> endpoint : this.endpoints) {
			if (endpoint instanceof ExposableWebEndpoint) {
				collectLinks(links, (ExposableWebEndpoint) endpoint, normalizedUrl);
			} else if (endpoint instanceof PathMappedEndpoint) {
				links.put(endpoint.getId(), createLink(normalizedUrl, ((PathMappedEndpoint) endpoint).getRootPath()));
			}
		}

		Application application = new Application();
		application.setName(name);
		Endpoints endpoints = new Endpoints(links);
		Registration registration = new Registration(name, normalizedUrl, normalizedUrl + "/health", hostUrl(request),
				"http-api");
		Instance instance = new Instance(name, registration, endpoints);
		application.setInstances(Arrays.asList(instance));
		return Arrays.asList(ServerSentEvent.builder(application).build());
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
