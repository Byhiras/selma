/*
 * Copyright 2013  Séven Le Mesle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package fr.xebia.extras.selma.codegen;

import javax.lang.model.element.Element;
import java.util.*;

/**
 * Created by slemesle on 27/03/2014.
 */
public class FieldMap {

    private final Map<String, String> from;
    private final Map<String, String> to;
    private Element element;


    public FieldMap(FieldMap clone) {
        from = new HashMap<String, String>(clone.from);
        to = new HashMap<String, String>(clone.to);
        element = clone.element;
    }

    public FieldMap(Element element) {
        this.element = element;
        from = new HashMap<String, String>();
        to = new HashMap<String, String>();
    }

    public void push(String _from, String _to) {
        from.put(_from, _to);
        to.put(_to, _from);
    }

    public String get(String key) {
        String val = from.get(key);
        if (val == null) {
            val = to.get(key);
        }
        return val;
    }

    public void remove(String field) {
        String val = from.get(field);
        if (val == null) {
            val = to.get(field);
            if (val != null) {
                to.remove(field);
                from.remove(val);
            }
        } else {
            from.remove(field);
            to.remove(val);
        }
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return from.entrySet();
    }

    public List<Field> getStartingWith(String fieldName) {

        List<Field> res = findStartingWith(from, fieldName);
        if (res.isEmpty()) {
            res = findStartingWith(to, fieldName);
        }
        return res;
    }

    private List<Field> findStartingWith(Map<String, String> from, String fieldName) {
        List<Field> res = new ArrayList<Field>();
        for (String key : from.keySet()) {
            if ((key.startsWith(fieldName) && key.contains(fieldName + ".")) ||
                    key.equals(fieldName)) {
                res.add(new Field(key, from.get(key), element));
            }
        }
        return res;
    }
}

class Field {
    public final String originalTo;
    public final String originalFrom;
    public String from;
    public String to;
    public Element element;

    public Field(String from, String to, Element element) {
        this.from = from;
        this.to = to;
        this.originalFrom = from;
        this.originalTo = to;

        this.element = element;
    }

    public void removeDestinationPrefix(String simpleName, String fqcn) {
        to = removePrefixes(to, simpleName, fqcn);
    }


    public void removeSourcePrefix(String simpleName, String fqcn) {
        from = removePrefixes(from, simpleName, fqcn);
    }

    private String removePrefixes(String key, String simpleName, String fqcn) {
        key = key.replace(simpleName + ".", "");
        return key.replace(fqcn + ".", "");
    }

    public boolean hasEmbedded() {
        return from.contains(".") || to.contains(".");
    }

    public boolean sourceEmbedded() {
        return from.contains(".");
    }

    public String[] fromFields() {
        return from.split("\\.");
    }

    public boolean hasEmbeddedSourceAndDestination() {
        return from.contains(".") && to.contains(".");
    }


    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("@Field{ \"");
        sb.append(originalTo).append("\"");
        sb.append(", \"").append(originalFrom).append("\"");
        sb.append('}');
        return sb.toString();
    }

    public String[] toFields() {
        return to.split("\\.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;

        if (originalFrom != null ? !originalFrom.equals(field.originalFrom) : field.originalFrom != null) return false;
        if (originalTo != null ? !originalTo.equals(field.originalTo) : field.originalTo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = originalTo != null ? originalTo.hashCode() : 0;
        result = 31 * result + (originalFrom != null ? originalFrom.hashCode() : 0);
        return result;
    }

    public boolean hasOneFieldMatching(Field field) {
        return to.equals(field.to) || to.equals(field.from) || from.equals(field.from);
    }
}