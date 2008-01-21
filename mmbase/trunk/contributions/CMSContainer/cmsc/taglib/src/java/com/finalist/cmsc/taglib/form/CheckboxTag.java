/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class CheckboxTag extends SimpleTagSupport {

	public String var;
	public String value;
	public Object selected;
	public boolean checked;

	@Override
	public void doTag() throws JspException, IOException {
		PageContext ctx = (PageContext) getJspContext();

		ctx.getOut().print("<input type=\"checkbox\" class=\"checkbox\" style=\"width:15px;\" name=\"" + var + "\" value=\"" + value + "\" ");
		if (isSelected(ctx.getRequest()) == true || this.checked == true) {
			ctx.getOut().print("checked=\"checked\"");
		}
		ctx.getOut().print(">");
		JspFragment frag = getJspBody();
		if (frag != null) {
			frag.invoke(null);
		}
	}

	private boolean isSelected(ServletRequest request) {
		Object selectedValues = request.getAttribute(var);
		if (selectedValues != null) {
			if (selectedValues instanceof String) {
				return ((String) selectedValues).equals(value);
			} else if (selectedValues instanceof String[]) {
				String[] selected = (String[]) selectedValues;
				List<String> selectedItems = Arrays.asList(selected);
				if (selectedItems.contains(value)) {
					return true;
				}
			} else if (selectedValues instanceof Integer) {
				return ((Integer) selectedValues).equals(value);
			} else if (selectedValues instanceof Integer[]) {
				Integer[] selected = (Integer[]) selectedValues;
				List<Integer> selectedItems = new ArrayList<Integer>();
				for (int i = 0; i < selected.length; i++) {
					selectedItems.add(Integer.valueOf(selected[i]));
				}
				if (selectedItems.contains(value)) {
					return true;
				}
			} else if (selectedValues instanceof List) {
				List selectedItems = (ArrayList) selectedValues;
				if (selectedItems != null && selectedItems.size() > 0) {
					for (int i = 0; i < selectedItems.size(); i++) {
						String item = String.valueOf(selectedItems.get(i));
						if (item.equals(value)) {
							return true;
						}
					}

				}
			}
		}
		return false;
	}

	public void setVar(String var) {
		this.var = var;
	}


	public void setSelected(Object selected) {
		this.selected = selected;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}