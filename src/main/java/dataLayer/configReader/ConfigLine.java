package dataLayer.configReader;

import java.lang.reflect.Field;

public class ConfigLine
{
	final Class dataClass;
	final Field classField;


	public ConfigLine(String dataClass, String classField) throws ClassNotFoundException, NoSuchFieldException
	{
		this.dataClass = Class.forName(dataClass);
		this.classField = this.dataClass.getField(classField);
	}
}
