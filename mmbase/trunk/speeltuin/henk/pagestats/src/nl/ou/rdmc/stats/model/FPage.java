/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is the
 * Ruud de Moor Centrum of the Open University.
 */
package nl.ou.rdmc.stats.model;

import java.util.Map;

//import nl.ou.rdmc.stats.process.IParams;
import nl.ou.rdmc.stats.process.Config;
import java.util.Iterator;

public class FPage {

  private Map pageMap;
  //private int type;
  private int hashcode;

  int pageviews;

  private Config conf;

  public FPage(Map pageMap, Config conf) {
  //public FPage(int type, Map pageMap) {
    //this.type = type;
    this.conf = conf;
    this.pageMap = pageMap;
    hashcode = calculateHashCode();
    pageviews = 1;
  }

  public String getValueFor(String param) {
    String value = (String)pageMap.get(param);
    if (value==null) return "&nbsp;";
    return value;
  }

  private int calculateHashCode() {
    // first we concatenate strings with sequence pointed in IParams.PARAMS
    // if some field is empty we use '*' instead
    StringBuffer sb = new StringBuffer();
    //sb.append(type).append(";");

    Iterator it = conf.getLogTagsIterator();
    while (it.hasNext()) {
      String tag = (String)it.next();
      if (pageMap.containsKey(tag)) sb.append(pageMap.get(tag));
      else sb.append("*");
      sb.append(";");
    }
    /*
    for (int i=0;i<IParams.PARAMS.length;i++) {
      if (pageMap.containsKey(IParams.PARAMS[i])) sb.append(pageMap.get(IParams.PARAMS[i]));
      else sb.append("*");
      sb.append(";");
    }
    */
    return sb.toString().hashCode();
  }

  public int hashCode() {
    return hashcode;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof FPage)) return false;
    return (hashcode == ((FPage)obj).hashcode);
  }


  public void incViews() {
    pageviews++;
  }

  public int getViewsNumber() {
    return pageviews;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    Iterator it = conf.getLogTagsIterator();
    while (it.hasNext()) {
      String tag = (String)it.next();
      if (pageMap.get(tag)!=null) sb.append(tag).append("=").append(getValueFor(tag)).append("; ");
    }
    /*
    if (pageMap.get(IParams.NAV_PARAM)!=null) sb.append(IParams.NAV_PARAM).append("=").append(getValueFor(IParams.NAV_PARAM)).append("; ");
    if (pageMap.get(IParams.SUBNAV_PARAM)!=null) sb.append(IParams.SUBNAV_PARAM).append("=").append(getValueFor(IParams.SUBNAV_PARAM)).append("; ");
    if (pageMap.get(IParams.TEST_PARAM)!=null) sb.append(IParams.TEST_PARAM).append("=").append(getValueFor(IParams.TEST_PARAM)).append("; ");
    if (pageMap.get(IParams.PAGE_PARAM)!=null) sb.append(IParams.PAGE_PARAM).append("=").append(getValueFor(IParams.PAGE_PARAM)).append("; ");
    if (pageMap.get(IParams.SCHOOLTYPE_PARAM)!=null) sb.append(IParams.SCHOOLTYPE_PARAM).append("=").append(getValueFor(IParams.SCHOOLTYPE_PARAM)).append("; ");
    if (pageMap.get(IParams.COMPETENCE_PARAM)!=null) sb.append(IParams.COMPETENCE_PARAM).append("=").append(getValueFor(IParams.COMPETENCE_PARAM)).append("; ");
    if (pageMap.get(IParams.CORETASK_PARAM)!=null) sb.append(IParams.CORETASK_PARAM).append("=").append(getValueFor(IParams.CORETASK_PARAM)).append("; ");
    if (pageMap.get(IParams.TESTLEVEL_PARAM)!=null) sb.append(IParams.TESTLEVEL_PARAM).append("=").append(getValueFor(IParams.TESTLEVEL_PARAM)).append("; ");
    if (pageMap.get(IParams.TESTTYPE_PARAM)!=null) sb.append(IParams.TESTTYPE_PARAM).append("=").append(getValueFor(IParams.TESTTYPE_PARAM)).append("; ");
    */
    return sb.toString();
  }


  public String toStringWith(String name, String start) {
    StringBuffer sb = new StringBuffer();
    sb.append(Config.USER_PARAM).append("=").append(name).append("; ");
    sb.append(Config.DATE_PARAM).append("=").append(start).append("; ");
    sb.append(toString());
    return sb.toString();
  }

}
