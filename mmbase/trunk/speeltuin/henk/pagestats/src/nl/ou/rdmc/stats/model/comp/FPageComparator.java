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
package nl.ou.rdmc.stats.model.comp;

import java.util.Comparator;
import java.util.Iterator;
import nl.ou.rdmc.stats.model.FPage;

//import nl.ou.rdmc.stats.process.IParams;
import nl.ou.rdmc.stats.process.Config;

public class FPageComparator implements Comparator {
  private Config conf;
  public FPageComparator(Config conf) {
    this.conf = conf;
  }

  public int compare(Object o1, Object o2) throws ClassCastException {
    if (!(o1 instanceof FPage)) throw new ClassCastException();
    if (!(o2 instanceof FPage)) throw new ClassCastException();
    FPage fpage1 = (FPage)o1;
    FPage fpage2 = (FPage)o2;
    Iterator it = conf.getLogTagsIterator();
    int result = 0;
    while (it.hasNext()) {
      String tag = (String)it.next();
      result = fpage1.getValueFor(tag).compareTo(fpage2.getValueFor(tag));
      if (result!=0) return result;
    }
    /*
    int result = fpage1.getValueFor(IParams.NAV_PARAM).compareTo(fpage2.getValueFor(IParams.NAV_PARAM));
    if (result!=0) return result;
    result = fpage1.getValueFor(IParams.SUBNAV_PARAM).compareTo(fpage2.getValueFor(IParams.SUBNAV_PARAM));
    if (result!=0) return result;
    result = fpage1.getValueFor(IParams.TEST_PARAM).compareTo(fpage2.getValueFor(IParams.TEST_PARAM));
    if (result!=0) return result;
    result = fpage1.getValueFor(IParams.PAGE_PARAM).compareTo(fpage2.getValueFor(IParams.PAGE_PARAM));
    if (result!=0) return result;
    result = fpage1.getValueFor(IParams.SCHOOLTYPE_PARAM).compareTo(fpage2.getValueFor(IParams.SCHOOLTYPE_PARAM));
    if (result!=0) return result;
    result = fpage1.getValueFor(IParams.COMPETENCE_PARAM).compareTo(fpage2.getValueFor(IParams.COMPETENCE_PARAM));
    if (result!=0) return result;
    result = fpage1.getValueFor(IParams.CORETASK_PARAM).compareTo(fpage2.getValueFor(IParams.CORETASK_PARAM));
    if (result!=0) return result;
    result = fpage1.getValueFor(IParams.TESTLEVEL_PARAM).compareTo(fpage2.getValueFor(IParams.TESTLEVEL_PARAM));
    if (result!=0) return result;
    result = fpage1.getValueFor(IParams.TESTTYPE_PARAM).compareTo(fpage2.getValueFor(IParams.TESTTYPE_PARAM));
    */
    return result;
  }

}
