package cn.pomit.boot.monitor.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@lombok.Data
public class Application {
	private String name;
	private String buildVersion;
	private String status = StatusInfo.STATUS_UP;
	private Instant statusTimestamp = Instant.now();
	private List<Instance> instances = new ArrayList<>();
}
