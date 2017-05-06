package be.ephys.utilitas.base.syncable;

import be.ephys.utilitas.base.nbt_writer.NbtWriter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Persist {

    String name() default "";

    Class<? extends NbtWriter> serializer() default NbtWriter.class;
}
