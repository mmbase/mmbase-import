/*
 *  Pager Tag Library
 *
 *  Copyright (C) 2002  James Klicman <james@jsptags.com>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jsptags.navigation.pager;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public abstract class JumpTagSupport extends PageTagSupport {

	static final String
	    CURRENT = "current",
	    INDEXED = "indexed";

	private String unless = null;

	public final void setUnless(String value) throws JspException {
		if (!(value == null || CURRENT.equals(value) || INDEXED.equals(value)))
		{
			throw new JspTagException("value for attribute \"unless\" " +
				"must be either \"current\" or \"indexed\".");
		}
		unless = value;
	}

	public final String getUnless() {
		return unless;
	}

	public int doStartTag() throws JspException {
		super.doStartTag();

		int jumpPage = getJumpPage();

		if (CURRENT.equals(unless)) {
			if (jumpPage == pagerTag.getPageNumber())
				return SKIP_BODY;
		} else if (INDEXED.equals(unless)) {
			int firstPage = pagerTag.getFirstIndexPage();
			int lastPage = pagerTag.getLastIndexPage(firstPage);

			if (jumpPage >= firstPage && jumpPage <= lastPage)
				return SKIP_BODY;
		}

		setPageAttributes(jumpPage);

		return EVAL_BODY_INCLUDE;
	}

	public void release() {
		unless = null;
		super.release();
	}

	protected abstract int getJumpPage();
}

/* vim:set ts=4 sw=4: */
