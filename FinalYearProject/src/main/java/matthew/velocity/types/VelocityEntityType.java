package matthew.velocity.types;


import org.apache.commons.lang.StringUtils;

import java.util.List;


public class VelocityEntityType
{

	public List<VelocityPropertyType> properties;
	public String name;
	// the type of the id
	public String idType;

	public String getCamelCaseName()
	{
		if (StringUtils.isEmpty(name))
		{
			throw new IllegalArgumentException("Cannot get camel case for empty name!");
		}

		if (StringUtils.isNumeric("" + name.charAt(0)))
		{
			// TODO this should be checked in the mappings
			throw new IllegalArgumentException("Entity name cannot start with a number");
		}

		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}


}
