package com.finalist.cmsc.module.glossary;

import junit.framework.TestCase;

public class GlossaryTest extends TestCase {

    protected void setUp()
        throws Exception
    {
        super.setUp();
        Glossary.instance().addTerm("term1", "am i");
        Glossary.instance().addTerm("term2", "im ii");
    }

    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    public void testMark()
    {
        String material = "I am a test term1 term1 material";
        String result = Glossary.instance().mark(material);
        assertEquals(String.format("I am a test %s term1 material",makeLink("term1")), result);
        material = "I am a test term1term1 material";
        result = Glossary.instance().mark(material);
        assertEquals("I am a test term1term1 material", result);
        material = "I am a test(term1)term1 material";
        result = Glossary.instance().mark(material);
        assertEquals(String.format("I am a test(%s)term1 material",makeLink("term1")), result);
    }

    public void testIgnoreLink()
    {
        String material = "I am a <a href=\"\">test term1</a> aaa material";
        String result = Glossary.instance().mark(material);
        assertEquals("I am a <a href=\"\">test term1</a> aaa material", result);
        material = "I am a <a>test term1</a> aaa term1 material";
        result = Glossary.instance().mark(material);
        assertEquals(String.format("I am a <a>test term1</a> aaa %s material",makeLink("term1")), result);
        material = "I am a <a>test term1</a> <a>aaa term1</a> term1 material";
        result = Glossary.instance().mark(material);
        assertEquals(String.format("I am a <a>test term1</a> <a>aaa term1</a> %s material",makeLink("term1")), result);
    }

    public void testMarkMultiTerms(){
        String material = "i am a term1 and a term2";
        String result = Glossary.instance().mark(material);
        assertEquals(String.format("i am a %s and a %s",makeLink("term1"),makeLink("term2")),result);
    }

    public void testInBold(){
        String material = "i am a <b>term1</b> and a term";
        String result = Glossary.instance().mark(material);
        assertEquals("i am a <b>term1</b> and a term",result);
    }

    public void testInTitle(){
        String materialInH1 = "i am a <h1>term1</h1> and a term";
        String materialInH2 = "i am a <h2>term1</h2> and a term";
        String materialInH3 = "i am a <h3>term1</h3> and a term";
        String result = Glossary.instance().mark(materialInH1);
        String result2 = Glossary.instance().mark(materialInH2);
        String result3 = Glossary.instance().mark(materialInH3);
        assertEquals(materialInH1,result);
        assertEquals(materialInH2,result2);
        assertEquals(materialInH3,result3);        
    }

    public void testBRWrap(){
        String m = "<br/>I am a test term1 term1 material<br/>";
         String result = Glossary.instance().mark(m);
         assertEquals(String.format("<br/>I am a test %s term1 material<br/>",makeLink("term1")), result);
    }

    public String makeLink(String word){
        return String.format(Glossary.LINKPATTERN,Glossary.instance().getTerms().get(word),word,word);
    }
}
