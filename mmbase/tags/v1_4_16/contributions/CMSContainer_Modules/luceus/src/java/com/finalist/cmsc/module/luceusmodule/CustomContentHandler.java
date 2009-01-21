/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule;

import java.util.Set;

import org.mmbase.bridge.Node;

/**
 * Custom content handler for content that needs to be indexed and is in some
 * way related to a ContentElement
 * 
 * @author Wouter Heijke
 */
public interface CustomContentHandler {

   public abstract Set<Node> findLinkedContent(Node contentElement);


   public abstract void registerListeners(LuceusModule module);

}