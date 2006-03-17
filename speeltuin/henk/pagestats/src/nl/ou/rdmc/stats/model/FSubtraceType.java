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
import nl.ou.rdmc.stats.model.comp.FSubtraceComparator;
import nl.ou.rdmc.stats.process.Config;

public class FSubtraceType  {

/*
  public static final FSubtraceType Observatieopdracht = new FSubtraceType(0);
  public static final FSubtraceType Analyseopdracht = new FSubtraceType(1);
  public static final FSubtraceType Completeeropdracht = new FSubtraceType(2);
  public static final FSubtraceType Praktijkopdracht = new FSubtraceType(3);

  public static final Map TESTTYPES = new HashMap();
  static {
    TESTTYPES.put(OBSERVATIE_1_TT,Observatieopdracht);
    TESTTYPES.put(ANALYSE_1_TT,Analyseopdracht);
    TESTTYPES.put(COMPLETEER_1_TT,Completeeropdracht);
    TESTTYPES.put(PRAKTIJK_1_TT,Praktijkopdracht);
  }
*/

  //private int id;
  private String name;
  private Config conf;

  private TreeSet tests;
  private TreeMap testsByIDs;

/*
  private FSubtraceType(int id) {
    this.id = id;
  }
*/

  public FSubtraceType(Config conf, String name) {
    this.conf = conf;
    this.name = name;
    tests = new TreeSet(new FSubtraceComparator());
    testsByIDs = new TreeMap();
  }


  public String toString() {
    return name;
    /*switch (id) {
      case 0: return OBSERVATIE_1_TT;
      case 1: return ANALYSE_1_TT;
      case 2: return COMPLETEER_1_TT;
      case 3: return PRAKTIJK_1_TT;
    }
    return null;*/
  }


  public String[] getPagesForIt() {
    return conf.getSubtracesArrFor(name);

    /*
    switch (id) {
      case 0: return OBSERVATIE_ARR;
      case 1: return ANALYSE_ARR;
      case 2: return COMPLETEER_ARR;
      case 3: return PRAKTIJK_ARR;
    }
    return null;*/
  }


  public boolean equals(Object obj) {
    if (!(obj instanceof FSubtraceType)) return false;
    //return (id == ((FSubtraceType)obj).id);
    return name.equals( ((FSubtraceType)obj).name );
  }

  public void addSubtrace(FSubtrace test) {
    tests.add(test);
    testsByIDs.put(test.getId(),test);

  }

  public int getSubtraceNumber() {
    if (tests!=null) return tests.size();
    return 0;
  }

  public Iterator getSubtracesIterator() {
    if (tests!=null) return tests.iterator();
    return null;
  }


  public FSubtrace getSubtrace(Long id) {
    if (testsByIDs==null) return null;
    return (FSubtrace)testsByIDs.get(id);
  }

  public String getSubtraceAverageLength() {
    int result = 0;
    int count = 0;
    for (Iterator it=tests.iterator(); it.hasNext(); count++) {
      result += ((FSubtrace)it.next()).getLengthInSecunds();
    }
    if (count != 0)
      result = Math.round(result/count);
    else
      return "00:00:00";
    int hours = result / 3600;
    int mins_n_secs = result % 3600;
    int mins = mins_n_secs / 60;
    int secs = mins_n_secs % 60;
    return FEvent.timeToString(hours, mins, secs);
  }


  public boolean isLastFromSubtrace(String page) {
    String[] pages = getPagesForIt();
    if (pages.length==0) return false;
    if (page.equals( pages[pages.length-1]  )) return true; // last page from this test
    return false;
  }

}
