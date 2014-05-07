package scaldingflows;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Scalding function dependencies annotation.
 * The function with this annotation, can run only after its dependencies have ran.
 * USAGE: @ScaldingDependencies("func1,class.func2,fullyQualifiedName.func3")
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScaldingDependencies {
    String dependencies(); // comma-separated list of parent functions
}
