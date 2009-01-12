package org.mmbase.applications.vprowizards.spring.cache.modifiers;

import junit.framework.TestCase;

public class PrefixSuffixModifierTest extends TestCase {

    private PrefixSuffixModifier mod;

    @Override
    protected void setUp() throws Exception {
        mod = new PrefixSuffixModifier();
        mod.setPrefix("pre_");
        mod.setSuffix("_suf");
    }

    public void test_empty_or_null_prefix_suffix() {
        mod = new PrefixSuffixModifier();
        String s = "hallo";
        assertEquals(s, mod.modify(s));
        mod.setPrefix(null);
        assertEquals(s, mod.modify(s));
        mod.setSuffix(null);
        assertEquals(s, mod.modify(s));
        mod.setPrefix("   ");
        assertEquals(s, mod.modify(s));
        mod.setSuffix("   ");
        assertEquals(s, mod.modify(s));
        mod.setPrefix("\t");
        assertEquals(s, mod.modify(s));
        mod.setSuffix("\t");
        assertEquals(s, mod.modify(s));
        System.out.println("dunnit");
    }
    
    public void test_leading_and_trainling_spaces_should_go(){
        mod = new PrefixSuffixModifier();
        mod.setPrefix(" p_");
        assertEquals("p_hi", mod.modify("hi"));
        mod.setPrefix("p_  ");
        assertEquals("p_hi", mod.modify("hi"));
        
        mod = new PrefixSuffixModifier();
        mod.setSuffix(" _h");
        assertEquals("hi_h", mod.modify("hi"));
        mod.setSuffix("_h    ");
        assertEquals("hi_h", mod.modify("hi"));
    }

}
