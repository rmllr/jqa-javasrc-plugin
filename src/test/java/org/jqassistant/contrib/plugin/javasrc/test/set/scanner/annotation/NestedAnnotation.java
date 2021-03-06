package org.jqassistant.contrib.plugin.javasrc.test.set.scanner.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A annotation which will be nested in {@link Annotation}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NestedAnnotation {

    String value();
}
