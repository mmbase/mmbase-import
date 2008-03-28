package com.finalist.cmsc.knownvisitor;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.module.Module;

public abstract class KnownVisitorModule extends Module {

   private static KnownVisitorModule instance;


   public abstract Visitor getVisitor(HttpServletRequest request);


   public static KnownVisitorModule getInstance() {
      return instance;
   }


   public static void setInstance(KnownVisitorModule module) {
      instance = module;
   }
}
