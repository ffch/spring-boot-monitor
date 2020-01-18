package cn.pomit.boot.monitor.event;

import java.util.ArrayList;
import java.util.List;

@lombok.Data
public class InstanceEventLog {
	private List<InstanceEvent> eventList = new ArrayList<>();
	private boolean hasInit = false;

	public void add(InstanceEvent instanceEvent) {
		eventList.add(instanceEvent);
	}
}
