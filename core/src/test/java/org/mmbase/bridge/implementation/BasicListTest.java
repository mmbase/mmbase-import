package org.mmbase.bridge.implementation;


import org.junit.Test;
import static org.junit.Assert.*;


public class BasicListTest {


    @Test
    public void test() {
        BasicList<Integer> list = new BasicList<Integer>() {
            @Override
             protected Integer convert(Object o) {
                return Integer.parseInt(o.toString());
            }
        };
        list.add(2);
        list.add(1);
        list.sort();
        assertEquals("[1, 2]", list.toString());

    }


}
