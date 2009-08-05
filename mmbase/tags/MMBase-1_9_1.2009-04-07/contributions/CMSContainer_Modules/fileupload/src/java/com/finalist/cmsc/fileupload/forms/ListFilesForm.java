package com.finalist.cmsc.fileupload.forms;

import com.finalist.cmsc.struts.PagerForm;

/**
 * This form is a pager-form that also allows to filter based on the (currently)
 * two properties of files: the title and the filename. The filtered results
 * are also paged.
 *
 * @author Auke van Leeuwen
 */
@SuppressWarnings("serial")
public class ListFilesForm extends PagerForm {
	private String searchTitle;
	private String searchFilename;
	private int resultsPerPage = 50;

	/**
	 * Returns the searchTitle.
	 *
	 * @return the searchTitle
	 */
	public String getSearchTitle() {
		return searchTitle;
	}

	/**
	 * Sets the searchTitle to the specified value.
	 *
	 * @param searchTitle
	 *            the searchTitle to set
	 */
	public void setSearchTitle(final String searchTitle) {
		this.searchTitle = searchTitle;
	}

	/**
	 * Returns the resultsPerPage.
	 *
	 * @return the resultsPerPage
	 */
	public int getResultsPerPage() {
		return resultsPerPage;
	}

	/**
	 * Sets the resultsPerPage to the specified value.
	 *
	 * @param resultsPerPage
	 *            the resultsPerPage to set
	 */
	public void setResultsPerPage(final int resultsPerPage) {
		this.resultsPerPage = resultsPerPage;
	}

	/**
	 * Returns the searchFilename.
	 *
	 * @return the searchFilename
	 */
	public String getSearchFilename() {
		return searchFilename;
	}

	/**
	 * Sets the searchFilename to the specified value.
	 *
	 * @param searchFilename
	 *            the searchFilename to set
	 */
	public void setSearchFilename(final String searchFilename) {
		this.searchFilename = searchFilename;
	}
}
