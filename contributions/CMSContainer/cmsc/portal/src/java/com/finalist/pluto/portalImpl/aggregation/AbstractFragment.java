/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* 

 */

package com.finalist.pluto.portalImpl.aggregation;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finalist.cmsc.services.Parameters;
import com.finalist.pluto.portalImpl.core.PortalURL;

public abstract class AbstractFragment implements Fragment {

	private String id;

	private ServletConfig config;

	private Fragment parent;

	private Parameters initParameters;

	public AbstractFragment(String id, ServletConfig config, Fragment parent) throws Exception {
		StringBuffer compId = new StringBuffer();
		if (parent != null) {
			String parentID = parent.getId();
			if (parentID != null) {
				compId.append(parentID);
				compId.append("_");
			}
		}

		if (id != null) {
			compId.append(id);
			this.id = compId.toString();
		}

		this.config = config;
		this.parent = parent;
	}

	public Fragment getParent() {
		return parent;
	}

	public String getId() {
		return id;
	}

	public Parameters getInitParameters() {
		return initParameters;
	}

	public abstract Collection getChildFragments();

	public abstract void addChild(Fragment child);

	public abstract void createURL(PortalURL url);

	public abstract boolean isPartOfURL(PortalURL url);

	// additional methods.
	public ServletConfig getServletConfig() {
		return config;
	}

    public String getServletContextParameterValue(String name, String defaultValue) {
        String contextValue = getServletConfig().getServletContext().getInitParameter(name);
        if (contextValue != null && !"".equals(contextValue)) {
            return contextValue;
        }
        return defaultValue;
    }

    public RequestDispatcher getMainRequestDispatcher(String resourceName) {
        return getRequestDispatcher("cmsc.portal.layout.base.dir", "/WEB-INF/templates/layout/", resourceName.trim());
    }

    public RequestDispatcher getRequestDispatcher(String contentName, String defaultValue, String resourceName) {
        String root = getServletContextParameterValue(contentName, defaultValue);
        return getServletConfig().getServletContext().getRequestDispatcher(root + resourceName);
    }
    
    
	public String getInitParameterValue(String name) {
		return initParameters.getString(name);
	}

	public abstract void preService(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException;

	public abstract void postService(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException;
}
