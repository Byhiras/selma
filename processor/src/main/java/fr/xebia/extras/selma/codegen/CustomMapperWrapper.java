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

import com.squareup.javawriter.JavaWriter;
import fr.xebia.extras.selma.IoC;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.util.*;

import static javax.lang.model.element.Modifier.*;

/**
 * Created by slemesle on 16/09/2014.
 */
public class CustomMapperWrapper {

    public static final String CUSTOM_MAPPER_FIELD_TPL = "customMapper%s";
    public static final String WITH_CUSTOM = "withCustom";
    private final AnnotationWrapper annotationWrapper;
    private final MapperGeneratorContext context;
    private final HashMap<InOutType, String> unusedCustomMappers;
    private final Map<InOutType, MappingBuilder> registryMap;
    private final Map<List<InOutType>, MappingBuilder> interceptorMap;
    private final List<TypeElement> customMapperFields;
    private final HashMap<List<InOutType>, String> unusedInterceptor;
    private final Element annotatedElement;
    private final IoC ioC;
    private CustomMapperWrapper parent;

    public CustomMapperWrapper(AnnotationWrapper mapperAnnotation, MapperGeneratorContext context) {
        this.annotatedElement = mapperAnnotation.getAnnotatedElement();

        this.annotationWrapper = mapperAnnotation;
        this.context = context;
        this.customMapperFields = new LinkedList<TypeElement>();
        this.unusedCustomMappers = new HashMap();
        this.unusedInterceptor = new HashMap<List<InOutType>, String>();
        this.registryMap = new HashMap<InOutType, MappingBuilder>();
        this.interceptorMap = new HashMap<List<InOutType>, MappingBuilder>();
        ioC = IoC.valueOf((annotationWrapper.getAsString(MapperWrapper.WITH_IOC) != null ?
                annotationWrapper.getAsString(MapperWrapper.WITH_IOC) : IoC.NO.toString()));
        collectCustomMappers();
    }

    public CustomMapperWrapper(CustomMapperWrapper parent, AnnotationWrapper annotationWrapper,
                               MapperGeneratorContext context) {
        this.parent = parent;
        this.annotationWrapper = annotationWrapper;
        this.customMapperFields = new LinkedList<TypeElement>();
        this.unusedCustomMappers = new HashMap();
        this.unusedInterceptor = new HashMap<List<InOutType>, String>();
        this.registryMap = new HashMap<InOutType, MappingBuilder>();
        this.interceptorMap = new HashMap<List<InOutType>, MappingBuilder>();

        this.context = context;
        this.ioC = parent.ioC;

        if (annotationWrapper != null) {
            this.annotatedElement = annotationWrapper.getAnnotatedElement();
            collectCustomMappers();
        } else {
            annotatedElement = null;
        }
    }


    void emitCustomMappersFields(JavaWriter writer, boolean assign) throws IOException {
        for (TypeElement customMapperField : customMapperFields) {
            final String field = String.format(CUSTOM_MAPPER_FIELD_TPL, customMapperField.getSimpleName().toString());
            if (assign) {
                if (customMapperField.getKind() != ElementKind.INTERFACE
                        && !(customMapperField.getKind() == ElementKind.CLASS
                            && customMapperField.getModifiers().contains(ABSTRACT))) {
                    TypeConstructorWrapper constructorWrapper = new TypeConstructorWrapper(context, customMapperField);
                    // assign the customMapper field to a newly created instance passing to it the declared source params
                    writer.emitStatement("this.%s = new %s(%s)", field, customMapperField.getQualifiedName().toString(),
                            constructorWrapper.hasMatchingSourcesConstructor ? context.newParams() : "");
                }
            } else {
                writer.emitEmptyLine();
                writer.emitJavadoc("This field is used for custom Mapping");
                if (ioC == IoC.SPRING) {
                    writer.emitAnnotation("org.springframework.beans.factory.annotation.Autowired");
                }
                writer.emitField(customMapperField.asType().toString(), String.format(CUSTOM_MAPPER_FIELD_TPL,
                        customMapperField.getSimpleName().toString()), EnumSet.of(PRIVATE));

                writer.emitEmptyLine();
                writer.emitJavadoc("Custom Mapper setter for " + field);
                
                EnumSet<Modifier> modifiers = EnumSet.of(PUBLIC, FINAL);
                if (ioC == IoC.CDI || ioC == IoC.CDI_SINGLETON || ioC == IoC.CDI_APPLICATION_SCOPED) {
                	modifiers = EnumSet.of(PUBLIC);	
                }
                writer.beginMethod("void", "setCustomMapper" + customMapperField.getSimpleName(),modifiers, customMapperField.asType().toString(), "mapper");
                writer.emitStatement("this.%s = mapper", field);
                writer.endMethod();
                writer.emitEmptyLine();
            }
        }
    }


    /**
     * Adds a custom mapping method to the registry for later use at codegen.
     *
     * @param method
     * @param immutable
     * @param ignoreAbstract
     */
    private void pushCustomMapper(final TypeElement element, final MethodWrapper method, Boolean immutable,
                                  boolean ignoreAbstract) {
        MappingBuilder res = null;
        String customMapperFieldName = ignoreAbstract ? "this" : buildMapperFieldName(element);

        InOutType inOutType = method.inOutType();
        String methodCall = String.format("%s.%s", customMapperFieldName, method.getSimpleName());

        if (immutable == null) {
            res = MappingBuilder.newCustomMapper(inOutType, methodCall);
            unusedCustomMappers.put(inOutType,
                    String.format("%s.%s", element.getQualifiedName(), method.getSimpleName()));
        } else if (immutable) {
            res = MappingBuilder.newCustomMapper(inOutType, methodCall);
            inOutType = new InOutType(inOutType, true);
        } else if (!immutable) {
            res = MappingBuilder.newCustomMapperImmutableForUpdateGraph(inOutType, methodCall);
            inOutType = new InOutType(inOutType, false);
        }

        if (registryMap.containsKey(inOutType)){
            MappingBuilder mappingBuilder = registryMap.get(inOutType);
            context.error(method.element(),"Conflicting custom mapper method "+
                         "conflicts with '%s' For '%s' ", mappingBuilder.toString(), inOutType.toString());
        }
        registryMap.put(inOutType, res);
    }


    private void pushMappingInterceptor(TypeElement element, MethodWrapper method, boolean ignoreAbstract) {

        String customMapperFieldName = ignoreAbstract ? "this" : buildMapperFieldName(element);
        List<InOutType> inOutTypes = method.allInOutArgs();

        MappingBuilder res = MappingBuilder.newMappingInterceptor(inOutTypes,
                String.format("%s.%s", customMapperFieldName, method.getSimpleName()));

        // Push IOType for both mutable and immutable mapping
        interceptorMap.put(inOutTypes, res);
        List<InOutType> noUpdate = new ArrayList<InOutType>();
        for (InOutType inOutType : inOutTypes){
            noUpdate.add(new InOutType(inOutType.in(), inOutType.out(), false));
        }
        interceptorMap.put(noUpdate, res);
        unusedInterceptor.put(inOutTypes, String.format("%s.%s", element.getQualifiedName(), method.getSimpleName()));
    }

    private void collectCustomMappers() {

        List<String> customClasses = annotationWrapper.getAsStrings(WITH_CUSTOM);
        if (customClasses.size() > 0) {
            int mappingMethodCount = 0;

            for (String customMapper : customClasses) {

                final TypeElement element = context.elements.getTypeElement(customMapper.replaceAll("\\.class$", ""));

                mappingMethodCount += collectCustomMethods(element, false);


                if (mappingMethodCount == 0) {
                    context.error(element, "No valid mapping method found in custom selma class %s\\n " +
                            "A custom mapping method is public and returns a type not void, it takes one parameter or" +
                            " more if you specified datasource.", customMapper);
                } else {

                    TypeConstructorWrapper constructorWrapper = new TypeConstructorWrapper(context, element);
                    if (!constructorWrapper.hasDefaultConstructor && element.getKind() != ElementKind.INTERFACE) {
                        context.error(element, "No default public constructor found in custom mapping class %s\\n" +
                                " Please add one", customMapper);
                    }

                    // Here we collect the name of the field to create in the Mapper generated class
                    customMapperFields.add(element);
                }


            }
        }

    }

    private int collectCustomMethods(TypeElement element, boolean ignoreAbstract) {
        int mappingMethodCount = 0;
        final List<ExecutableElement> methods = ElementFilter.methodsIn(element.getEnclosedElements());
        final HashMap<CustomMapperKey, CustomMapperEntry> customInOutTypes = new HashMap<CustomMapperKey, CustomMapperEntry>();
        for (ExecutableElement method : methods) {
            MethodWrapper methodWrapper = new MethodWrapper(method, (DeclaredType) element.asType(), context);
            // We should ignore abstract methods if parsing an abstract mapper class
            if (ignoreAbstract && methodWrapper.isAbstract()) {
                continue;
            }
            if (isValidCustomMapping(methodWrapper)) {

                if (methodWrapper.isCustomMapper()) {
                    pushCustomMapper(element, methodWrapper, null, ignoreAbstract);
                    addCustomInOutType(customInOutTypes, methodWrapper);
                } else {
                    pushMappingInterceptor(element, methodWrapper, ignoreAbstract);
                }
                mappingMethodCount++;
            }
        }

        // Create defaults custom mappers if immutable or mutable is missing
        addMissingMappings(customInOutTypes, element, ignoreAbstract);
        return mappingMethodCount;
    }

    private void addMissingMappings(HashMap<CustomMapperKey, CustomMapperEntry> customInOutTypes, TypeElement element,
                                    boolean ignoreAbstract) {
        for (Map.Entry<CustomMapperKey, CustomMapperEntry> entry : customInOutTypes.entrySet()) {
            if (entry.getValue().updateGraphMethod == null) {
                pushCustomMapper(element, entry.getValue().immutableMethod, Boolean.TRUE, ignoreAbstract);
            } else if (entry.getValue().immutableMethod == null) {
                pushCustomMapper(element, entry.getValue().updateGraphMethod, Boolean.FALSE, ignoreAbstract);
            }
        }
    }

    private void addCustomInOutType(HashMap<CustomMapperKey, CustomMapperEntry> customInOutTypes,
                                    MethodWrapper methodWrapper) {
        CustomMapperKey key = new CustomMapperKey(methodWrapper.inOutType());
        CustomMapperEntry entry1 = customInOutTypes.get(key);
        if (entry1 == null) {
            CustomMapperEntry entry = new CustomMapperEntry(methodWrapper.inOutType(), methodWrapper);
            customInOutTypes.put(key, entry);
        } else {
            CustomMapperEntry entry = new CustomMapperEntry(entry1, methodWrapper);
            customInOutTypes.put(key, entry);
        }
    }

    private String buildMapperFieldName(TypeElement element) {
        return String.format(CUSTOM_MAPPER_FIELD_TPL, element.getSimpleName());
    }

    private boolean isValidCustomMapping(MethodWrapper methodWrapper) {
        boolean res = true;

        if (MapperProcessor.exclusions.contains(methodWrapper.getSimpleName())) {
            // We skip excluded methods
            return false;
        }

        if (!methodWrapper.element().getModifiers().contains(javax.lang.model.element.Modifier.PUBLIC)) {
            res = false;
        }

        if (methodWrapper.element().getModifiers().contains(Modifier.STATIC)) {
            res = false;
        }

        if (!methodWrapper.isCustomMapper() && !methodWrapper.isMappingInterceptor()) {
            res = false;
        }

        return res;
    }


    public MappingBuilder getMapper(InOutType inOutType) {
        MappingBuilder res = registryMap.get(inOutType);
        if (res != null) {
            unusedCustomMappers.remove(inOutType);
        } else if (parent != null) {
            res = parent.getMapper(inOutType);
        }

        return res;
    }

    public MappingBuilder getMappingInterceptor(List<InOutType> inOutTypes) {
        MappingBuilder res = interceptorMap.get(inOutTypes);
        if (res != null) {
            List<InOutType> noUpdate = new ArrayList<InOutType>();
            for (InOutType inOutType : inOutTypes){
                noUpdate.add(new InOutType(inOutType.in(), inOutType.out(), true));
            }
            unusedInterceptor.remove(noUpdate);
        } else if (parent != null) {
            res = parent.getMappingInterceptor(inOutTypes);
        }
        return res;
    }

    public void reportUnused() {
        for (String field : unusedCustomMappers.values()) {
            context.warn(annotatedElement, "Custom mapping method \"%s\" is never used", field);
        }
        for (String field : unusedInterceptor.values()) {
            context.warn(annotatedElement, "Custom interceptor method \"%s\" is never used", field);
        }
    }

    public List<TypeElement> mapperFields() {
        return customMapperFields;
    }

    public void addFields(List<TypeElement> childFields) {

        for (TypeElement childField : childFields) {
            boolean found = false;
            for (TypeElement maperField : customMapperFields) {

                if (childField.equals(maperField)) {
                    found = true;
                    break;
                }

            }
            if (!found) {
                customMapperFields.add(childField);
            }
        }
    }

    public String toString(){
        return this.annotationWrapper.toString();
    }

    public void addMappersElementMethods(TypeElement mapperInterface) {
        collectCustomMethods(mapperInterface, true);
    }

    class CustomMapperKey {
        final TypeMirror in;
        final TypeMirror out;

        public CustomMapperKey(InOutType inOutType) {
            in = inOutType.in();
            out = inOutType.out();
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CustomMapperKey key = (CustomMapperKey) o;

            if (!MapperProcessor.types.isSameType(in, key.in)) return false;
            return MapperProcessor.types.isSameType(out, key.out);

        }

        @Override
        public int hashCode() {
            // Here we use the string representation of types because hashCode is not consistent bw instances
            int result = ("" + in).hashCode();
            result = 31 * result + ("" + out).hashCode();
            return result;
        }
    }

    class CustomMapperEntry {
        final MethodWrapper updateGraphMethod;
        final MethodWrapper immutableMethod;

        public CustomMapperEntry(InOutType inOutType, MethodWrapper customMethod) {
            if (inOutType.isOutPutAsParam()) {
                this.updateGraphMethod = customMethod;
                this.immutableMethod = null;
            } else {
                this.immutableMethod = customMethod;
                this.updateGraphMethod = null;
            }
        }


        public CustomMapperEntry(CustomMapperEntry entry1, MethodWrapper methodWrapper) {
            if (entry1.immutableMethod != null) {
                immutableMethod = entry1.immutableMethod;
                updateGraphMethod = methodWrapper;
            } else {
                updateGraphMethod = entry1.updateGraphMethod;
                immutableMethod = methodWrapper;
            }
        }
    }
}
