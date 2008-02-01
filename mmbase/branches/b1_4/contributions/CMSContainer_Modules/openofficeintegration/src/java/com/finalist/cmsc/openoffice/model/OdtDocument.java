package com.finalist.cmsc.openoffice.model;

/**
 * POJO class ,used for openoffice's odt ducment;
 * @author 
 *
 */
public class OdtDocument {

	private String title;
	
	private String mimeType;
	
	private String body;
	
	private String info;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
}
