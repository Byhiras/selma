package fr.xebia.extras.selma.codegen;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

abstract class BuilderWrapperFactory {
    /**
     * BuilderWrapperFactory that is always empty (has no builders).
     */
    private static final BuilderWrapperFactory NOOP = new BuilderWrapperFactory() {
        @Override
        public boolean hasBuilder(TypeMirror out) {
            return false;
        }

        @Override
        public BuilderWrapper getBuilder(TypeMirror out) {
            return null;
        }
    };

    public static BuilderWrapperFactory create(MapperGeneratorContext context, List<AnnotationWrapper> builderAnnotations,
            Element mappedElement) {
        if (builderAnnotations.isEmpty()) {
            return NOOP;
        }

        // validate the annotations
        checkAnnotations(context, builderAnnotations, mappedElement);
        return new DefaultImpl(context, builderAnnotations, mappedElement);
    }

    /**
     * @return {@code true} if the annotation exists and is valid.
     */
    private static void checkAnnotations(MapperGeneratorContext context, List<AnnotationWrapper> builderAnnotations,
            Element mappedElement) {
        for (AnnotationWrapper builderAnnotation : builderAnnotations) {
            List<TypeMirror> classes = builderAnnotation.getAsList("classes");
            List<String> classPatterns = builderAnnotation.getAsList("classPatterns");
            if (classes.isEmpty() && classPatterns.isEmpty()) {
                context.error(mappedElement, "@Builder annotation must have at least one value for 'classes' or 'classPatterns'");
            }

            TypeMirror builderClass = builderAnnotation.getAsTypeMirror("builderClass");
            boolean hasBuilderClass = !context.types().isSameType(builderClass, context.types().getNoType(TypeKind.VOID));

            boolean hasClassName = !builderAnnotation.getAsString("builderClassName").trim().isEmpty();
            if (hasBuilderClass && hasClassName) {
                context.error(mappedElement, "@Builder annotation cannot have value for both 'builderClass' and 'builderClassName'");
            }
        }
    }

    public abstract boolean hasBuilder(TypeMirror out);

    public abstract BuilderWrapper getBuilder(TypeMirror out);

    private static class DefaultImpl extends BuilderWrapperFactory {
        private static final String DEFAULT_BUILDER_PATTERN = "${fqClass}.Builder";
        private static final String DEFAULT_BUILD_METHOD = "build";

        private final Map<TypeMirror, BuilderWrapper> builders = new HashMap<TypeMirror, BuilderWrapper>();

        private final MapperGeneratorContext context;
        private final Element mappedElement;
        private final List<BuilderConfig> builderConfigs;

        DefaultImpl(MapperGeneratorContext context, List<AnnotationWrapper> builderAnnotations, Element mappedElement) {
            this.context = context;
            this.mappedElement = mappedElement;

            this.builderConfigs = new ArrayList<BuilderConfig>(builderAnnotations.size());
            for (AnnotationWrapper builderAnnotation : builderAnnotations) {
                builderConfigs.add(createBuilderConfig(builderAnnotation));
            }
        }

        @Override
        public boolean hasBuilder(TypeMirror out) {
            return getBuilder(out) != null;
        }

        @Override
        public BuilderWrapper getBuilder(TypeMirror out) {
            BuilderWrapper builderWrapper = doGetBuilder(out);
            return builderWrapper == NONE ? null : builderWrapper;
        }

        private BuilderWrapper doGetBuilder(TypeMirror out) {
            BuilderWrapper builder = builders.get(out);
            if (builder == null) {
                builder = NONE;

                // find matching builder config
                BuilderConfig builderConfig = getBuilderConfig(out);
                if (builderConfig != null) {
                    // if found, create the builder
                    builder = builderConfig.getBuilder(out);
                }

                builders.put(out, builder);
            }
            return builder;
        }

        private BuilderConfig getBuilderConfig(TypeMirror bean) {
            // look for exact matches first
            for (BuilderConfig builderConfig : builderConfigs) {
                if (builderConfig.matchesClass(bean)) {
                    return builderConfig;
                }
            }
            // look for pattern matches
            for (BuilderConfig builderConfig : builderConfigs) {
                if (builderConfig.matchesPattern(bean)) {
                    return builderConfig;
                }
            }
            return null;
        }

        /**
         * Sentinel value indicating that no builder exists.  Use this to avoid looking up builders that are known
         * not to exist (because we failed looking them up previously).
         */
        private static final BuilderWrapper NONE = new BuilderWrapper(null, null);

        private BuilderConfig createBuilderConfig(AnnotationWrapper builderAnnotation) {
            List<TypeMirror> classes = builderAnnotation.getAsList("classes");
            List<String> classPatternStrs = builderAnnotation.getAsList("classPatterns");
            List<Pattern> classPatterns = new ArrayList<Pattern>(classPatternStrs.size());
            for (String pattern : classPatternStrs) {
                classPatterns.add(Pattern.compile(pattern));
            }

            String buildMethod = builderAnnotation.getAsString("method").trim();
            buildMethod = buildMethod.isEmpty() ? DEFAULT_BUILD_METHOD : buildMethod;

            boolean enabled = builderAnnotation.getAsBoolean("enabled");

            TypeMirror builderClass = builderAnnotation.getAsTypeMirror("builderClass");
            if (context.types().isSameType(builderClass, context.types().getNoType(TypeKind.VOID))) {
                String pattern = builderAnnotation.getAsString("builderClassName").trim();
                return new BuilderPatternConfig(classes, classPatterns, buildMethod, enabled,
                        pattern.isEmpty() ? DEFAULT_BUILDER_PATTERN : pattern);
            }

            return new BuilderClassConfig(classes, classPatterns, buildMethod, enabled, builderClass);
        }

        private abstract class BuilderConfig {
            private final List<TypeMirror> classes;
            private final List<Pattern> classPatterns;
            private final String buildMethod;
            private final boolean enabled;

            public BuilderConfig(List<TypeMirror> classes, List<Pattern> classPatterns, String buildMethod,
                    boolean enabled) {
                this.classes = classes;
                this.classPatterns = classPatterns;
                this.buildMethod = buildMethod;
                this.enabled = enabled;
            }

            public boolean matchesClass(TypeMirror bean) {
                for (TypeMirror cls : classes) {
                    if (context.types().isSameType(cls, bean)) {
                        return true;
                    }
                }
                return false;
            }

            public boolean matchesPattern(TypeMirror bean) {
                TypeElement element = (TypeElement) context.types().asElement(bean);
                Name fqName = element.getQualifiedName();
                for (Pattern classPattern : classPatterns) {
                    if (classPattern.matcher(fqName).find()) {
                        return true;
                    }
                }
                return false;
            }

            public BuilderWrapper getBuilder(TypeMirror bean) {
                // building explicitly disabled by this config
                if (!enabled) {
                    return NONE;
                }

                TypeMirror builder = getBuilderType(bean);
                if (builder == null) {
                    return NONE;
                }

                // make sure method exists
                TypeElement builderTypeElem = (TypeElement) context.types().asElement(builder);
                boolean found = false;
                for (ExecutableElement method : ElementFilter.methodsIn(builderTypeElem.getEnclosedElements())) {
                    if (method.getSimpleName().toString().equals(buildMethod) &&
                            method.getParameters().isEmpty() &&
                            context.types().isAssignable(method.getReturnType(), bean)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    context.error(mappedElement, "Could not find a method compatible with '%s %s()' on class %s",
                            ((TypeElement) context.types().asElement(builder)).getQualifiedName(), buildMethod,
                            builderTypeElem.getQualifiedName());
                    return NONE;
                }

                return new BuilderWrapper(builder, buildMethod);
            }

            protected abstract TypeMirror getBuilderType(TypeMirror bean);
        }

        private class BuilderClassConfig extends BuilderConfig {
            private final TypeMirror builderClass;

            public BuilderClassConfig(List<TypeMirror> classes, List<Pattern> classPatterns, String buildMethod,
                    boolean enabled, TypeMirror builderClass) {
                super(classes, classPatterns, buildMethod, enabled);
                this.builderClass = builderClass;
            }

            @Override
            protected TypeMirror getBuilderType(TypeMirror ignored) {
                return builderClass;
            }
        }

        private class BuilderPatternConfig extends BuilderConfig {
            private final String builderPattern;

            public BuilderPatternConfig(List<TypeMirror> classes, List<Pattern> classPatterns, String buildMethod,
                    boolean enabled, String builderPattern) {
                super(classes, classPatterns, buildMethod, enabled);
                this.builderPattern = builderPattern;
            }

            @Override
            protected TypeMirror getBuilderType(TypeMirror bean) {
                // find builder
                TypeElement outElem = (TypeElement) context.types().asElement(bean);
                String expectedBuilder = builderPattern
                        .replace("${fqClass}", outElem.getQualifiedName().toString())
                        .replace("${class}", outElem.getSimpleName().toString())
                        .replace("${package}", outElem.getEnclosingElement().toString());

                TypeElement builderType = context.elements().getTypeElement(expectedBuilder);
                if (builderType == null) {
                    TypeElement type = (TypeElement) context.types().asElement(bean);
                    context.error(mappedElement, "Could not resolve builder class for %s: @Builder.className = %s, expected class name = %s",
                            type.getQualifiedName().toString(), builderPattern, expectedBuilder);
                    return null;
                }

                return builderType.asType();
            }
        }
    }

    public static class BuilderWrapper {
        private final TypeMirror builder;
        private final String buildMethod;

        private BuilderWrapper(TypeMirror builder, String buildMethod) {
            this.builder = builder;
            this.buildMethod = buildMethod;
        }

        public TypeMirror getBuilder() {
            return builder;
        }

        public String getBuildMethod() {
            return buildMethod;
        }
    }
}
