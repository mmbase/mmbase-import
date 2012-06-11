package org.mmbase.applications.vprowizards.spring.cache.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import junit.framework.TestCase;


public abstract class AbstractTemplateParserTest extends TestCase{
    protected String matchingTemplate = getMatchingTemplate();
    protected String matchingTemplateWithNodenr = getMatchingTemplateWithNodenr();
    protected String nonMatchingTemplate = getNonMatchingTemplate();
    protected String nonMatchingTemplateWithNodenr = getNonMatchingTemplateWithNodenr();
    protected String matchingTemplateResult = getMatchingTemplateResult();
    protected String matchingTemplateWithTemplateRemoved = getMatchingTemplateWithTemplateRemoved();
    protected String nodeType = getNodeType();
    protected String nodeNumber= getNodeNumber();
    protected List<String> legalPatterns = getLegalPatterns();
    protected List<String> illlegalPatterns = getIllegalPatterns();
    protected TemplateParser templateParser ;
    private Class<? extends TemplateParser> templateParserClass = getTemplateParserClass();
    
    
    public void test_isPattern_method(){
        legalPatterns.add(matchingTemplate);
        legalPatterns.add(matchingTemplateWithNodenr);
        
        
        try {
            Method isTemplateMethod = templateParserClass.getMethod("isTemplate", String.class);
            for(String legalPattern :illlegalPatterns){
                Boolean result = (Boolean)isTemplateMethod.invoke(null, legalPattern);
                assertFalse(String.format("Pattern %s should be invalid", legalPattern), result);
            }
            for(String legalPattern :legalPatterns){
                Boolean result = (Boolean)isTemplateMethod.invoke(null, legalPattern);
                assertTrue(String.format("Pattern %s should be valid", legalPattern),result);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void test_non_matching_template_is_ignored(){
        createInstanceAndInsertNodenr(nodeType, nodeNumber, nonMatchingTemplate);
        assertEquals(nonMatchingTemplate, templateParser.getTemplate());
    }

    public void test_matching_template_has_nodenumber_inserted(){
        createInstanceAndInsertNodenr(nodeType, nodeNumber, matchingTemplate);
        assertEquals(matchingTemplateResult, templateParser.getTemplate());
    }
    
    public void test_matching_tempalte_with_nodenr_is_cleaned_and_number_is_inserted(){
        createInstanceAndInsertNodenr(nodeType, nodeNumber, matchingTemplateWithNodenr);
        assertEquals(matchingTemplateResult, templateParser.getTemplate());
    }
    
    public void test_nonmatching_template_with_nodenr_is_cleaned(){
        createInstanceAndInsertNodenr(nodeType, nodeNumber, nonMatchingTemplateWithNodenr);
        assertEquals(nonMatchingTemplate, templateParser.getTemplate());
    }
    
    public void test_strip_template_leave_nodenr_withNodenr(){
        createInstanceAndInsertNodenr(nodeType, nodeNumber, matchingTemplate);
        templateParser.stripTemplateLeaveNodenr();
        assertEquals(matchingTemplateWithTemplateRemoved, templateParser.getTemplate());
    }
    
    protected abstract String getMatchingTemplate();
    protected abstract String getMatchingTemplateWithNodenr();
    protected abstract String getNonMatchingTemplate();
    protected abstract String getNonMatchingTemplateWithNodenr();
    protected abstract String getMatchingTemplateResult();
    protected abstract String getMatchingTemplateWithTemplateRemoved();
    protected abstract String getNodeType();
    protected abstract String getNodeNumber();
    protected abstract void createInstanceAndInsertNodenr(String nodeType, String nodeNumber, String template);
    protected abstract List<String> getLegalPatterns();
    protected abstract List<String> getIllegalPatterns();
    protected abstract Class<? extends TemplateParser> getTemplateParserClass();
    
}
