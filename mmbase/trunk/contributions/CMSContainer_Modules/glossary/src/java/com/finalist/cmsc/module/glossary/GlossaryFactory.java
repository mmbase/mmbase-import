// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GlossaryFactory.java

package com.finalist.cmsc.module.glossary;

import java.util.Iterator;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;
import org.mmbase.bridge.*;

// Referenced classes of package com.finalist.cmsc.module.glossary:
//            Glossary

public class GlossaryFactory
{

    public GlossaryFactory()
    {
    }

    public static Glossary getGlossary()
    {
        Glossary glossary = Glossary.instance();
        Cloud cloud = CloudProviderFactory.getCloudProvider().getAnonymousCloud();
        NodeManager manager = cloud.getNodeManager("glossary");
        NodeList list = manager.createQuery().getList();
        Node node;
        for(Iterator nodeListIterator = list.iterator(); nodeListIterator.hasNext(); Glossary.instance().addTerm(node.getStringValue("term"), node.getStringValue("definition")))
            node = (Node)nodeListIterator.next();

        return glossary;
    }
}
