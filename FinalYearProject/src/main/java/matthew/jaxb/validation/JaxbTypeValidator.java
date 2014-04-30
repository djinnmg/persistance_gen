package matthew.jaxb.validation;

import matthew.jaxb.types.EntitiesType;
import matthew.jaxb.types.EntityType;
import matthew.jaxb.types.PropertyType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class JaxbTypeValidator
{
	public static transient Logger log = Logger.getLogger(JaxbTypeValidator.class);

	// TODO add logging
	public static String validateEntityStructure(final EntitiesType entitiesType)
{
	if (entitiesType.getEntity().isEmpty())
	{
		return "Entities list is empty";
	}

	for (EntityType entity : entitiesType.getEntity())
	{
		// check has name
		if (StringUtils.isEmpty(entity.getName()))
		{
			return "Entities require a name!";
		}

		if (entity.getProperty().isEmpty())
		{
			return "Entity " + entity.getName() + " has no properties!";
		}


		int numIds = 0;

		for (PropertyType property : entity.getProperty())
		{
			if (StringUtils.isEmpty(property.getName()))
			{
				return "Entity " + entity.getName() +
				         " has a property with no name! All properties require names!";
			}

			if (StringUtils.isEmpty(property.getType()))
			{
				return "Entity " + entity.getName() +
				         " has a property with no type! All properties require types!";
			}

			if (property.isId())
			{
				if (!StringUtils.equalsIgnoreCase(property.getType(), "int") &&
				    !StringUtils.equalsIgnoreCase(property.getType(), "long"))
				{
					return "Entity " + entity.getName() + " has id of type " + property.getType() +
					         ". Only types int and long are allowed for ids!";
				}

				numIds++;
			}

			switch (property.getType().toLowerCase())
			{
				case "string":
				case "long":
				case "int":
				case "integer":
				case "boolean":
				case "date":
				case "datetime":
					break;
				default:
					if (!checkComplexProperty(property.getType(), entitiesType))
					{
						return "Property type " + property.getType() +
						         " is not supported! Property is not one of supported types: string, int, " +
						         "integer," +
						         " long, boolean, date, datetime and a matching entity name cannot be found for a" +
						         " " +
						         "complex type!";
					}
					// if a property is complex, incoming is required
					else if (StringUtils.isEmpty(property.getIncoming()))
					{
						return "Complex property " + property.getName() + " in entity " + entity.getName() +
						         " does not have a value for 'incoming'. This is a required field for complex " +
						         "properties.";
					}
			}

		}

		if (numIds < 1)
		{
			return "Entity " + entity.getName() +
			         " does not have an id property! All entities require an id property!";
		}

		if (numIds > 1)
		{
			return "Only one id allowed per entity! Entity " + entity.getName() + " has " + numIds + "!";
		}


	}

	return null;
}

	public static boolean checkComplexProperty(final String type, final EntitiesType entitiesType)
	{
		for (EntityType entity : entitiesType.getEntity())
		{
			if (StringUtils.equalsIgnoreCase(entity.getName(), type))
			{
				return true;
			}
		}

		return false;
	}

}
