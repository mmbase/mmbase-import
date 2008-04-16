package com.finalist.cmsc.resources.forms;

import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;

public class NewletterPublicationAction extends SearchAction {
	public static final String TITLE_FIELD = "title";
	public static final String DESCRIPTION_FIELD = "description";
	public static final String INTRO_FIELD = "intro";
	public static final String SUBJECT_FIELD="subject";
	
	@Override
	protected void addConstraints(SearchForm searchForm,
			NodeManager nodeManager, QueryStringComposer queryStringComposer,
			NodeQuery query) {
		// TODO Auto-generated method stub
		NewletterPublicationForm form=(NewletterPublicationForm)searchForm;
		addField(nodeManager, queryStringComposer, query, TITLE_FIELD, form.getTitle());
		addField(nodeManager, queryStringComposer, query, DESCRIPTION_FIELD, form.getDescription());
		addField(nodeManager, queryStringComposer, query, INTRO_FIELD, form.getIntro());
		addField(nodeManager, queryStringComposer, query, SUBJECT_FIELD, form.getSubject());		
	}
}
