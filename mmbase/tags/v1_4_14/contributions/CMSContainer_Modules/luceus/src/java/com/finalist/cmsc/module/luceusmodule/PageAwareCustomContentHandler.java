package com.finalist.cmsc.module.luceusmodule;

import java.util.Set;

import org.mmbase.bridge.Node;

/**
 * A {@link CustomContentHandler} which finds linked content, not only based on the content element,
 * but also takes the page into account.
 * 
 * @author Rob Schellhorn
 * @since 1.4.14
 */
public interface PageAwareCustomContentHandler extends CustomContentHandler {

   /**
    * Retrieves a set containing content related to the given contentElement, when it is showed on
    * the given page.
    * 
    * @param contentElement the contentElement being indexed.
    * @param page the page scope the contentElement is being index in.
    * @return A set of linked content nodes, never <code>null</code>.
    */
   Set<Node> findLinkedContent(Node contentElement, Node page);
}