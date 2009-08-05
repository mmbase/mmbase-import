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
import nl.ou.rdmc.stats.model.FSubtraceType;

import org.mmbase.util.logging.*;

//import nl.ou.rdmc.stats.ILogger;
//import nl.ou.rdmc.stats.SystemLogger;

public class Config  {

  private static final Logger log = Logging.getLoggerInstance(FileParser.class);
   //private static final ILogger log = new SystemLogger();

  public final static String DATE_PARAM = "date";
  public final static String USER_PARAM = "user";

  public final static String IP_PARAM = "ip";
  public final static String SESSIONID_PARAM = "sessionid";

  private List logTags;

  private Map tagsForSubtraces; // map of usetag by subtrace names
  private Map subtraces; // map of values list by subtrace names
  public Map types; // map of FSubtraceType by subtrace names


  private String filenameprefix;
  private String fileext;
  private String logdir;

  private String delimiter;
  private String sessionStartTag;
  private String sessionStartValue;

  public Config() {
    logTags = new ArrayList();
    subtraces = new HashMap();
    types = new HashMap();
    tagsForSubtraces = new HashMap();
  }

  public void setFileNamePrefix(String value) {
    this.filenameprefix = value;
  }
  public String getFileNamePrefix() {
    return filenameprefix;
  }

  public void setFileExt(String value) {
    this.fileext = value;
  }
  public String getFileExt() {
    return fileext;
  }

  public void setLogDir(String value) {
    this.logdir = value;
  }
  public String getLogDir() {
    return logdir;
  }

  void addLogTag(String logTag) {
    logTags.add(logTag);
  }
  public Iterator getLogTagsIterator() {
    return logTags.iterator();
  }

  public String getSessionStartTag() {
    return sessionStartTag;
  }
  public String getSessionStartValue() {
    return sessionStartValue;
  }
  void setSessionStartTag(String tag) {
    sessionStartTag = tag;
  }
  void setSessionStartValue(String value) {
    sessionStartValue = value;
  }

  public String getTagForSubtrace(String subtrace) {
    return (String)tagsForSubtraces.get(subtrace);
  }
  void setTag(String subtrace, String tag) {
    tagsForSubtraces.put(subtrace, tag);
  }
  public Iterator getTagsForSubtraceIterator() {
    return tagsForSubtraces.keySet().iterator();
  }

  void addSubtrace(String subtrace) {
    if (!subtraces.containsKey(subtrace)) {
      subtraces.put(subtrace, new ArrayList());
      types.put(subtrace, new FSubtraceType(this, subtrace));
    }
  }
  void addSubtraceValue(String subtrace, String value) {
    if (!subtraces.containsKey(subtrace)) addSubtrace(subtrace);
    ((List)subtraces.get(subtrace)).add(value);
  }


  public String[] getSubtracesArrFor(String subtrace) {
    if (subtraces.containsKey(subtrace)) {
      List list = (List)subtraces.get(subtrace);
      return (String[])list.toArray( new String[] {} );
    }
    return null;
  }

  public List getSubtracesFor(String subtrace) {
    if (subtraces.containsKey(subtrace)) {
      return (List)subtraces.get(subtrace);
    }
    return null;
  }


  public String getDelimiter() {
    return delimiter;
  }
  void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public void logConfig() {

    log.info("Delimiter: '"+getDelimiter()+"'");

    log.info("Session start: '"+getSessionStartTag()+"' value: '"+getSessionStartValue()+"'");

    log.info("LogTags:");
    for (Iterator it=logTags.iterator(); it.hasNext();) {
      log.info((String)it.next());
    }
    log.info("Subtraces:");
    for (Iterator it=subtraces.keySet().iterator(); it.hasNext();) {
      String name = (String)it.next();
      log.info("name='"+name+"'");
      log.info("usetag='"+(String)tagsForSubtraces.get(name)+"'");
      List values = (List)subtraces.get(name);
      for (Iterator it2=values.iterator(); it2.hasNext();) {
        log.info("value='"+(String)it2.next()+"'");
      }
    }

  }

}
