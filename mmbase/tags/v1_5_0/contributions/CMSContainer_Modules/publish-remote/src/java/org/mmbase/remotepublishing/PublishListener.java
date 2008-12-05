package org.mmbase.remotepublishing;

import org.mmbase.bridge.Node;

public interface PublishListener {
   void published(Node publishedNode);
   void publishedFailed(Node publishedNode, String systemMessage);
}
