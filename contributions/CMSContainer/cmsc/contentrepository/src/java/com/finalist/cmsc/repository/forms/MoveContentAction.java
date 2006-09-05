/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.struts.action.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseAction;


public class MoveContentAction extends MMBaseAction {

	
    private static final String PARAMETER_CHANNEL = "parentchannel";
	private static final String PARAMETER_NUMBER = "objectnumber";
	private static final String PARAMETER_DIRECTION = "direction";
	
	private static final String DIRECTION_DOWN = "down";
	private static final String DIRECTION_UP = "up";

	@Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {

		int number = Integer.parseInt(request.getParameter(PARAMETER_NUMBER));
		String direction = request.getParameter(PARAMETER_DIRECTION);
		int channel = Integer.parseInt(request.getParameter(PARAMETER_CHANNEL));
		
		if(direction.equals(DIRECTION_UP)) {
			moveUp(number, channel);
		}
		if(direction.equals(DIRECTION_DOWN)) {
			moveDown(number, channel);
		}
		
		String path = mapping.findForward(SUCCESS).getPath()+"?"+PARAMETER_CHANNEL+"="+channel+"&direction=down";
    	return new ActionForward(path);
    }

	private void moveDown(int number, int channel) {
		
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		NodeList nodeList = cloud.getList(""+channel,"contentchannel,contentrel,contentelement", "contentrel.number,contentrel.pos,contentelement.number", null, "contentrel.pos", "down", null, false);
		
		swap(nodeList, number);
	}

	private void moveUp(int number, int channel) {

		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		NodeList nodeList = cloud.getList(""+channel,"contentchannel,contentrel,contentelement", "contentrel.number,contentrel.pos,contentelement.number", null, "contentrel.pos", "up", null, false);
		
		swap(nodeList, number);
	}

	private void swap(NodeList nodeList, int number) {
		for(NodeIterator ni = nodeList.nodeIterator(); ni.hasNext();) {
			Node node = ni.nextNode();
			int nodeNumber = node.getIntValue("contentelement.number");
			if(nodeNumber == number) {
				swap(ni.nextNode().getIntValue("contentrel.number"), node.getIntValue("contentrel.number"));
				break;  //(out of the for loop)
			}
		}
	}
	
	private void swap(int posrelNumber1, int posrelNumber2) {
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		Node posrelNode1 = cloud.getNode(posrelNumber1);
		Node posrelNode2 = cloud.getNode(posrelNumber2);
		int oldPosrel1Pos = posrelNode1.getIntValue("pos");
		posrelNode1.setIntValue("pos", posrelNode2.getIntValue("pos"));
		posrelNode1.commit();
		posrelNode2.setIntValue("pos", oldPosrel1Pos);
		posrelNode2.commit();
	}


}
