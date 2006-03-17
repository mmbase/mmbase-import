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

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Date;

public class FSession extends FEvent {

  private FUser user;
  private List tests;
  private String sessionId;

  public FSession(FUser user, Date start, String sessionId) {
    super(start);
    this.user = user;
    user.addSession(this);
    this.sessionId = sessionId;
    tests = new ArrayList();
  }

  public String getSessionId() {
    return sessionId;
  }

  public FUser getUser() {
    return user;
  }

  public FSubtrace getCurrentTest() {
   if ((tests!=null)&&(tests.size()!=0)) {
     FSubtrace test = (FSubtrace) tests.get(tests.size() - 1);
     if (!test.isFinished()) return test; // return active test only
   }
   return null;
  }

  public Iterator getSubtracesIterator() {
    if (tests!=null) return tests.iterator();
    return null;
  }

  public void addSubtrace(FSubtrace test) {
    tests.add(test);
  }

  protected void childrenFinish(Date end, FFinishReason reason) {
    FSubtrace test = getCurrentTest();
    // ignore parent finish date, use its own
    if (test != null) test.finish(test.getActivity(), reason);
  }

  protected void childrenAddPage(FPage page, Date date) {
    // and in current test too
    FSubtrace ctest = getCurrentTest();
    if (ctest==null) return;
    ctest.addPage(page, date);
  }

}
