package cn.pomit.boot.monitor.model;

import static java.util.stream.Collectors.toMap;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.lang.Nullable;

@lombok.EqualsAndHashCode
@lombok.ToString
public class Endpoints implements Iterable<Endpoint>, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5621206253990279887L;
	private final Map<String, Endpoint> endpoints;
    private static final Endpoints EMPTY = new Endpoints(Collections.emptyList());

    private Endpoints(Collection<Endpoint> endpoints) {
        if (endpoints.isEmpty()) {
            this.endpoints = Collections.emptyMap();
        } else {
            this.endpoints = endpoints.stream().collect(toMap(Endpoint::getId, Function.identity()));
        }
    }
    
    public Endpoints(Map<String, Link> links) {
        if (links.isEmpty()) {
            this.endpoints = Collections.emptyMap();
        } else {
    		this.endpoints = new HashMap<>();
        	links.forEach((k,v) ->{
        		Endpoint endpoint = new Endpoint(k, v.getHref());
        		this.endpoints.put(k, endpoint);
            });
        }
    }

    public Optional<Endpoint> get(String id) {
        return Optional.ofNullable(this.endpoints.get(id));
    }

    public boolean isPresent(String id) {
        return this.endpoints.containsKey(id);
    }

    @Override
    public Iterator<Endpoint> iterator() {
        return new UnmodifiableIterator<>(this.endpoints.values().iterator());
    }

    public static Endpoints empty() {
        return EMPTY;
    }

    public static Endpoints single(String id, String url) {
        return new Endpoints(Collections.singletonList(Endpoint.of(id, url)));
    }

    public static Endpoints of(@Nullable Collection<Endpoint> endpoints) {
        if (endpoints == null || endpoints.isEmpty()) {
            return empty();
        }
        return new Endpoints(endpoints);
    }

    public Endpoints withEndpoint(String id, String url) {
        Endpoint endpoint = Endpoint.of(id, url);
        HashMap<String, Endpoint> newEndpoints = new HashMap<>(this.endpoints);
        newEndpoints.put(endpoint.getId(), endpoint);
        return new Endpoints(newEndpoints.values());
    }

    public Stream<Endpoint> stream() {
        return this.endpoints.values().stream();
    }

    private static class UnmodifiableIterator<T> implements Iterator<T> {
        private final Iterator<T> delegate;

        private UnmodifiableIterator(Iterator<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override
        public T next() {
            return this.delegate.next();
        }
    }
}
