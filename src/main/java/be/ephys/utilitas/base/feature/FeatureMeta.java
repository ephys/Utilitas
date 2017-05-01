package be.ephys.utilitas.base.feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureMeta {

    String name();

    String description() default "";

    boolean defaultEnabled() default true;

    Class<? extends Feature>[] dependencies() default {};
}
