package cn.pomit.boot.monitor.event;

import java.time.Instant;

import cn.pomit.boot.monitor.model.Endpoints;

/**
 * This event gets emitted when all instance's endpoints are discovered.
 *
 * @author Johannes Edmeier
 */
@lombok.Data
@lombok.EqualsAndHashCode(callSuper = true)
@lombok.ToString(callSuper = true)
public class InstanceEndpointsDetectedEvent extends InstanceEvent {
	private static final long serialVersionUID = 1L;
	private final Endpoints endpoints;

	public InstanceEndpointsDetectedEvent(String instance, long version, Endpoints endpoints) {
		this(instance, version, Instant.now(), endpoints);
	}

	public InstanceEndpointsDetectedEvent(String instance, long version, Instant timestamp, Endpoints endpoints) {
		super(instance, version, "ENDPOINTS_DETECTED", timestamp);
		this.endpoints = endpoints;
	}
}
