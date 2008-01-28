package com.finalist.cmsc.alias.beans.om;

import com.finalist.cmsc.beans.om.NavigationItem;

@SuppressWarnings("serial")
public class Alias extends NavigationItem {

    private int page;

    public void setPage(int number) {
        this.page = number;
    }
    
    public int getPage() {
        return page;
    }

}
