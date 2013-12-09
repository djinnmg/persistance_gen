package matthew.velocity.codegen;

import matthew.jaxb.types.EntitiesType;
import matthew.jaxb.types.EntityType;
import matthew.jaxb.types.PropertyType;
import matthew.velocity.types.VelocityEntitiesType;
import matthew.velocity.types.VelocityEntityType;
import matthew.velocity.types.VelocityPropertyType;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * User: matthew
 */
public class VelocityMarshaller
{
	private final EntitiesType entities;
	private final List<VelocityEntityType> velocityEntityList;


	public VelocityMarshaller(final EntitiesType entities)
	{
		this.entities = entities;
		velocityEntityList = new LinkedList<>();
	}

	public VelocityEntitiesType marshall(final String packagePath)
	{
		VelocityEntitiesType velocityEntities = new VelocityEntitiesType();

		velocityEntities.setPackagePath(packagePath);


		for (EntityType entityType : entities.getEntity())
		{
			marshall(entityType);
		}

		velocityEntities.setEntities(velocityEntityList);

		return velocityEntities;
	}

	private void marshall(final EntityType entity)
	{
		String entityIdType = null;

		// check if the entity has an Id property
		{
			for (PropertyType property : entity.getProperty())
			{
				if (property.isId())
				{
					// only one Id allowed currently
					if (StringUtils.isNotEmpty(entityIdType))
					{
						throw new IllegalArgumentException(
								"Entity " + entity.getName() + " cannot have more than one Id!");
					}
					else
					{
						entityIdType = property.getType();
					}
				}
			}

			if (StringUtils.isEmpty(entityIdType))
			{
				throw new IllegalArgumentException("Entity " + entity.getName() + " must have an Id!");
			}
		}


		final String entityName = entity.getName().replaceAll("[^A-Za-z0-9]", "");

		// check for already existing entities (may have been added to accommodate incoming field)
		// we assume it has a name
		{
			for (VelocityEntityType velocityEntity : velocityEntityList)
			{
				if (StringUtils.equals(velocityEntity.getName(), entityName))
				{
					marshall(velocityEntity, entity, entityIdType);
					return;
				}
			}
		}

		// no entry exists, create and add new entity
		{
			VelocityEntityType velocityEntity = new VelocityEntityType();

			velocityEntity.setName(entityName);
			velocityEntity.setProperties(new LinkedList<VelocityPropertyType>());

			marshall(velocityEntity, entity, entityIdType);
			velocityEntityList.add(velocityEntity);
			return;
		}
	}

	private void marshall(final VelocityEntityType velocityEntity, final EntityType entity, final String entityIdType)
	{
		velocityEntity.setIdType(entityIdType);

		for (PropertyType property : entity.getProperty())
		{
			velocityEntity.getProperties().add(marshall(property, entity));
		}
	}

	private VelocityPropertyType marshall(final PropertyType property, final EntityType parentEntity)
	{
		final VelocityPropertyType velocityProperty = new VelocityPropertyType();

		velocityProperty.setName(property.getName());
		velocityProperty.setId(property.isId());
		velocityProperty.setSerialise(property.isSerialise());
		velocityProperty.setAnnotation(getPropertyAnnotation(property));

		getValidPropertyType(velocityProperty, property, parentEntity);

		// check if the property if a collection
		if (StringUtils.equalsIgnoreCase(velocityProperty.getAnnotation(), "ManyToOne") ||
		    StringUtils.equalsIgnoreCase(velocityProperty.getAnnotation(), "ManyToMany"))
		{
			velocityProperty.setCollection(true);
		}

		return velocityProperty;
	}

	private String getPropertyAnnotation(final PropertyType property)
	{
		String annotation;

		if (property.isId())
		{
			annotation = "Id";
		}
		else if (StringUtils.isEmpty(property.getMapping()))
		{
			annotation = "Column";

			if (property.isNullable())
			{
				annotation += "(nullable = true)";
			}
		}
		else
		{
			annotation = getValidPropertyMapping(property.getMapping());
		}

		return annotation;
	}

	private String getValidPropertyMapping(final String mapping)
	{
		final String oneToMany = "OneToMany";
		final String manyToOne = "ManyToOne";
		final String manyToMany = "ManyToMany";

		if (StringUtils.equalsIgnoreCase(mapping, oneToMany))
		{
			return oneToMany;
		}
		else if (StringUtils.equalsIgnoreCase(mapping, manyToOne))
		{
			return manyToOne;
		}
		else if (StringUtils.equalsIgnoreCase(mapping, manyToMany))
		{
			return manyToMany;
		}
		else
		{
			throw new IllegalArgumentException(mapping + " is not a valid mapping!");
		}
	}

	private void getValidPropertyType(VelocityPropertyType velocityProperty, final PropertyType property,
			final EntityType parentEntity)
	{
		boolean isNullable = property.isNullable();

		if (property.isId())
		{
			isNullable = false;
		}

		switch (property.getType().toLowerCase())
		{
			case "boolean":
				if (isNullable)
				{
					velocityProperty.setType("Boolean");

				}
				else
				{
					velocityProperty.setType("boolean");
				}
				return;
			case "string":
				velocityProperty.setType("String");
				return;
			case "int":
			case "integer":
				if (isNullable)
				{
					velocityProperty.setType("Integer");
				}
				else
				{
					velocityProperty.setType("int");
				}
				return;
			case "long":
				if (isNullable)
				{
					velocityProperty.setType("Long");
				}
				else
				{
					velocityProperty.setType("long");
				}
				return;
			case "datetime":
			case "date":
				velocityProperty.setType("DateTime");
				return;
		}

		if (property.isId())
		{
			throw new IllegalArgumentException("Cannot use complex type as Id! Property: " + property.getName());
		}

		// is a complex type
		velocityProperty.setComplex(true);

		// check if property name matches another entity name
		velocityProperty.setType(checkForType(property, parentEntity));

	}

	private String checkForType(final PropertyType property, final EntityType parentEntity)
	{
		// check for a property type within the entities
		for (EntityType entity : entities.getEntity())
		{
			final String entityName = entity.getName().replaceAll("[^A-Za-z0-9]", "");

			if (StringUtils.equalsIgnoreCase(entityName, property.getType()))
			{
				// check if velocity entity with that name exists
				for (VelocityEntityType velocityEntity : velocityEntityList)
				{
					// if does add a property with name property.incoming of type parentEntity
					if (StringUtils.equalsIgnoreCase(velocityEntity.getName(), entityName))
					{
						velocityEntity.getProperties().add(getPropertyForReversedMappedEntity(property, parentEntity));

						return entityName;
					}
				}

				{
					// if not create a new entity with name propertyTypeName
					VelocityEntityType velocityEntity = new VelocityEntityType();

					velocityEntity.setName(entityName);
					velocityEntity.setProperties(new LinkedList<VelocityPropertyType>());

					// then add property with name property.incoming of type parentEntity
					velocityEntity.getProperties().add(getPropertyForReversedMappedEntity(property, parentEntity));

					return entityName;
				}
			}

		}

		throw new IllegalArgumentException(property.getType() + " is not a valid type!");
	}

	// get a property with name property.incoming of type parentEntity for reverse mapping a relationship
	private VelocityPropertyType getPropertyForReversedMappedEntity(final PropertyType property,
			final EntityType parentEntity)
	{
		VelocityPropertyType velocityProperty = new VelocityPropertyType();

		velocityProperty.setType(parentEntity.getName().replaceAll("[^A-Za-z0-9]", ""));
		velocityProperty.setComplex(true);
		velocityProperty.setName(property.getIncoming());
		velocityProperty.setSerialise(property.isSerialise());
		velocityProperty.setAnnotation(getReversedPropertyMapping(property.getMapping()));

		return velocityProperty;
	}

	// this is used for the reverse mapping of a property onto another type
	private String getReversedPropertyMapping(final String mapping)
	{
		final String oneToMany = "OneToMany";
		final String manyToOne = "ManyToOne";
		final String manyToMany = "ManyToMany";

		if (StringUtils.equalsIgnoreCase(mapping, oneToMany))
		{
			return manyToOne;
		}
		else if (StringUtils.equalsIgnoreCase(mapping, manyToOne))
		{
			return oneToMany;
		}
		else if (StringUtils.equalsIgnoreCase(mapping, manyToMany))
		{
			return manyToMany;
		}
		else
		{
			throw new IllegalArgumentException(mapping + " is not a valid mapping!");
		}
	}


}
