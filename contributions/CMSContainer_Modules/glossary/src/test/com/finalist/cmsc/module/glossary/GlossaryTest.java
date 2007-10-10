package com.finalist.cmsc.module.glossary;

import junit.framework.TestCase;

public class GlossaryTest extends TestCase {

    public GlossaryTest()
    {
    }

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
        assertEquals("I am a test <a href=\"#\" title=\"am i\" id=\"_glossary_term1\">term1</a> term1 material", result);
        material = "I am a test term1term1 material";
        result = Glossary.instance().mark(material);
        assertEquals("I am a test term1term1 material", result);
        material = "I am a test(term1)term1 material";
        result = Glossary.instance().mark(material);
        assertEquals("I am a test(<a href=\"#\" title=\"am i\" id=\"_glossary_term1\">term1</a>)term1 material", result);
    }

    public void testIgnoreLink()
    {
        String material = "I am a <a href=\"\">test term1</a> aaa material";
        String result = Glossary.instance().mark(material);
        assertEquals("I am a <a href=\"\">test term1</a> aaa material", result);
        material = "I am a <a>test term1</a> aaa term1 material";
        result = Glossary.instance().mark(material);
        assertEquals("I am a <a>test term1</a> aaa <a href=\"#\" title=\"am i\" id=\"_glossary_term1\">term1</a> material", result);
        material = "I am a <a>test term1</a> <a>aaa term1</a> term1 material";
        result = Glossary.instance().mark(material);
        assertEquals("I am a <a>test term1</a> <a>aaa term1</a> <a href=\"#\" title=\"am i\" id=\"_glossary_term1\">term1</a> material", result);
    }
}
