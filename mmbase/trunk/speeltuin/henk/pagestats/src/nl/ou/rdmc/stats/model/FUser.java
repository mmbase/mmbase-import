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

import java.util.*;

public class FUser {

  private String name;
  private TreeMap sessions;

  private Date activity;

  public FUser(String name, Date date) {
    this.name = name;
    activity = date;
    sessions = new TreeMap();
  }

  public String getName() {
    return name;
  }

  public FSession getSession(Long id) {
    if ((sessions==null)||(sessions.size()==0)) return null;
    return (FSession)sessions.get(id);
  }

  public void updateActivity(Date date) {
    activity = date;
  }

  public Date getActivity() {
    return activity;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof FUser)) return false;
    return name.equals( ((FUser)obj).name );
  }

  public void addSession(FSession session) {
    sessions.put(session.getId(), session);
  }

  public FSession getCurrentSession() {
   if ((sessions!=null)&&(sessions.size()!=0)) {
     FSession session = (FSession) sessions.get(sessions.lastKey());
     if (!session.isFinished()) return session; // return active session only
   }
   return null;
  }

  public Iterator getSessionsIterator() {
    if (sessions!=null) return sessions.values().iterator();
    return null;
  }

  public Iterator getSubtracesIterator() {
    if (sessions!=null) {
      List tests = new ArrayList();
      for (Iterator it = sessions.values().iterator(); it.hasNext(); ) {
        FSession session = (FSession)it.next();
        for (Iterator it2 = session.getSubtracesIterator(); it2.hasNext(); ) {
          tests.add( (FSubtrace)it2.next() );
        }
      }
      return tests.iterator();
    }
    return null;
  }


  public int getSessionsNumber() {
    if (sessions!=null) return sessions.size();
    return 0;
  }

  public String getFirstSessionDate() {
    if ((sessions != null)&&(sessions.size()!=0)) {
      FSession session = (FSession)sessions.get(sessions.firstKey());
      if (session!=null)
        return session.getStart();
    }
    return "*";
  }

  public String getLastSessionDate() {
    if ((sessions != null)&&(sessions.size()!=0)) {
      FSession session = (FSession)sessions.get(sessions.lastKey());
      if (session!=null)
        return session.getStart();
    }
    return "*";
  }


}
