package org.mmbase.applications.vprowizards.spring.cache.modifiers;

import junit.framework.TestCase;

public class TemplateCleanerModifierTest extends TestCase {

    public void test_simple_template_with_nodenr_is_removed(){
        cleanAndCheck("users_[name:898]", "users_898");
    }
    
    public void test_simle_template_without_nodenr_is_removed(){
        cleanAndCheck("users_[name]", "users_");
    }
    
    public void test_query_template_with_nodenr_is_removed(){
        cleanAndCheck("users_[name.nogwat.disco:898]", "users_898");
    }
    
    public void test_query_template_without_nodenr_is_removed(){
        cleanAndCheck("users_[name.nogwat.disco]", "users_");
    }
    
    public void test_multi_template_with_nodenr_is_removed(){
        cleanAndCheck("users_[name.nogwat.disco:898]_nogwat_[user:10]", "users_898_nogwat_10");
    }
    
    private void cleanAndCheck(String template, String expected){
        TemplateCleanerModifier modifier = new TemplateCleanerModifier();
        assertEquals(expected, modifier.modify(template));
    }
}
