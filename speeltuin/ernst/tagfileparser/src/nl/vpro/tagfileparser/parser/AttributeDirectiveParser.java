package nl.vpro.tagfileparser.parser;

import java.util.HashMap;
import java.util.Map;

import nl.vpro.tagfileparser.model.AttributeDirective;
import nl.vpro.util.StringUtil;

/**
 * @author Ernst Bunders
 *
 */
public class AttributeDirectiveParser extends DirectiveParser {
	
	private Map<String, PropertySetter> setters = new HashMap<String, PropertySetter>();
	private AttributeDirective attributeDirective = null;

	public AttributeDirectiveParser() {
		super("attribute");
		
		setters.put("required", new PropertySetter(){
			public void setProperty(String value) {
				if(StringUtil.isValidBoolean(value, false)){
					attributeDirective.setIsRequired(new Boolean(value));
				}else{
					throw new IllegalDirectiveException("the 'attribute' directive expects a valid boolean as value for attribute 'required', and not ["+value+"]");
				}
			}});
		
		setters.put("fragment", new PropertySetter(){
			public void setProperty(String value) {
				if(StringUtil.isValidBoolean(value, false)){
					attributeDirective.setIsFragment(new Boolean(value));
				}else{
					throw new IllegalDirectiveException("the 'attribute' directive expects a valid boolean as value for attribute 'fragment', and not ["+value+"]");
				}
			}});
		
		setters.put("rtexprvalue", new PropertySetter(){
			public void setProperty(String value) {
				if(StringUtil.isValidBoolean(value, false)){
					attributeDirective.setIsRtexprvalue(new Boolean(value));
				}else{
					throw new IllegalDirectiveException("the 'attribute' directive expects a valid boolean as value for attribute 'rtexprvalue', and not ["+value+"]");
				}
			}});
		
		setters.put("name", new PropertySetter(){
			public void setProperty(String value) {
				attributeDirective.setName(value);
			}
		});
		
		setters.put("description", new PropertySetter(){
			public void setProperty(String value) {
				attributeDirective.setDescription(value);
			}
		});
		
		setters.put("type", new PropertySetter(){
			public void setProperty(String value) {
				try{
					attributeDirective.setType(Class.forName(value));
				}catch (Throwable t){
					throw new IllegalDirectiveException("the 'attribute' directive expects the name of a loadable class as value for attribute 'type', and not ["+value+"]", t);
				}
			}});
	}

	@Override
	protected void attributeFound(Attribute attribute) {
		if(attributeDirective == null){
			throw new IllegalDirectiveException("attribute found before TagDirective instance was created");
		}
		if(setters.get(attribute.getName()) != null){
			setters.get(attribute.getName()).setProperty(attribute.getValue());
		}else{
			throw new IllegalDirectiveException("attribute ["+attribute.getName()+"] is not valid for a attribute directive");
		}

	}

	@Override
	protected void directiveFound() {
		attributeDirective = new AttributeDirective();
		tag.addAttribute(attributeDirective);
	}

}
