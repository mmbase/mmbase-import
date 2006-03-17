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
package nl.ou.rdmc.stats;

import nl.ou.rdmc.stats.process.*;

import java.io.*;

public class LogProcessor {

  private Config conf;
  private ModelBuilder modelBuilder;
  private FileParser fileParser;
  private LogViewer logViewer;

  public LogProcessor(String config_file) {
    ConfigBuilder cbuilder = new ConfigBuilder(config_file);
    conf = cbuilder.getConfig();
    //conf = StaticConfigBuilder.conf;
    modelBuilder = new ModelBuilder(conf);
    fileParser = new FileParser(modelBuilder, conf);
    logViewer = new LogViewer(modelBuilder, System.out);
  }

  private void parse() {
    String logdirPath = conf.getLogDir();
    File logdir = new File(logdirPath);
    String [] logfiles = logdir.list();
    if ((logfiles==null)||(logfiles.length==0)) {
      System.out.println("Wrong or empty logdir: "+logdirPath);
      return;
    }

    String filenamePrefix = conf.getFileNamePrefix();
String fileExtension = conf.getFileExt();

for(int i=0; i<logfiles.length;i++) {
   String fileName = logfiles[i];
   if ( ( (filenamePrefix==null) || (fileName.indexOf(filenamePrefix)==0) ) &&
        ( (fileExtension==null) || (fileName.lastIndexOf(fileExtension)==(fileName.length()-fileExtension.length()) ) )) {
                File file = new File(logdirPath, fileName);
                fileParser.parse(file);
   }
           }


    for(int i=0; i<logfiles.length;i++){
      File file = new File(logdirPath, logfiles[i]);
      //System.out.println("Parse: "+file.getPath());
      fileParser.parse(file);
    }
  }

  private void view() {
    logViewer.view();
  }

  public static void main(String[] args) {

    if ((args==null) || (args.length==0)) {
      System.out.println("Usage: LogProcessor _path_to_config_file\n");
      return;
    }
    LogProcessor logProcessor = new LogProcessor(args[0]);
    logProcessor.parse();
    logProcessor.view();

  }

}
