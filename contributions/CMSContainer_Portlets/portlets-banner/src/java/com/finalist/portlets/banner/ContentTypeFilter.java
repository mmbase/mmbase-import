package com.finalist.portlets.banner;

import java.util.List;
import javax.portlet.RenderRequest;

import com.finalist.cmsc.beans.om.ContentElement;

public interface ContentTypeFilter {
   public void dofilter(RenderRequest request, List<ContentElement> elements);
}
