/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NotFoundException;

import com.finalist.cmsc.module.luceusmodule.luceus.Indexer;
import com.finalist.cmsc.module.luceusmodule.luceus.LuceusUtil;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.services.search.PageInfo;
import com.finalist.cmsc.services.search.Search;
import com.luceus.core.om.Envelope;
import com.luceus.core.om.EnvelopeFactory;
import com.luceus.core.om.EnvelopeFieldFactory;

/**
 * Task that handles queued updates
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.2 $
 */
public class IndexUpdateTask implements Runnable {
	private static Log log = LogFactory.getLog(IndexUpdateTask.class);

	private LinkedBlockingQueue<QueuedUpdate> in;

	private LuceusModule module;

	private HashSet<String> secondaryCache;

	private int id;

	public IndexUpdateTask(LuceusModule module, LinkedBlockingQueue<QueuedUpdate> queue, int id) {
		this.module = module;
		this.in = queue;
		this.id = id;
	}

	public void run() {
		log.debug(id + " IndexUpdateTask running..");

		secondaryCache = new HashSet<String>();

		try {
			while (in.size() > 0) {
				QueuedUpdate update = in.take();
				try {
					switch (update.getMethod()) {
					case QueuedUpdate.METHOD_UPDATE_CONTENT_INDEX:
						executeUpdateContentIndex(update.getNodeNumber());
						break;
					case QueuedUpdate.METHOD_UPDATE_CONTENT_CHANNEL_INDEX:
						executeUpdateContentChannelIndex(update.getNodeNumber());
						break;
					case QueuedUpdate.METHOD_UPDATE_PAGE_INDEX:
						executeUpdatePageIndex(update.getNodeNumber());
						break;
					case QueuedUpdate.METHOD_UPDATE_SECONDARYCONTENT_INDEX:
						executeUpdateSecondaryContentIndex(update.getNodeNumber());
						break;
					case QueuedUpdate.METHOD_DELETE_CONTENT_INDEX:
						executeDeleteContentIndex(update.getNodeNumber());
						break;
					case QueuedUpdate.METHOD_DELETE_PAGE_INDEX:
						executeDeletePageIndex(update.getNodeNumber());
						break;
					case QueuedUpdate.METHOD_DELETE_PAGECONTENT_INDEX:
						executeDeletePageContentIndex(update.getNodeNumber(), update.getRelatedNodeNumber());
						break;
					case QueuedUpdate.METHOD_DELETE_CHANNELCONTENT_INDEX:
						executeDeleteChannelContentIndex(update.getNodeNumber(), update.getRelatedNodeNumber());
						break;
					case QueuedUpdate.METHOD_CREATE_CONTENT_INDEX:
						executeCreateContentIndex(update.getNodeNumber());
						break;
					case QueuedUpdate.METHOD_ERASE_INDEX:
						executeEraseIndex();
						break;
					default:
						log.error("method type not found " + update.getMethod());
					}
				} catch (Throwable t) {
					log.error("IndexUpdate failed: Method " + update.getMethod() + " Node " + update.getNodeNumber()
							+ " RelatedNode " + update.getRelatedNodeNumber(), t);
				}
			}

		} catch (InterruptedException ex) {
			log.warn("IndexUpdateTask " + id + " interrupted");
		}

		secondaryCache = null;

		log.debug(id + " IndexUpdateTask exits.");
	}

	private void executeEraseIndex() {
		log.debug(id + " Erasing index content created by this repository, this will be fun!");
		erase();
	}

	private void executeDeleteChannelContentIndex(int channel, int contentElement) {
		log.debug(id + " Delete contentelement: " + contentElement + " from channel: " + channel);
		Node node = fetchNode(contentElement);
		if (node != null) {
			Node channelNode = fetchNode(channel);
			if (channelNode != null) {
                List<PageInfo> pages = Search.findPagesForContentElement(node, channelNode);
                if (pages.size() == 0) {
                    log.debug(id + " Unable to find page(s) for channel: '" + channel + "' to delete contentelement from: "
                            + contentElement);
                }
                for (PageInfo info : pages) {
					int pageId = info.getPageNumber();
					delete(String.valueOf(pageId), String.valueOf(contentElement));
				}
			}
		} else {
			delete(null, String.valueOf(contentElement));
		}
	}

	private void executeDeletePageContentIndex(int page, int contentElement) {
		log.debug(id + " Delete page: " + page + " with content: " + contentElement);
		delete(String.valueOf(page), String.valueOf(contentElement));
	}

	private void executeDeletePageIndex(int pageNumber) {
		log.debug(id + " Delete page: " + pageNumber);
		delete(String.valueOf(pageNumber), null);
	}

	private void executeDeleteContentIndex(int ceNumber) {
		log.debug(id + " Delete content: " + ceNumber);
		delete(null, String.valueOf(ceNumber));
	}

	private void executeCreateContentIndex(int nodeNumber) {
		Node node = fetchNode(nodeNumber);
		if (node != null) {
			executeCreateContentIndex(node);
		}
	}

	private void executeCreateContentIndex(Node node) {
		log.debug(id + " Create content index: " + node.getNumber());
		List<PageInfo> pages = Search.findAllDetailPagesForContent(node);
        for (PageInfo info : pages) {
			int pageId = info.getPageNumber();
			create(String.valueOf(pageId), node);
		}
	}

	private void executeUpdateContentIndex(Node node) {
		log.debug(id + " Update content index: " + node.getNumber());
        List<PageInfo> pages = Search.findAllDetailPagesForContent(node);
		if (pages.size() == 0) {
			log.debug(id + " Unable to find page(s) for update of content element: " + node.getNumber());
			delete(null, String.valueOf(node.getNumber()));
		}
        for (PageInfo info : pages) {
            int pageId = info.getPageNumber();
			update(String.valueOf(pageId), node);
		}
	}

	private void executeUpdateContentIndex(int nodeNumber) {
		Node node = fetchNode(nodeNumber);
		if (node != null) {
			executeUpdateContentIndex(node);
		} else {
			delete(null, "" + nodeNumber);
		}
	}

	private void executeUpdateContentChannelIndex(int nodeNumber) {
		log.debug(id + " Update contentchannel index: " + nodeNumber);
		executeUpdateRelatedContent(nodeNumber, "contentelement");
	}

	private void executeUpdateSecondaryContentIndex(int nodeNumber) {
		log.debug(id + " Update secondary content: " + nodeNumber);
		Node node = fetchNode(nodeNumber);
		if (node != null) {
			NodeList ceList = node.getRelatedNodes(ContentElementUtil.CONTENTELEMENT, null, "SOURCE");
			Iterator ceIter = ceList.iterator();
			while (ceIter.hasNext()) {
				Node ceNode = (Node) ceIter.next();
				executeUpdateContentIndex(ceNode.getNumber());
			}
		} else {
			delete(null, "" + nodeNumber);
		}
	}

	private void executeUpdateRelatedContent(int nodeNumber, String type) {
		log.debug(id + " Update related content: " + nodeNumber);
		Node node = fetchNode(nodeNumber);
		if (node != null) {
			NodeList relatedNodes = node.getRelatedNodes(type);
			for (NodeIterator i = relatedNodes.nodeIterator(); i.hasNext();) {
				Node relatedNode = i.nextNode();
				if (relatedNode.getNumber() != nodeNumber) {
					executeUpdateContentIndex(relatedNode.getNumber());
				}
			}
		} else {
			// TODO do nothing for now
		}
	}

	private void executeUpdatePageIndex(int nodeNumber) {
		log.debug(id + " Update page: " + nodeNumber);
		Node node = fetchNode(nodeNumber);
		if (node != null) {
			Set<Node> elementen = Search.findContentElementsForPage(node);
			if (elementen.size() == 0) {
				log.debug(id + " Unable to find content element(s) for update of page: " + nodeNumber);
				delete("" + nodeNumber, null);
			}
			for (Iterator<Node> pIter = elementen.iterator(); pIter.hasNext();) {
				Node element = pIter.next();
				update(node, element);
			}
		} else {
			delete("" + nodeNumber, null);
		}
	}

	private void create(String pageId, Node contentElement) {
		Node pageNode = contentElement.getCloud().getNode(pageId);
		create(pageNode, contentElement);
		pageNode = null;
	}

	private void create(Node pageNode, Node contentElement) {
		Envelope doc = EnvelopeFactory.creatingEnvelope();
		commit(doc, pageNode, contentElement);
	}

	private void update(String pageId, Node contentElement) {
		Node pageNode = contentElement.getCloud().getNode(pageId);
		update(pageNode, contentElement);
		pageNode = null;
	}

	private void update(Node pageNode, Node contentElement) {
		Envelope doc = EnvelopeFactory.updatingEnvelope();
		commit(doc, pageNode, contentElement);
	}

	private void commit(Envelope doc, Node pageNode, Node contentElement) {
		log.debug(id + " Commit page: " + pageNode.getNumber() + " contentelement: " + contentElement.getNumber());

		NodeManager nm = contentElement.getNodeManager();
		String nmName = nm.getName();
		doc.setType(nmName);

		LuceusUtil.nodeFields(contentElement, doc, null);

		// composite the key of the page and the contentelement id's
		String ceId = contentElement.getStringValue(ContentElementUtil.NUMBER_FIELD);
		String pageId = pageNode.getStringValue("number");
		setKeys(doc, ceId, pageId);

		// index the page title as metadata
		doc.add(EnvelopeFieldFactory.getStringField("page.title", pageNode.getStringValue(PagesUtil.TITLE_FIELD)));

		// add pageid and contentelementid as metadata
		doc.add(EnvelopeFieldFactory.getStringField("page.id", pageId));
		doc.add(EnvelopeFieldFactory.getStringField("contentelement.id", ceId));

		// add path to page as metadata
		List path = NavigationUtil.getPathToRoot(pageNode);
		for (Iterator pathIter = path.iterator(); pathIter.hasNext();) {
			Node pathNode = (Node) pathIter.next();
			doc.add(EnvelopeFieldFactory.getStringField(PagesUtil.FRAGMENT_FIELD, pathNode.getStringValue("number")));
		}

		if (module.isDoAttachments()) {
			Set<Node> attachments = Search.findLinkedSecondaryContent(contentElement, "attachments");
			for (Iterator<Node> aIter = attachments.iterator(); aIter.hasNext();) {
				Node attachment = aIter.next();
				if (module.isDoSecondaryWithPrimary()) {
					LuceusUtil.nodeFields(attachment, doc, null);
				}
				commit(doc.duplicate(), attachment);
			}
		}

		if (module.isDoUrls()) {
			Set<Node> urls = Search.findLinkedSecondaryContent(contentElement, "urls");
			for (Iterator<Node> uIter = urls.iterator(); uIter.hasNext();) {
				Node url = uIter.next();
				if (module.isDoSecondaryWithPrimary()) {
					LuceusUtil.nodeFields(url, doc, null);
				}
				commit(doc.duplicate(), url);
			}
		}

		if (module.isDoImages()) {
			Set<Node> images = Search.findLinkedSecondaryContent(contentElement, "images");
			for (Iterator<Node> iIter = images.iterator(); iIter.hasNext();) {
				Node image = iIter.next();
				if (module.isDoSecondaryWithPrimary()) {
					LuceusUtil.nodeFields(image, doc, "image");
				}
				commit(doc.duplicate(), image);
			}
		}

		Indexer idx = module.getIndexer();
		if (idx != null) {
			idx.write(doc);
		}
	}

	private void commit(Envelope doc, Node secondaryContent) {
		if (module.isDoSecondaryAsPrimary()) {
			if (secondaryContent != null) {
				String scId = secondaryContent.getStringValue(ContentElementUtil.NUMBER_FIELD);

				if (secondaryCache != null) {
					if (secondaryCache.contains(scId)) {
						log.debug("secondary content '" + scId + "' already done.");
						return;
					}
				}

				NodeManager nm = secondaryContent.getNodeManager();
				String nmName = nm.getName();
				doc.setType(nmName);

				LuceusUtil.nodeFields(secondaryContent, doc, null);

				// composite the key of the page and the secondary contentelement id's
				doc.setKey("sc" + scId);
				doc.add(EnvelopeFieldFactory.getStringField("contentelement.id", scId));

				Indexer idx = module.getIndexer();
				if (idx != null) {
					if (secondaryCache != null) {
						secondaryCache.add(scId);
					}
					idx.write(doc);
				}
			}
		}
	}

	private void delete(String pageId, String ceId) {
		Envelope doc = EnvelopeFactory.deletingEnvelope();
		setKeys(doc, ceId, pageId);

		Indexer idx = module.getIndexer();
		if (idx != null) {
			idx.write(doc);
		}
	}

	private void erase() {
		Envelope doc = EnvelopeFactory.erasingEnvelope();
		Indexer idx = module.getIndexer();
		if (idx != null) {
			idx.write(doc);
		}
	}

	private void setKeys(Envelope env, String ceId, String pageId) {
		if (pageId != null && pageId.length() > 0) {
			env.setKey("p" + pageId);
		}
		if (ceId != null && ceId.length() > 0) {
			env.setKey("ce" + ceId);
		}
	}

	private Node fetchNode(int nodeNumber) {
		Node node = null;
		try {
			Cloud cloud = module.getAnonymousCloud();
			node = cloud.getNode(nodeNumber);
		} catch (NotFoundException e) {
			log.debug("Cannot find Node: '" + nodeNumber + "'");
		}
		return node;
	}
}
