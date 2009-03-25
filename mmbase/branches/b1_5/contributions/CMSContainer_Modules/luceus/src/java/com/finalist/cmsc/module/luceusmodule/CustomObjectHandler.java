/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule;

import java.util.List;
import java.util.Set;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.search.PageInfo;

/**
 * Custom object handler for objects (that is not content) that need to be indexed and are in some
 * way related to a ContentElement
 * 
 * @author Jurn de Ruijter
 */
public interface CustomObjectHandler {

   Set<Node> findLinkedContent(Node customObject);

   Set<PageInfo> findAllPagesForCustomObject(Node customObject);

   void registerListeners(LuceusModule module);

   void fullIndex(boolean erase);
}