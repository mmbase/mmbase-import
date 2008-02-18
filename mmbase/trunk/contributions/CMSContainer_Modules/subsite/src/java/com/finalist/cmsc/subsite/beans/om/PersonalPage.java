package com.finalist.cmsc.subsite.beans.om;

import com.finalist.cmsc.beans.om.Page;

@SuppressWarnings("serial")
public class PersonalPage extends Page {
   //For future use: new data fields e.g. IDs
	
	private String userID;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
}
