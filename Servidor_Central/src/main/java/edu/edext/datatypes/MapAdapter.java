package edu.edext.datatypes;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapAdapter extends XmlAdapter<MapAdapter.MapWrapper, Map<String, Integer>> {

    public static class MapWrapper {
        public List<MapEntry> entry = new ArrayList<>();
    }

    public static class MapEntry {
        public String key;
        public Integer value;
    }

    @Override
    public Map<String, Integer> unmarshal(MapWrapper v) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        if (v != null && v.entry != null) {
            for (MapEntry e : v.entry) {
                map.put(e.key, e.value);
            }
        }
        return map;
    }

    @Override
    public MapWrapper marshal(Map<String, Integer> v) throws Exception {
        if (v == null) {
            return null;
        }
        MapWrapper wrapper = new MapWrapper();
        for (Map.Entry<String, Integer> e : v.entrySet()) {
            MapEntry entry = new MapEntry();
            entry.key = e.getKey();
            entry.value = e.getValue();
            wrapper.entry.add(entry);
        }
        return wrapper;
    }
}