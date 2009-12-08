package org.mmbase.applications.vprowizards.spring.util;

import java.util.Date;

import junit.framework.TestCase;

public class ClassInstanceFactoryTest extends TestCase {
    public void test_factory_with_base_class(){
            ClassInstanceFactory<Date> factory = new ClassInstanceFactory<Date>();
            factory.setClassName("java.util.Date");
            assertEquals(Date.class, factory.newInstance().getClass());
    }
    
    public void test_factory_with_subclass(){
        ClassInstanceFactory<Date> factory = new ClassInstanceFactory<Date>();
        factory.setClassName(MyDate.class.getCanonicalName());
//        assertEquals(Date.class, factory.newInstance().getClass());
        assertTrue(Date.class.isAssignableFrom(factory.newInstance().getClass()));
    }
    
}
