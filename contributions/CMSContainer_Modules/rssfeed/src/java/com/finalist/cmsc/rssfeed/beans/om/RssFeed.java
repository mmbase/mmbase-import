package com.finalist.cmsc.rssfeed.beans.om;

import java.util.*;

import com.finalist.cmsc.beans.om.NavigationItem;

@SuppressWarnings("serial")
public class RssFeed extends NavigationItem {

    private int maximum;
    private int max_age_in_days;
    private String link;
    private String language;
    private String copyright;
    private String email_managing_editor;
    private String email_webmaster;

    private List<String> contenttypes = new ArrayList<String>();
    private int contentChannel = -1;
    
    public int getMaximum() {
        return maximum;
    }
    
    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }
    
    public int getMax_age_in_days() {
        return max_age_in_days;
    }
    
    public void setMax_age_in_days(int max_age_in_days) {
        this.max_age_in_days = max_age_in_days;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getCopyright() {
        return copyright;
    }
    
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
    
    public String getEmail_managing_editor() {
        return email_managing_editor;
    }
    
    public void setEmail_managing_editor(String email_managing_editor) {
        this.email_managing_editor = email_managing_editor;
    }
    
    public String getEmail_webmaster() {
        return email_webmaster;
    }
    
    public void setEmail_webmaster(String email_webmaster) {
        this.email_webmaster = email_webmaster;
    }
    
    public List<String> getContenttypes() {
        return Collections.unmodifiableList(contenttypes);
     }

     public void addContenttype(String contenttypes) {
        this.contenttypes.add(contenttypes);
     }

    public int getContentChannel() {
        return contentChannel;
    }
    
    public void setContentChannel(int contentChannel) {
        this.contentChannel = contentChannel;
    }

}
