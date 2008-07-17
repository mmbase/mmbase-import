package com.finalist.cmsc.module.luceusmodule;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.NodeEventListener;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class TestEventListener implements NodeEventListener {

   private static Logger log = Logging.getLoggerInstance(TestEventListener.class.getName());


   public TestEventListener(LuceusModule module) {
      MMBase.getMMBase().addNodeRelatedEventsListener("object", this);
      log.info("registered listener for: " + "object");
   }


   public void notify(NodeEvent event) {
      log.info("test: " + event);
   }
}
