package com.finalist.cmsc.egemmail.forms;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.KeywordUtil;
import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class EgemSearchAction extends MMBaseAction {

    private static final String RESULTS = "results";
    private static final String RESULT_COUNT = "resultCount";
	private static final int MAX_RESULTS = 200;
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {
		
		EgemSearchForm searchForm = (EgemSearchForm) form;
        NodeManager nodeManager = cloud.getNodeManager(ContentElementUtil.CONTENTELEMENT);

        NodeQuery query = nodeManager.createQuery();
        SearchUtil.addLimitConstraint(query, 0, MAX_RESULTS*2);
        
        
        if(!StringUtil.isEmpty(searchForm.getTitle())) {
        	Field field = nodeManager.getField("title");
            SearchUtil.addLikeConstraint(query, field, searchForm.getTitle());
        }
        
        if(!StringUtil.isEmpty(searchForm.getAuthor())) {
            SearchUtil.addEqualConstraint(query, nodeManager, "creator", searchForm.getAuthor());
        }
        
        if (!StringUtil.isEmpty(searchForm.getKeywords())) {
            List<String> keywords = KeywordUtil.getKeywords(searchForm.getKeywords());
        	Field field = nodeManager.getField("keywords");
            for (Iterator<String> i = keywords.iterator(); i.hasNext();) {
                SearchUtil.addLikeConstraint(query, field, i.next());
            }
        }
        
        
        NodeList results = nodeManager.getList(query);
        if(results.size() > 0) {
        	removeUnpublished(cloud, results);
        }
        
        request.setAttribute(RESULT_COUNT, results.size());
        request.setAttribute(RESULTS, results);
		
		return mapping.findForward(SUCCESS);
	}

	
	private void removeUnpublished(Cloud cloud, NodeList results) {
		StringBuffer constraints = new StringBuffer("sourcenumber in (");
		for(NodeIterator ni = results.nodeIterator(); ni.hasNext();) {
			Node next = ni.nextNode();
			constraints.append(next.getNumber());
			if(ni.hasNext()) {
				constraints.append(",");
			}
		}
		constraints.append(")");
		
		NodeList remoteNodes = cloud.getNodeManager("remotenodes").getList(constraints.toString(), null, null);
		
		HashSet<Integer> remoteNumbers = new HashSet<Integer>();
		for(NodeIterator ni = remoteNodes.nodeIterator(); ni.hasNext();) {
			Node next = ni.nextNode();
			remoteNumbers.add(new Integer(next.getStringValue("sourcenumber")));
		}
		

		int found = 0;
		for(NodeIterator ni = results.nodeIterator(); ni.hasNext();) {
			Node next = ni.nextNode();
			if(found >= MAX_RESULTS || !remoteNumbers.contains(next.getNumber())) {
				ni.remove();
			}
			else {
				found ++;
			}
		}
		
		
	}
}
