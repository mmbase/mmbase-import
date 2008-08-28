/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule.luceus;

import java.io.IOException;

import javax.management.MalformedObjectNameException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.luceus.core.om.Envelope;
import com.luceus.server.LuceusIndexServiceMBean;
import com.luceus.server.util.MBeanHelper;

/**
 * Wrapper around LuceusIndexer, handles remote and local indexers
 * 
 * @author Wouter Heijke
 */
public class Indexer {

   private static Log log = LogFactory.getLog(Indexer.class);

   private LuceusIndexServiceMBean indexer;

   private String name;


   public Indexer(String name, String url) throws IOException, MalformedObjectNameException, NullPointerException {

      this.name = name;

      if (url != null) {
         log.info("LuceusModule uses remote Luceus Indexer");
      }
      else {
         log.info("LuceusModule uses local Luceus Indexer");
      }

      indexer = MBeanHelper.getIndexer(url);
   }


   public void write(Envelope doc) {
      if (doc != null) {
         doc.setRepository(name);
         indexer.enqueue(doc);
      }
   }

}
