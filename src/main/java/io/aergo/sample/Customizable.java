package io.aergo.sample;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Any method/interface/class marked this is customizable.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface Customizable {

}
