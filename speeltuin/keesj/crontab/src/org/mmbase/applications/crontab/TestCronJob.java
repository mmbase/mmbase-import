package org.mmbase.applications.crontab;

import java.net.*;
import java.util.*;
import java.io.*;
import nanoxml.*;

public class TestCronJob implements JCronJob {


    public void init(JCronEntry jCronEntry) {
    }

    public void stop() {
    }
    
    public void run() {
        System.err.println("start run job");
        try {
            //URL url = new URL("http://www.bbc.co.uk/syndication/feeds/news/ukfs_news/world/rss091.xml");
            //URL url = new URL("http://calit.minenu/~keesj/rss091.xml");
            URL url = new URL("http://carlit.mine.nu/~keesj/slashdot.rss.xml");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line= null;
            StringBuffer sb = new StringBuffer();
            while( (line = br.readLine() ) != null){
                sb.append(line);
            }
            XMLElement xmle = new XMLElement();
            xmle.parseString(sb.toString());
            //just parse items
            Iterator childs = xmle.getChildren().iterator();
            while(childs.hasNext()){
                XMLElement childElement = (XMLElement)childs.next();
                if (childElement.getTagName().equals("item")){
                    System.err.println(childElement);
                }
            }
            
        } catch(Exception e){
            System.err.println("EXCEPTION" + e.getMessage());
        }
        System.err.println("stop run job");
    }
    
}
