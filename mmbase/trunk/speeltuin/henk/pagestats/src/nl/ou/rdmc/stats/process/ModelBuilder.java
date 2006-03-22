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
package nl.ou.rdmc.stats.process;

import java.util.*;

import nl.ou.rdmc.stats.model.*;
import nl.ou.rdmc.stats.model.comp.FPageComparator;
import nl.ou.rdmc.stats.process.Config;
import org.mmbase.util.logging.*;

//import nl.ou.rdmc.stats.ILogger;
//import nl.ou.rdmc.stats.SystemLogger;

public class ModelBuilder {

  private static final Logger log = Logging.getLoggerInstance(ModelBuilder.class);

    //private static final ILogger log = new SystemLogger();

  private Map userMap;
  private Map pages;
  private Config conf;

  private boolean isSomeFilesParsed = false;

  public ModelBuilder(Config conf) {
    userMap = new TreeMap(); // key for it is String so sorting it in alphabet order by default
    this.conf = conf;
    pages = new TreeMap(new FPageComparator(conf));
  }

  public Config getConfig() {
    return conf;
  }

  public void isSomeFilesParsed(boolean is) {
    isSomeFilesParsed = is;
  }
  public boolean isSomeFilesParsed() {
    return isSomeFilesParsed;
  }

  public void addPage(Map pMap, LineInfo li) {
    String name = li.getUser();
    Date date = li.getDate();

    // first create temporary page and try see: is it first occurence of this page
    FPage temp = new FPage(pMap, conf);
    if (pages.containsKey(temp)) {
      // or it is next occurences then simple increment original
      FPage original = (FPage)pages.get(temp);
      temp = original; // save link to use below
      original.incViews(); // important: we use values of map
    } else {
      pages.put(temp,temp);
    }
    // then register this page in user's session
    FUser user = (FUser)userMap.get(name); // here we can get null dont worry about it
    if (user==null) return;
    FSession session = tryToGetSession(li, user);
    if (session == null) return;
    session.addPage(temp, date);
  }

  public Iterator getPagesIterator() {
    return pages.values().iterator(); // important: we use values of map
  }


  // must call it for EVERY log line AFTER any another handlers
  public void updateActivity(LineInfo li) {
    String name = li.getUser();
    if(name!=null) {
       FUser user = (FUser)userMap.get(name);
       if (user!=null) {
          Date date = li.getDate();
          if(date!=null) {
            user.updateActivity(date);
            FSession session = tryToGetSession(li, user);
            if (session!=null) session.updateActivity(date);
          } else {
            log.error("No date found in " + li);
          }
       } else {
         log.error("Could not get user for name " + name + " in " + li);
         log.error("This probably means that this user's session did not start with an indexEvent()");
       }
    } else {
      log.error("No name found in " + li);
    }
  }


  // can represent user login event or just returning to index
  public void indexEvent(LineInfo li) {
    log.info("indexEvent for LineInfo " + li);
    String name = li.getUser();
    Date date = li.getDate();
    FUser user = (FUser)userMap.get(name);
    if (user == null) {
      user = new FUser(name, date);
      userMap.put(name, user);
    }
    String sessionId = li.getSessionId();
    FSession session = null;
    if (sessionId!=null) {
      // use detect session by id method: can have multiply sessions in one time!
      boolean isFoundSameSession = false;
      Iterator sessionIterator = user.getSessionsIterator();
      if (sessionIterator!=null) {
        for (Iterator it = sessionIterator; it.hasNext(); ) {
          session = (FSession) it.next();
          if (session.isFinished())continue;
          if (session.getSessionId().equals(sessionId)) { // found same session do nothing NOTE: we try not close it!!!
            isFoundSameSession = true;
          } else {
            tryFinishSession(session, user, date, false); // not create new
          }
        }
      }
      if (!isFoundSameSession) {
        session = new FSession(user, date, sessionId);
      }
      return; // do nothing below
    } else {
      // one-in-time session model
      session = user.getCurrentSession();
      if (session == null) { // nothing active
        session = new FSession(user, date, li.getSessionId());
        return; // do nothing below
      }
      // try finish current and create new
      tryFinishSession(session, user, date, true); // true: create new
    }

  }

  private void tryFinishSession(FSession session, FUser user, Date date, boolean createNew) {
    Calendar now = Calendar.getInstance(); now.setTime(date);
    Calendar lastAct = Calendar.getInstance(); lastAct.setTime(user.getActivity());

    // check new day
    if (lastAct.get(Calendar.DAY_OF_MONTH) != now.get(Calendar.DAY_OF_MONTH)) {
      session.finish(user.getActivity(), FFinishReason.NEWDAY); // finish previous
      if (createNew )session = new FSession(user, date, null); // and create new
      return;
    }

    // check timeout
    lastAct.roll(Calendar.HOUR_OF_DAY, 1); // timeout time (one hour after last activity)
    if (lastAct.before(now)) {
      session.finish(user.getActivity(), FFinishReason.TIMEOUT); // finish previous
      if (createNew ) session = new FSession(user, date, null); // and create new
      return;
    }

  }


  private FSession tryToGetSession(LineInfo li, FUser user) {
    // try to get session
    String sessionId = li.getSessionId();
    FSession session = null;
    if (sessionId!=null) {
      // use detect session by id method: can have multiply sessions in one time!
      for (Iterator it = user.getSessionsIterator(); it.hasNext(); ) {
        session = (FSession) it.next();
        if (session.getSessionId().equals(sessionId)&&!session.isFinished()) return session; // found same session: will use it
      }
      return null; // cannot works with unopened session
    } else {
      // one-in-time session model
      session = user.getCurrentSession();
      return session; // may be null
    }
  }

  public void subtraceEvent(LineInfo li, String subtrace, String value) {
    log.info("subtraceEvent for LineInfo " + li + " with subtrace="+subtrace+" and value="+value);
    String name = li.getUser();
    Date date = li.getDate();
    // lets try start test
    FUser user = (FUser)userMap.get(name);
    if (user==null) return; // cannot works with unlogined user
    // try to get session
    FSession session = tryToGetSession(li, user);
    if (session==null) return; // cannot works with unopened session

    // ok works with test
    FSubtraceType type = (FSubtraceType)conf.types.get(subtrace);
    FSubtrace test = session.getCurrentTest();

    if (test==null) {
      if (type.isLastFromSubtrace(value)) {
        // protect against double last value occuring
        // when it came first we finish current test. if it came second we couldn't start new one!
        return;
      }
      // start new test
      test = new FSubtrace(session, type, date);
    } else {
      FSubtraceType curType = test.getTestType();
      if (curType.equals(type)) {
        // user just continue current test
        test.updateActivity(date); // IMPORTANT
        if (type.isLastFromSubtrace(value)) { // finish current test
          test.finish(date, FFinishReason.SUBTRACEPASS);
        }
      } else {
        // user gone to new test
        test.finish(date,FFinishReason.NEWSUBTRACECAME);
        test = new FSubtrace(session, type, date);
      }
    }
  }

  public void fileEndEvent() {
    // we need to check all users
    for (Iterator it = userMap.values().iterator(); it.hasNext();) {
      FUser user = (FUser) it.next();
      // and close all sessions for him
      for (Iterator it2 = user.getSessionsIterator(); it2.hasNext(); ) {
        FSession session = (FSession) it2.next();
        if (session.isFinished()) continue;
        session.finish(session.getActivity(), FFinishReason.FILEEND); // finish with date last sessions's activity
      }
    }
  }

  public Iterator getModelIterator() {
    return userMap.values().iterator();
  }

  public FUser getUser(String name) {
    if (name==null) return null;
    return (FUser)userMap.get(name);
  }

}
