package nl.vpro.tagfileparser.model;

/**
 * @author Ernst Bunders
 *
 */
public class TagDirective {
	
	private String displayName = null;
	private String bodyContent = null;
	private String dynamicAttributes = null;
	private String smallIcon = null;
	private String largeIcon = null;
	private String description = null;
	private String example = null;
	private String language = null;
	private String imports = null;
	private String pageEncoding = null;
	private Boolean isELIgnored= null;
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getBodyContent() {
		return bodyContent;
	}
	public void setBodyContent(String bodyContent) {
		this.bodyContent = bodyContent;
	}
	public String getDynamicAttributes() {
		return dynamicAttributes;
	}
	public void setDynamicAttributes(String dynamicAttributes) {
		this.dynamicAttributes = dynamicAttributes;
	}
	public String getSmallIcon() {
		return smallIcon;
	}
	public void setSmallIcon(String smallIcon) {
		this.smallIcon = smallIcon;
	}
	public String getLargeIcon() {
		return largeIcon;
	}
	public void setLargeIcon(String largeIcon) {
		this.largeIcon = largeIcon;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getExample() {
		return example;
	}
	public void setExample(String example) {
		this.example = example;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getImports() {
		return imports;
	}
	public void setImports(String imports) {
		this.imports = imports;
	}
	public String getPageEncoding() {
		return pageEncoding;
	}
	public void setPageEncoding(String pageEncoding) {
		this.pageEncoding = pageEncoding;
	}
	public Boolean getIsELIgnored() {
		return isELIgnored;
	}
	public void setIsELIgnored(Boolean isELIgnored) {
		this.isELIgnored = isELIgnored;
	}
	
	
}
