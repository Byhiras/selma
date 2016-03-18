/*
 * Copyright 2013 Xebia and Séven Le Mesle
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


import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.*;

/**
 * Conveniance annotation wrapper class to retrieve easily annotation parameters
 *
 * User: slemesle
 * Date: 25/11/2013
 * Time: 13:40
 */
public class AnnotationWrapper {
    private final AnnotationMirror annotationMirror;
    private final Element annotatedElement;
    private final HashMap<String, AnnotationValue> map;
    private final MapperGeneratorContext context;


    public AnnotationWrapper(MapperGeneratorContext context, AnnotationMirror annotationMirror, Element annotatedElement) {
        this.annotationMirror = annotationMirror;
        this.annotatedElement = annotatedElement;
        this.map = new HashMap<String, AnnotationValue>();
        this.context = context;

        Map<? extends ExecutableElement, ? extends AnnotationValue> values = context.elements.getElementValuesWithDefaults(annotationMirror);
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : values.entrySet()) {
            this.map.put(entry.getKey().getSimpleName().toString(), entry.getValue());
        }
    }

    public static AnnotationWrapper buildFor(MapperGeneratorContext context, Element method, Class<?> annot) {


        AnnotationMirror annotationMirror = null;

        for (AnnotationMirror mirror : method.getAnnotationMirrors()) {

            if (mirror.getAnnotationType().toString().equals(annot.getCanonicalName())) {
                annotationMirror = mirror;
                break;
            }
        }

        if (annotationMirror != null) {
            return new AnnotationWrapper(context, annotationMirror, method);
        } else {
            return null;
        }

    }

    public List<String> getAsStrings(String parameterName) {

        List<String> res = new ArrayList<String>();
        AnnotationValue myValue = map.get(parameterName);
        if (myValue.getValue() instanceof List) {
            List<? extends AnnotationValue> values = (List<? extends AnnotationValue>) myValue.getValue();
            for (AnnotationValue value : values) {
                if (value.getValue() instanceof String){
                    res.add((String)value.getValue());
                }else {
                    res.add(value.toString());
                }
            }
        }

        return res;
    }

    @SuppressWarnings("unchecked")
    public List<AnnotationWrapper> getAsAnnotationWrapper(String parameterName) {

        List<AnnotationWrapper> res = new ArrayList<AnnotationWrapper>();
        AnnotationValue myValue = map.get(parameterName);
        List<? extends AnnotationValue> values = null;
        if (myValue.getValue() instanceof List) {
            values = (List<? extends AnnotationValue>) myValue.getValue();
        } else if (myValue.getValue() instanceof AnnotationValue) {
            values = Collections.singletonList(myValue);
        }

        if (values != null) {
            for (AnnotationValue value : values) {
                if (value.getValue() instanceof AnnotationMirror){
                    res.add(new AnnotationWrapper(context, (AnnotationMirror) value.getValue(), annotatedElement));
                }
            }
        }

        return res;
    }

    public boolean getAsBoolean(String parameter) {
        return (Boolean) map.get(parameter).getValue();

    }

    public TypeMirror getAsTypeMirror(String parameter) {
        Object value = map.get(parameter).getValue();
        if (value instanceof TypeMirror) {
            return (TypeMirror) value;
        }

        String classe = value.toString();
        final TypeElement element = context.elements.getTypeElement(classe.replace(".class", ""));
        if (element == null) {
            throw new IllegalArgumentException("Cannot convert parameter '" + parameter + "' to " +
                    TypeMirror.class.getName() + ", was " + value.getClass().getName() + ": " +
                    classe);
        }

        return element.asType();
    }

    public String getAsString(String parameter) {
        return map.get(parameter).getValue().toString();
    }

    public <T> T getAs(String parameter) {
        return (T) map.get(parameter).getValue();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAsList(String parameter) {
        Object val = map.get(parameter).getValue();
        if (!(val instanceof List)) {
            throw new IllegalArgumentException("Cannot convert parameter '" + parameter + "' to " +
                    List.class.getName() + ", was " + val.getClass().getName() + ": " +
                    val.toString());
        }

        List<AnnotationValue> values = (List<AnnotationValue>) val;
        List<T> res = new ArrayList<T>(values.size());
        for (AnnotationValue annotationValue : values) {
            res.add((T) annotationValue.getValue());
        }
        return res;
    }

    public Element asElement() {
        return this.annotationMirror.getAnnotationType().asElement();
    }

    public Element getAnnotatedElement() {
        return annotatedElement;
    }

}
