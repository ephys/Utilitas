package nf.fr.ephys.playerproxies.util;

import java.util.HashMap;

public class CaseInsensitiveMap<V> extends HashMap<String, V> {
	@Override
	public V put(String key, V value) {
		return super.put(key, value);
	}

	public V get(String key) {
		return super.get(key.toLowerCase());
	}
	
	@Override
	public V get(Object key) {
		return get((String) key);
	}
}