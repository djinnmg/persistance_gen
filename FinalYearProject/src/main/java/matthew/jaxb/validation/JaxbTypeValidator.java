package matthew.jaxb.validation;

import matthew.jaxb.types.EntitiesType;
import matthew.jaxb.types.EntityType;
import matthew.jaxb.types.PropertyType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

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

			if (!validEntityName(entitiesType.getEntity(), entity))
			{
				return "Entity name" + entity.getName() + " is not valid! Another entity has the same name.";
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
						// if a property is complex, mapping is required
						else if (StringUtils.isEmpty(property.getMapping()))
						{
							return "Complex property " + property.getName() + " in entity " + entity.getName() +
							       " does not have a value for 'mapping'. This is a required field for complex " +
							       "properties.";
						}
				}

				if (!validPropertyName(entitiesType.getEntity(), entity, property))
				{
					return "Property name " + property.getName() + " in entity " + entity.getName() +
					       " is not valid! Another property has the same name or another entity has as incoming " +
					       "mapping which will create a property with the same name in this entity. Check the logs for" +
					       " more details.";
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


	/**
	 * This validates an entity does not have the same name as any others in the list
	 *
	 * @param entities         The list of existing entities
	 * @param entityToValidate The entity to validate
	 * @return true if valid, false otherwise
	 */
	private static boolean validEntityName(final List<EntityType> entities, final EntityType entityToValidate)
	{
		log.debug("Validating entity the name of entity " + entityToValidate.getName());
		for (EntityType entity : entities)
		{
			if (!entity.equals(entityToValidate))
			{
				if (StringUtils.equalsIgnoreCase(entity.getName(), entityToValidate.getName()))
				{
					log.warn("More than one entity has the name " + entity.getName() + "!");
					return false;
				}
			}
		}

		return true;
	}


	/**
	 * This method will ensure a property has a valid name. The name would be invalid if another property in the
	 * entity has the same name or an entity which use the parent entity of this property has an incoming value which
	 * is the same as the property name. This would cause an error when the reverse mapping is carried out and a
	 * property is created using the value of incoming in the same entity as this property.
	 *
	 * @param entities           The list of all the entities
	 * @param parentEntity       The entity which contains the property we wish to validate
	 * @param propertyToValidate The property we wish to validate
	 * @return true if valid, false otherwise
	 */
	private static boolean validPropertyName(final List<EntityType> entities, final EntityType parentEntity,
			final PropertyType propertyToValidate)
	{
		log.debug("Validating the name of property " + propertyToValidate.getName());
		// check other properties within entity for name match
		for (PropertyType property : parentEntity.getProperty())
		{
			if (!property.equals(propertyToValidate))
			{
				if (StringUtils.equalsIgnoreCase(property.getName(), propertyToValidate.getName()))
				{
					log.warn("More than one property in entity " + parentEntity.getName() + " has the name " +
					         propertyToValidate.getName() + "!");
					return false;
				}
			}
		}

		// check all other entities apart from entity containing the property to validate
		for (EntityType entity : entities)
		{
			if (!entity.equals(parentEntity))
			{
				// for each property in the entity check if their type is the parent entity
				for (PropertyType property : entity.getProperty())
				{
					if (StringUtils.equalsIgnoreCase(property.getType(), parentEntity.getName()))
					{
						// if they have an incoming value, check it does not match the name of the property we a
						// validating
						if (StringUtils.isNotEmpty(property.getIncoming()) &&
						    StringUtils.equalsIgnoreCase(property.getIncoming(), propertyToValidate.getName()))
						{
							log.warn("The property " + property.getName() + " in the entity " + entity.getName() +
							         " has an incoming value of " + property.getIncoming() +
							         " which will create a property with the same name as " +
							         propertyToValidate.getName() + "!");
							return false;
						}
					}
				}
			}
		}

		return true;
	}


}
