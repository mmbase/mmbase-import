package nl.vpro.util;


public class IllegalParameterException extends RuntimeException {
	
	private Class parameterType;
	private String parameterName;

	public IllegalParameterException(String message, Class parameterType, String parameterName){
		super(message);
		this.parameterType= parameterType;
		this.parameterName = parameterName;
	}
	
	public static void check(Object value, Class expected, String parameterName){
		if(value == null){
			throw new IllegalParameterException("Parameter "+parameterName+" is null", value.getClass(), parameterName);
		}else{
			if(! expected.isAssignableFrom(expected)){
				throw new IllegalParameterException("Parameter "+parameterName+" is of wrong type."+
						" Type "+expected+" was expected, but "+value.getClass()+" was given", value.getClass(), parameterName);
			}
		}
	}
	
}
