package com.finalist.cmsc.services.community.domain;

import java.util.List;
import java.util.Map;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.preferences.Preference;
import com.finalist.cmsc.services.community.security.Authentication;

public class CommunityExport {
	private List<PersonExportImportVO> users;

	public List<PersonExportImportVO> getUsers() {
		return users;
	}

	public void setUsers(List<PersonExportImportVO> users) {
		this.users = users;
	}

	public CommunityExport(List<PersonExportImportVO> users) {
	
		this.users = users;
	}
	

}
