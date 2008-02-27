package com.finalist.cmsc.maintenance.log;

import org.mmbase.module.core.MMBaseObserver;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Jeoffrey Bakker (Finalist IT Group)
 */
public class LoggingMMBaseObserver implements MMBaseObserver {
   private final static Logger log = Logging.getLoggerInstance(MMBaseObserver.class);

   private ArrayList<LogConstraint> logLocalConstraints = new ArrayList<LogConstraint>();
   private ArrayList<LogConstraint> logRemoteConstraints = new ArrayList<LogConstraint>();
   private static LoggingMMBaseObserver instance = null;


   private LoggingMMBaseObserver() {
      MMBase.getMMBase().getBuilder("object").addLocalObserver(this);
   }


   public static LoggingMMBaseObserver getInstance() {
      if (instance == null) {
         instance = new LoggingMMBaseObserver();
      }
      return instance;
   }


   public boolean nodeRemoteChanged(String machine, String number, String builder, String ctype) {
      Iterator<LogConstraint> logConstraintIter = logRemoteConstraints.iterator();
      while (logConstraintIter.hasNext()) {
         LogConstraint logConstraint = logConstraintIter.next();

         if (logConstraint.matches(machine, number, builder, ctype)) {
            log.info("Remote! machine:" + machine + ", number:" + number + ",builder:" + ", ctype:" + ctype);
            if (logConstraint.isPrintStrackTrace()) {
               log.info(Logging.stackTrace(new Exception()));
            }
         }
      }
      return true;
   }


   public boolean nodeLocalChanged(String machine, String number, String builder, String ctype) {
      Iterator<LogConstraint> logConstraintIter = logLocalConstraints.iterator();
      while (logConstraintIter.hasNext()) {
         LogConstraint logConstraint = logConstraintIter.next();

         if (logConstraint.matches(machine, number, builder, ctype)) {
            log.info("Local! machine:" + machine + ", number:" + number + ",builder:" + builder + ", ctype:" + ctype);
            if (logConstraint.isPrintStrackTrace()) {
               log.info(Logging.stackTrace(new Exception()));
            }
         }
      }
      return true;
   }


   public void addLogConstraints(LogConstraint logConstraint, boolean local, boolean remote) {
      if (local) {
         logLocalConstraints.add(logConstraint);
      }
      if (remote) {
         logRemoteConstraints.add(logConstraint);
      }
      if (local ^ !remote) {
         logLocalConstraints.add(logConstraint);
         logRemoteConstraints.add(logConstraint);
      }
   }


   public void addLogConstraints(LogConstraint logConstraint) {
      addLogConstraints(logConstraint, true, true);
   }


   public void removeLogConstraints(LogConstraint logConstraint) {
      removeLogConstraints(logConstraint, true, true);
   }


   public void removeLogConstraints(LogConstraint logConstraint, boolean local, boolean remote) {
      if (local) {
         logLocalConstraints.remove(logConstraint);
      }
      if (remote) {
         logRemoteConstraints.remove(logConstraint);
      }
      if (local ^ !remote) {
         logLocalConstraints.remove(logConstraint);
         logRemoteConstraints.remove(logConstraint);
      }
   }


   public ArrayList<LogConstraint> getLogLocalConstraints() {
      return logLocalConstraints;
   }


   public ArrayList<LogConstraint> getLogRemoteConstraints() {
      return logRemoteConstraints;
   }

}
