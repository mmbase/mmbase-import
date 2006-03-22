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
import java.io.*;

import nl.ou.rdmc.stats.process.Config;
import org.mmbase.util.logging.*;

//import nl.ou.rdmc.stats.ILogger;
//import nl.ou.rdmc.stats.SystemLogger;

public class FileParser {

  private static final Logger log = Logging.getLoggerInstance(FileParser.class);
   //private static final ILogger log = new SystemLogger();

  private ModelBuilder builder;
  private Map fileSizeMap;
  private Config conf;


  public FileParser(ModelBuilder builder, Config conf) {
    this.builder = builder;
    fileSizeMap = new HashMap();
    this.conf = conf;
  }

  public void parse(File file) {
   log.info("Parsing file " + file);
   String fName = file.getName();
   Long fSize = new Long(file.length());
   Long checkSize = (Long) fileSizeMap.get(fName);
   if ((checkSize==null) || !checkSize.equals(fSize)) {
     // it is new or growing-now file lets process it
     try {
       BufferedReader in = new BufferedReader(new FileReader(file));
       String line;
       while((line=in.readLine())!=null) {
         parseLine(line);
       }
     } catch (FileNotFoundException nfe) {
         log.error(nfe.getMessage());
     } catch (IOException ioe) {
         log.error(ioe.getMessage());
     }
     builder.fileEndEvent();
     fileSizeMap.put(fName, fSize);
   }

  }

  private void parseLine(String line) {
    if (line.trim().indexOf("#")==0) return; // scip comments
    log.info("Parsing line " + line);
    String[] result = line.split(conf.getDelimiter());
    if ((result==null)||(result.length<2)) {
      log.error("This log line has a wrong format") ;
      return;
    }
    Map pMap = new HashMap();
    LineInfo li = new LineInfo();
    //int pType = 0;
    /*String dateStr = null;
    String userStr = null;
    String ipStr = null;
    String sessionStr = null;
    Date date = null;*/
    for (int i=0; i<result.length; i++) {
      String p[] = result[i].split("=");
      /*if (p[0].equals(IParams.USER_PARAM)) pType += IParams.USER_CT;
      if (p[0].equals(IParams.CORETASK_PARAM)) pType += IParams.TASK_CT;
      if (p[0].equals(IParams.PAGE_PARAM)) pType += IParams.PAGE_CT;*/
      if (p[0].equals(Config.DATE_PARAM)) {
         li.setDateStr(p[1]);
      } else if (p[0].equals(Config.USER_PARAM)) {
         li.setUserStr(p[1]);
      } else if (p[0].equals(Config.IP_PARAM)) {
         li.setIpStr(p[1]);
      } else if (p[0].equals(Config.SESSIONID_PARAM)) {
         li.setSessionStr(p[1]);
      } else {
         pMap.put(p[0],p[1]); // not put date, user, ip and session
      }
    }
    if (!li.validate()) return;
    //  works on events first
    // because, perhaps, we need to create user and session first before register page inside it

    if ( ((String)pMap.get(conf.getSessionStartTag())).equals(conf.getSessionStartValue()) ) {
      builder.indexEvent(li);
    }
    // check if the line contains a usetag and the value of it is equal to the subtrace
    for(Iterator it=conf.getTagsForSubtraceIterator();it.hasNext();) {
      String subtrace = (String)it.next();
      String usetag = conf.getTagForSubtrace(subtrace);
      List values = conf.getSubtracesFor(subtrace);
      String value = ((String)pMap.get(usetag));
      if ((value!=null) && (values!=null) && values.contains(value)) {
        builder.subtraceEvent(li, subtrace, value);
      }
    }
    // and for finishing every line must update activity!
    builder.updateActivity(li);

    // then register page view in common map and inside user session
    //builder.addPage(pMap, pType, userStr, date);
    builder.addPage(pMap, li);

  }

}
