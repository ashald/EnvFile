package net.ashald.envfile.utils;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ReadOnceMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {

    private Map<K, V> firstRead;
    private Map<K, V> otherReads;

    private boolean wasRead = false;

    public ReadOnceMap(Map<K, V> first, Map<K, V> rest) {
        firstRead = first;
        otherReads = rest;
    }

    @Override
    public int size() {
        return getSource(false).size();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return getSource(true).entrySet();
    }

    private Map<K, V> getSource(boolean consume) {
        Map<K ,V> source;
        synchronized (this) {
            if (wasRead) {
                source = otherReads;
            } else {
                if (consume) {
                    wasRead = true;
                }
                source = firstRead;
            }
        }
        return source == null ? Collections.emptyMap() : source;
    }
}
