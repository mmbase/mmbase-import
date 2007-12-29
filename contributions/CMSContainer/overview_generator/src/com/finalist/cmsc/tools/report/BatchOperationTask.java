package com.finalist.cmsc.tools.report;

import org.apache.tools.ant.Task;

import java.io.*;
import java.util.*;

public class BatchOperationTask extends Task {
    protected String configfile;
    protected String dest;
    protected String targetReportFileLocation;


    public void setConfigfile(String configfile) {
        this.configfile = configfile;
    }

    public void setTargetReportFileLocation(String targetReportFileLocation) {
        this.targetReportFileLocation = targetReportFileLocation;
    }

    public void setDest(String dest) {
        this.dest = dest;

        if (!dest.endsWith(File.separator)) {
            this.dest = dest + File.separator;
        }
    }

    public List<VCSConfig> getConfigs() {
        File configFile = new File(configfile);
        List<VCSConfig> configs = new ArrayList<VCSConfig>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            String str;
            while ((str = reader.readLine()) != null) {
                if (str.startsWith("#")||(!str.startsWith("cvs")&&!str.startsWith("svn"))) continue;


               VCSConfig config = null;

                if(str.startsWith("cvs")) {
                   config = getCVSConfig(str);
                }

                if(str.startsWith("svn")) {
                   config = getSVNConfig(str);
                }

               config.setWorkingfolder(this.dest);
               configs.add(config);
            }
        } catch (FileNotFoundException e) {
            log("config file not existed"+configfile);
        } catch (IOException e) {
            log("error when read from config file");
        }

        return configs;

    }

    private VCSConfig getSVNConfig(String str) {
        VCSConfig config = new VCSConfig("svn");
        StringTokenizer tokens = new StringTokenizer(str," ");

        tokens.nextElement();
        config.setUrl(tokens.nextElement().toString());
        config.setModule(tokens.nextElement().toString());
        config.setUsername(tokens.nextElement().toString());
        config.setPassword(tokens.nextElement().toString());

        return config;
    }

    private VCSConfig getCVSConfig(String str) {
        VCSConfig config = new VCSConfig("cvs");

        StringTokenizer tokens = new StringTokenizer(str," ");

        tokens.nextElement();
        config.setUrl(tokens.nextElement().toString());
        config.setModule(tokens.nextElement().toString());
        config.setPassword(tokens.nextElement().toString());

        return config;
    }
}
