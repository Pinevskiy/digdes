package com.digdes.school;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaSchoolStarter {
    static final Pattern HEAD = Pattern.compile("(?i)^(insert\\s+values|update\\s+values|select|delete)\\s*(.*)$");
    static final Pattern WHERE = Pattern.compile("(?i)^where\\s+(.*)$");
    static final List<Field> FIELDS = List.of( Field.of("id", Long.class), Field.of("lastName", String.class),
            Field.of("age", Long.class), Field.of("cost", Double.class), Field.of("active", Boolean.class));
    static final Map<String,Field> FIELDS_MAP = new HashMap<>() {{
        FIELDS.forEach(f -> put(f.getName().toLowerCase(Locale.ROOT), f));
    }};

    List<Map<String,Object>> data = new ArrayList<>();

    Calculator calculator = new Calculator();

    public JavaSchoolStarter() {
    }

    public static Field getFieldByName(String name) {
        return FIELDS_MAP.get(name.toLowerCase(Locale.ROOT));
    }

    //На вход запрос, на выход результат выполнения запроса
    public List<Map<String,Object>> execute(String request) throws Exception {
        Matcher m = HEAD.matcher(request);
        if(m.find()) {
            String cmd = m.group(1).toLowerCase(Locale.ROOT);
            String tail = m.group(2);
            if(cmd.startsWith("insert")) {
                return insert(tail);
            } else if(cmd.startsWith("update")) {
                return update(tail);
            } else if(cmd.startsWith("select")) {
                return select(tail);
            } else if(cmd.startsWith("delete")) {
                return delete(tail);
            }
        }
        throw new Exception("bad request " + request);
    }

    private List<Map<String,Object>> insert(String query) throws Exception {
        Map<String,Object> values = makeMapByValues(query);
        Map<String,Object> map = new HashMap<>();
        setValues(values, map);
        data.add(map);
        return List.of(Collections.unmodifiableMap(map));
    }

    private Map<String,Object> makeMapByValues(String values) throws Exception {
        Map<String,Object> newValues = new HashMap<>();
        String[] ss = values.split("\\s*,\\s*");
        for (String s : ss) {
            String[] sss = s.split("\\s*=\\s*");
            String key = sss[0].substring(1, sss[0].length() - 1);
            String value;
            if (sss[1].startsWith("'") && sss[1].endsWith("'")) {
                value = sss[1].substring(1, sss[1].length() - 1);
                setValue(newValues, key, value);
            }
            else if (sss[1].equals("null")) {
                value = null;
                setValue(newValues, key, null);
            }
            else if (sss[1].equals("true") || sss[1].equals("false")) {
                value = sss[1];
                setValue(newValues, key, Boolean.parseBoolean(value));
            }
            else if (sss[1].contains(".")) {
                value = sss[1];
                setValue(newValues, key, Double.parseDouble(value));
            }
            else {
                value = sss[1];
                setValue(newValues, key, Long.parseLong(value));
            }
        }
        return newValues;
    }

    private List<Map<String,Object>> update(String query) throws Exception {
        String[] ss = query.split("(?i)\\s+where\\s+");
        String where = (ss.length == 1) ? "" : ss[1];
        Map<String,Object> values = makeMapByValues(ss[0]);
        List<Map<String,Object>> found = find(where);
        List<Map<String,Object>> ret = new ArrayList<>();
        found.forEach(f -> {
            setValues(values, f);
            ret.add(Collections.unmodifiableMap(f));
        });
        return ret;
    }

    private List<Map<String,Object>> select(String query) throws Exception {
        String where = extractWhere(query);
        List<Map<String,Object>> found = new ArrayList<>();
        for(Map<String,Object> m : data) {
            if(testRecord(m, where)) {
                found.add(Collections.unmodifiableMap(m));
            }
        }
        return found;
    }

    private List<Map<String,Object>> delete(String query) throws Exception {
        String where = extractWhere(query);
        List<Map<String,Object>> toDel = find(where);
        data.removeAll(toDel);
        return toDel;
    }

    private String extractWhere(String query) {
        Matcher m = WHERE.matcher(query);
        return m.find() ? m.group(1) : "";
    }

    private List<Map<String,Object>> find(String where) throws Exception {
        List<Map<String,Object>> found = new ArrayList<>();
        for(Map<String,Object> m : data) {
            if(testRecord(m, where)) {
                found.add(m);
            }
        }
        return found;
    }

    private boolean testRecord(Map<String,Object> record, String where) throws Exception {
        if (where.isBlank()){
            return true;
        }
        return calculator.calculate(where, record);
    }

    public static void setValue(Map<String,Object> map, String name, Object value) throws Exception {
        Field f = getFieldByName(name);
        if(f == null) {
            throw new Exception("unknown field " + name);
        } else {
            Class<?> fldClass = f.getClazz();
            if(value != null && value.getClass() != fldClass) {
                if(fldClass == Double.class && value instanceof Long) {
                    value = ((Long) value).doubleValue();
                } else {
                    throw new Exception("incompatible types " + value.getClass().getName() + " and " + fldClass.getName());
                }
            }
            map.put(f.getName(), value);
        }
    }

    public static void setValues(Map<String,Object> src, Map<String,Object> dst) {
        src.forEach((k,v) -> {
            if(v == null) {
                dst.remove(k);
            } else {
                dst.put(k, v);
            }
        });
    }

    static class Field {
        private final String name;
        private final Class<?> clazz;

        public Field(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public static Field of(String name, Class<?> clazz) {
            return new Field(name,clazz);
        }

        public String getName() {
            return name;
        }

        public Class<?> getClazz() {
            return clazz;
        }
    }

}