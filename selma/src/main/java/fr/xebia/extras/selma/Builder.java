package fr.xebia.extras.selma;

import java.lang.annotation.Target;

/**
 * Builder annotation used to designate builder-behaviour for {@link Mapper#withBuilders()} annotation.
 */
@Target({})
public @interface Builder {
    /**
     * One more classes that the builder configuration will be used for.  It is possible to specify both this and
     * {@link #classPatterns()}.
     */
    Class<?>[] classes() default {};

    /**
     * One or more regular expressions (Java syntax) used to match classes that the builder configuration will be used
     * for.  It is possible to specify both this and {@link #classes()}.
     */
    String[] classPatterns() default {};

    /**
     * The builder class.  Note that specifying both this and {@link #builderClassName()} is unsupported and will
     * cause an error.
     */
    Class<?> builderClass() default void.class;

    /**
     * <p>The class name used for the builder.  Can contain:
     * <ul>
     *     <li>{@code ${class}} to insert the class name;</li>
     *     <li>{@code ${fqClass}} to insert the fully-qualified class name;</li>
     *     <li>{@code ${package}} to insert the package name;</li>
     * </ul>
     * The default value is empty, which is converted to {@link #DEFAULT_BUILDER_CLASS_NAME} if
     * {@link #builderClass()} is {@code void}.</p>
     * <p>Note that specifying both this and {@link #builderClass()} is unsupported and will cause an error.</p>
     */
    String builderClassName() default "";

    /**
     * The build method name. The default value is {@link #DEFAULT_METHOD}.
     */
    String method() default DEFAULT_METHOD;

    /**
     * Whether the builder is enabled.  This can be used to explicitly disable builder support (for example,
     * to exclude a particular class that would otherwise be matched by another {@link Builder} annotation).
     */
    boolean enabled() default true;

    String DEFAULT_BUILDER_CLASS_NAME = "${fqClass}.Builder";
    String DEFAULT_METHOD = "build";
}
