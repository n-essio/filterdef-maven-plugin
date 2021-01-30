package it.ness.codebuilder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) //on class field
public @interface CodeBuilderNullBooleanFilterDef {

	String prefix() default "";
	String name() default "";
	String type() default "";
	String condition() default "";
	CodeBuilderOption[] options() default {};

}
