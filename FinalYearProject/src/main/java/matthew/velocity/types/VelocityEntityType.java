package matthew.velocity.types;


import org.apache.commons.lang.StringUtils;

import java.util.List;


public class VelocityEntityType
{

	public List<VelocityPropertyType> getProperties()
	{
		return properties;
	}

	public void setProperties(List<VelocityPropertyType> properties)
	{
		this.properties = properties;
	}

	private List<VelocityPropertyType> properties;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getIdType()
	{
		return idType;
	}

	public void setIdType(String idType)
	{
		this.idType = idType;
	}

	private String name;
	// the type of the id
	private String idType;

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


	public String getCapitalisedIdType()
	{
		// TODO this works for longs but needs to be expanded to cover all id types
		return StringUtils.capitalize(idType);
	}

}
