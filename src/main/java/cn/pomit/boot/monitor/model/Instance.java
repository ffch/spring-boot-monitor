
package cn.pomit.boot.monitor.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * The aggregate representing a registered application instance.
 *
 * @author Johannes Edmeier
 */
@lombok.Data
public class Instance implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5476210649123305653L;
	private final String id;
	private final long version;
	private final Registration registration;
	private final boolean registered;
	private final StatusInfo statusInfo;
	private final Instant statusTimestamp;
	private final Info info;
	private final Endpoints endpoints;
	private final String buildVersion;
	private final Tags tags;

	private Instance(String id) {
		this(id, -1L, null, false, StatusInfo.ofUnknown(), Instant.EPOCH, Info.empty(), Endpoints.empty(), null,
				Tags.empty());
	}

	public Instance(String id, @Nullable Registration registration, Endpoints endpoints) {
		this.id = id;
		this.version = -1L;
		this.registration = registration;
		this.registered = true;
		this.statusInfo = StatusInfo.valueOf(StatusInfo.STATUS_UP);
		this.statusTimestamp = Instant.now();
		this.info = Info.empty();
		this.endpoints = endpoints;
		this.buildVersion = null;
		this.tags = Tags.empty();
	}
	
	private Instance(String id, long version, @Nullable Registration registration, boolean registered,
			StatusInfo statusInfo, Instant statusTimestamp, Info info, Endpoints endpoints,
			@Nullable String buildVersion, Tags tags) {
		Assert.notNull(id, "'id' must not be null");
		Assert.notNull(endpoints, "'endpoints' must not be null");
		Assert.notNull(info, "'info' must not be null");
		Assert.notNull(statusInfo, "'statusInfo' must not be null");
		this.id = id;
		this.version = version;
		this.registration = registration;
		this.registered = registered;
		this.statusInfo = statusInfo;
		this.statusTimestamp = statusTimestamp;
		this.info = info;
		this.endpoints = registered && registration != null
				? endpoints.withEndpoint(Endpoint.HEALTH, registration.getHealthUrl()) : endpoints;
		this.buildVersion = buildVersion;
		this.tags = tags;
	}

	public static Instance create(String id) {
		Assert.notNull(id, "'id' must not be null");
		return new Instance(id);
	}

	public boolean isRegistered() {
		return this.registered;
	}

	public Registration getRegistration() {
		if (this.registration == null) {
			throw new IllegalStateException("Application '" + this.id + "' has no valid registration.");
		}
		return this.registration;
	}

	@Nullable
	@SafeVarargs
	private final String updateBuildVersion(Map<String, ?>... sources) {
		return Arrays.stream(sources).map(s -> from(s)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public static String from(Map<String, ?> map) {
		if (map.isEmpty()) {
			return null;
		}

		Object build = map.get("build");
		if (build instanceof Map) {
			Object version = ((Map<?, ?>) build).get("version");
			if (version instanceof String) {
				return (String) version;
			}
		}

		Object version = map.get("build.version");
		if (version instanceof String) {
			return (String) version;
		}

		version = map.get("version");
		if (version instanceof String) {
			return (String) version;
		}
		return null;
	}
}
