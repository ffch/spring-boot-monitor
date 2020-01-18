package cn.pomit.boot.monitor.event;

import java.time.Instant;

import cn.pomit.boot.monitor.model.StatusInfo;

/**
 * This event gets emitted when an instance changes its status.
 *
 * @author Johannes Edmeier
 */
@lombok.Data
@lombok.EqualsAndHashCode(callSuper = true)
@lombok.ToString(callSuper = true)
public class InstanceStatusChangedEvent extends InstanceEvent {
	private static final long serialVersionUID = 1L;
	private final StatusInfo statusInfo;

	public InstanceStatusChangedEvent(String instance, long version, StatusInfo statusInfo) {
		this(instance, version, Instant.now(), statusInfo);
	}

	public InstanceStatusChangedEvent(String instance, long version, Instant timestamp, StatusInfo statusInfo) {
		super(instance, version, "STATUS_CHANGED", timestamp);
		this.statusInfo = statusInfo;
	}

	public StatusInfo getStatusInfo() {
		return statusInfo;
	}
}
