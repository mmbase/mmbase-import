package nl.vpro.tagfileparser.parser;

import java.util.HashMap;
import java.util.Map;

import nl.vpro.tagfileparser.model.VariableDirective;
import nl.vpro.util.StringUtil;

/**
 * @author Ernst Bunders
 *
 */
public class VariableDirectiveParser extends DirectiveParser {
	
	private VariableDirective variableDirective = null;
	private Map<String,PropertySetter> setters = new HashMap<String,PropertySetter>();

	public VariableDirectiveParser() {
		super("variable");
		setters.put("name-given", new PropertySetter(){
			public void setProperty(String value) {
				variableDirective.setNameGiven(value);
			}
		});
		
		setters.put("name-from-attribute", new PropertySetter(){
			public void setProperty(String value) {
				variableDirective.setNameFromAttribute(value);
			}
		});
		
		setters.put("alias", new PropertySetter(){
			public void setProperty(String value) {
				variableDirective.setAlias(value);
			}
		});
		
		setters.put("declare", new PropertySetter(){
			public void setProperty(String value) {
				if(StringUtil.isValidBoolean(value, false)){
					variableDirective.setDeclare(new Boolean(value));
				}else{
					throw new IllegalDirectiveException("the 'attribute' directive expects a valid boolean as value for attribute 'declare', and not ["+value+"]");
				}
			}});
		
		setters.put("variable-class", new PropertySetter(){
			public void setProperty(String value) {
				try{
					variableDirective.setVariableClass(Class.forName(value));
				}catch (Throwable t){
					throw new IllegalDirectiveException("the 'attribute' directive expects the name of a loadable class as value for attribute 'variable-class', and not ["+value+"]", t);
				}
			}});
		
		setters.put("scope", new PropertySetter(){
			public void setProperty(String value) {
				variableDirective.setScope(value);
			}
		});
		
		setters.put("description", new PropertySetter(){
			public void setProperty(String value) {
				variableDirective.setDescription(value);
			}
		});
	}

	@Override
	protected void attributeFound(Attribute attribute) {
		if(variableDirective == null){
			throw new IllegalDirectiveException("attribute found before VariableDirective instance was created");
		}
		if(setters.get(attribute.getName()) != null){
			setters.get(attribute.getName()).setProperty(attribute.getValue());
		}else{
			throw new IllegalDirectiveException("attribute ["+attribute.getName()+"] is not valid for a variable directive");
		}
	}

	@Override
	protected void directiveFound() {
		variableDirective = new VariableDirective();
		tag.addVariableDirective(variableDirective);
	}

}
