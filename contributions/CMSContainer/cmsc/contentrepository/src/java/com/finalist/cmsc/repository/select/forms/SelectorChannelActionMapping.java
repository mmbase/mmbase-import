/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository.select.forms;

import com.finalist.cmsc.struts.SelectorActionMapping;

@SuppressWarnings("serial")
public class SelectorChannelActionMapping extends SelectorActionMapping {

   private boolean contentChannelOnly;


   public boolean isContentChannelOnly() {
      return contentChannelOnly;
   }


   public void setContentChannelOnly(boolean contentChannelOnly) {
      this.contentChannelOnly = contentChannelOnly;
   }

}
