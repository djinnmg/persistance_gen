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

	/**
	 * Gets the entities name in a camel case form. This is useful in the thymeleaf template processing
	 *
	 * @return The formatted entity name
	 */
	public String getCamelCaseName()
	{
		if (StringUtils.isEmpty(name))
		{
			throw new IllegalArgumentException("Cannot get camel case for empty name!");
		}

		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}


	/**
	 * Gets the Object form of the ID
	 *
	 * @return The Object form of the IDs simple type
	 */
	public String getCapitalisedIdType()
	{
		switch (idType)
		{
			case "long":
				return "Long";
			case "int":
				return "Integer";
			default:
				throw new IllegalArgumentException("Cannot get capitalised version of id with type " + idType +
				                                   "! Only int and long are supported.");
		}
	}

}
