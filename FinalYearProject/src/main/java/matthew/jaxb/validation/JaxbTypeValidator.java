package matthew.jaxb.validation;

import matthew.jaxb.types.EntitiesType;
import matthew.jaxb.types.EntityType;
import matthew.jaxb.types.PropertyType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class JaxbTypeValidator
{
	public static transient Logger log = Logger.getLogger(JaxbTypeValidator.class);

	/**
	 * Validate an entity structure. This will return an error string if not valid
	 *
	 * @param entitiesType the structure to be validated
	 * @return the error string if not valid, null otherwise
	 */
	public static String validateEntityStructure(final EntitiesType entitiesType)
	{
		log.info("Testing the validity of an entity structure...");

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
				log.info("Property " + property.getName() + " has mapping: " + property.getMapping());

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
						// if a property is complex, mapping is required
						else if (StringUtils.isEmpty(property.getMapping()))
						{
							return "Complex property " + property.getName() + " in entity " + entity.getName() +
							       " does not have a value for 'mapping'. This is a required field for complex " +
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

		log.info("Entity structure is valid!");
		return null;
	}

	/**
	 * Check if a type is a valid complex type by check if a entity exists which has the same name as the type we want
	 * to use
	 *
	 * @param type         the type we want to check for validity
	 * @param entitiesType the entity structure to check
	 * @return true if a match if valid(match found), false otherwise
	 */
	public static boolean checkComplexProperty(final String type, final EntitiesType entitiesType)
	{
		log.info("Checking for the existence of type " + type +
		         " in the given entity structure to validate it as being a complex type...");
		for (EntityType entity : entitiesType.getEntity())
		{
			if (StringUtils.equalsIgnoreCase(entity.getName(), type))
			{
				log.info("Match found!");
				return true;
			}
		}

		log.info("No match found! Type " + type + " is not valid!");
		return false;
	}

}
