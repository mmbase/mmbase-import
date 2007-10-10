// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GlossaryTag.java

package com.finalist.cmsc.module.glossary.taglib;

import com.finalist.cmsc.module.glossary.Glossary;
import com.finalist.cmsc.module.glossary.GlossaryFactory;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GlossaryTag extends BodyTagSupport
{

    public GlossaryTag()
    {
        log = LogFactory.getLog(com/finalist/cmsc/module/glossary/Glossary);
    }

    public int doAfterBody()
        throws JspException
    {
        BodyContent bc = getBodyContent();
        String content = bc.getString();
        Glossary glossary = GlossaryFactory.getGlossary();
        try
        {
            bc.clear();
            getPreviousOut().write(glossary.mark(content));
        }
        catch(IOException e)
        {
            log.error("IO Exception when transfer glossary ", e);
        }
        return 0;
    }

    private Log log;
}
