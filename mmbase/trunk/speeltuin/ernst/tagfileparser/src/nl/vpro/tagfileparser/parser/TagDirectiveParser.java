package nl.vpro.tagfileparser.parser;

import java.util.HashMap;
import java.util.Map;

import nl.vpro.tagfileparser.model.TagDirective;
import nl.vpro.util.StringUtil;

/**
 * @author Ernst Bunders
 *
 */
public class TagDirectiveParser extends DirectiveParser {

	private TagDirective tagDirective = null;
	
	private Map<String,PropertySetter> setters = new HashMap<String,PropertySetter>();
	
	public TagDirectiveParser() {
		super("tag");
		setters.put("display-name", new PropertySetter(){
			public void setProperty(String value) {
				tagDirective.setDisplayName(value);
			}
		});
		
		setters.put("body-content", new PropertySetter(){
			public void setProperty(String value) {
				tagDirective.setBodyContent(value);
			}
		});
		
		setters.put("dynamic-attributes", new PropertySetter(){
			public void setProperty(String value) {
				tagDirective.setDynamicAttributes(value);
			}
		});
		
		setters.put("small-icon", new PropertySetter(){
			public void setProperty(String value) {
				tagDirective.setSmallIcon(value);
			}
		});
		
		setters.put("large-icon", new PropertySetter(){
			public void setProperty(String value) {
				tagDirective.setLargeIcon(value);
			}
		});
		
		setters.put("description", new PropertySetter(){
			public void setProperty(String value) {
				tagDirective.setDescription(value);
			}
		});
		
		setters.put("example", new PropertySetter(){
			public void setProperty(String value) {
				tagDirective.setExample(value);
			}
		});
		
		setters.put("language", new PropertySetter(){
			public void setProperty(String value) {
				tagDirective.setLanguage(value);
			}
		});
		
		setters.put("import", new PropertySetter(){
			public void setProperty(String value) {
				tagDirective.setImports(value);
			}
		});
		
		setters.put("pageEncoding", new PropertySetter(){
			public void setProperty(String value) {
				tagDirective.setPageEncoding(value);
			}
		});
		
		setters.put("isELIgnored", new PropertySetter(){
			public void setProperty(String value) {
				if(StringUtil.isValidBoolean(value, false)){
					tagDirective.setIsELIgnored(new Boolean(value));
				}else{
					throw new IllegalDirectiveException("the 'tag' directive expects a valid boolean as value for attribute 'isELIgnored', and not ["+value+"]");
				}
			}
		});
	}

	@Override
	protected void attributeFound(Attribute attribute) {
		if(tagDirective == null){
			throw new IllegalDirectiveException("attribute found before TagDirective instance was created");
		}
		if(setters.get(attribute.getName()) != null){
			setters.get(attribute.getName()).setProperty(attribute.getValue());
		}else{
			throw new IllegalDirectiveException("attribute ["+attribute.getName()+"] is not valid for a tag directive");
		}
	}

	/**
	 * @see nl.vpro.tagfileparser.parser.DirectiveParser#directiveFound()
	 * @throws IllegalDirectiveException when the tagInfo instance of this parser already has an TagDirective instance
	 */
	@Override
	protected void directiveFound() {
		if(tag.getTagDirective() != null){
			throw new IllegalDirectiveException("tag directive found, but one is already in the taginfo object");
		}
		tagDirective = new TagDirective();
		tag.setTagDirective(tagDirective);

	}
}
