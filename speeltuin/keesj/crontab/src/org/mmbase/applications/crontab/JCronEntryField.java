package org.mmbase.applications.crontab;


import java.util.*;

public class JCronEntryField {
    private String content;
    
    boolean[] valid = new boolean[60];
    
    public JCronEntryField() {
    }
    
    public JCronEntryField(String content) {
        setTimeVal(content);
    }
    
    /**
     *
     **/
    public void setTimeVal(String content){
        this.content = content;
        
        for (int x  =0 ; x < valid.length ; x++){
            valid[x] = false;
        }
        
        StringTokenizer st = new StringTokenizer(content,",");
        //entries of one field are seoarateed
        while(st.hasMoreTokens()){
            String subentry = st.nextToken();
            parseEntry(subentry);
        }
    }
    
    public void parseEntry(String subentry){
        StringTokenizer st = new StringTokenizer(subentry,"/");
        String timelist = st.nextToken();
        
        
        int step =1;
        if (st.hasMoreTokens()){
            step = Integer.parseInt(st.nextToken());
        }
        
        //at this point timelist can be in a few formats
        //"int" like 12
        //"range" like 8-12
        //"*" like "any", for simplicity we assume any are all the numbers
        //under 60
        if (timelist.indexOf("-") != -1){ // range
            StringTokenizer rangeSplit = new StringTokenizer(timelist,"-");
            String start = rangeSplit.nextToken();
            String end = rangeSplit.nextToken();
            for (int x = Integer.parseInt(start); x <= Integer.parseInt(end); x += step){
                valid[x] = true;
            }
        } else if (timelist.indexOf("*") != -1){//while card
            for (int x =0 ; x < valid.length; x += step){
                valid[x]= true;
            }
        } else {
            int number = Integer.parseInt(timelist);
            valid[number] = true;
        }
    }
    
    public boolean valid(int otherValue){
        return valid[otherValue];
    }
}
