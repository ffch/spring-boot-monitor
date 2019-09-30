package cn.pomit.boot.monitor.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

/**
 * Represents the info fetched from the info actuator endpoint
 *
 * @author Johannes Edmeier
 */
@lombok.Data
public class Info implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8939503231561398583L;

	private static final Info EMPTY = new Info(Collections.emptyMap());

    private final Map<String, Object> values;

    private Info(Map<String, Object> values) {
        if (values.isEmpty()) {
            this.values = Collections.emptyMap();
        } else {
            this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
        }
    }

    public static Info from(@Nullable Map<String, Object> values) {
        if (values == null || values.isEmpty()) {
            return empty();
        }
        return new Info(values);
    }

    public static Info empty() {
        return EMPTY;
    }

    @JsonAnyGetter
    public Map<String, Object> getValues() {
        return this.values;
    }
}
