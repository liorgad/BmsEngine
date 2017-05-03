package com.danenergy.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;

/**
 * Created by Lior Gad on 2/13/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) //can use in field only.
public @interface ParserDefinition
{
    int Index() default 0;

    int BytesLength() default 0;

    int ASCIILength() default 0;

    String RelatedFieldLength() default "";

    boolean IsSigned() default  true;

    public static Comparator<ParserDefinition> ParserDefinitionComparator
            = new Comparator<ParserDefinition>() {

        public int compare(ParserDefinition p1, ParserDefinition p2) {

            final int BEFORE = -1;
            final int EQUAL = 0;
            final int AFTER = 1;

            //this optimization is usually worthwhile, and can
            //always be added
            if (p1.Index() == p2.Index()) return EQUAL;

            //primitive numbers follow this form
            if (p1.Index() < p2.Index()) return BEFORE;
            if (p1.Index() > p2.Index()) return AFTER;

            return 0;
        }
    };
}
