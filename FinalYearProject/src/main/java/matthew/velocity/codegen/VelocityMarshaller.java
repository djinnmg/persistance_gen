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
			VelocityEntityType velocityEntity =	marshallEntity(entityType);

            if (!velocityEntityList.contains(velocityEntity))
            {
                velocityEntityList.add(velocityEntity);
            }
		}

		velocityEntities.setEntities(velocityEntityList);

		return velocityEntities;
	}

    /**
     * Marshall an entityType to a velocity entity and add it to the velocityEntityList.
     *
     * @param entity The EntityType to be marshalled.
     */
	private VelocityEntityType marshallEntity(final EntityType entity)
	{
        if (StringUtils.isEmpty(entity.getName()))
        {
            throw new IllegalArgumentException("All entities require names!");
        }
        final String entityName = getFormattedEntityName(entity.getName());
        final String entityIdType = getEntityIdType(entity);

        // check for already existing entities (may have been added to accommodate incoming field)
        VelocityEntityType velocityEntity = getVelocityEntityByName(entityName);

        // no entry exists, create and add new entity
        if (velocityEntity == null)
        {
            velocityEntity = new VelocityEntityType();
            velocityEntity.setName(entityName);
            velocityEntity.setProperties(new LinkedList<VelocityPropertyType>());
        }

        velocityEntity.setIdType(entityIdType);

        velocityEntity.getProperties().addAll(marshallProperties(entity));

		return velocityEntity;
	}


    private VelocityEntityType getVelocityEntityByName(final String entityName)
    {
        // check for already existing entities (may have been added to accommodate incoming field)
        for (VelocityEntityType velocityEntity : velocityEntityList)
        {
            if (StringUtils.equals(velocityEntity.getName(), entityName))
            {
                return velocityEntity;
            }
        }

        return null;
    }

    /**
     * Checks if entity has one id property and returns the type of that id.
     * Only one id is allowed and an id is required for an entity. Only types int and long are supported.
     * Will throw an IllegalArgumentException in any of these cases.
     *
     * @param entity Is an entity to be tested for having an id.
     * @return A string containing the entities Id type.
     */
    private String getEntityIdType(final EntityType entity)
    {
        String entityIdType = null;

        // check if the entity has an Id property
        for (PropertyType property : entity.getProperty())
        {
            if (property.isId())
            {
                // only one Id allowed
                if (StringUtils.isNotEmpty(entityIdType))
                {
                    throw new IllegalArgumentException(
                            "Entity " + entity.getName() + " cannot have more than one Id!");
                }
                // only long and int supported for id type
                else if (!StringUtils.equalsIgnoreCase(property.getType(), "long") &&
                        !StringUtils.equalsIgnoreCase(property.getType(), "int"))
                {
                    throw new IllegalArgumentException(
                            "Invalid entity id type: " + property.getType() + ", " +
                                    "only int and long are supported!");
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

        return entityIdType;
    }


	private List<VelocityPropertyType> marshallProperties(final EntityType entity)
	{
        List<VelocityPropertyType> velocityPropertyList = new LinkedList<>();

		for (PropertyType property : entity.getProperty())
		{
			if (StringUtils.isEmpty(property.getName()))
			{
				throw new IllegalArgumentException("All Properties require names!");
			}

            velocityPropertyList.add(marshallProperty(property, entity.getName()));
		}

        return velocityPropertyList;
	}

	private VelocityPropertyType marshallProperty(final PropertyType property, final String parentEntityName)
	{
		final VelocityPropertyType velocityProperty = new VelocityPropertyType();

		velocityProperty.setName(getFormattedPropertyName(property.getName()));
		velocityProperty.setId(property.isId());

		// id must be serialised to allow for updating/deleting in UI
		if (property.isId())
		{
			velocityProperty.setSerialise(true);
		}
		else
		{
			velocityProperty.setSerialise(property.isSerialise());
		}

		velocityProperty.setAnnotation(getPropertyAnnotation(property));
		velocityProperty.setTextArea(property.isTextArea());

		String propertyType = getValidPropertyType(property);

        if (StringUtils.isNotEmpty(propertyType))
        {
            velocityProperty.setType(propertyType);
        }
        else
        // property is complex so process it
        {
            if (property.isId())
            {
                throw new IllegalArgumentException("Cannot use complex type as Id! Property: " + property.getName());
            }

            // is a complex type
            velocityProperty.setComplex(true);

            // check if property name matches another entity name, throws exception if no match found
            final String complexPropertyType = getComplexPropertyType(property);

            // set the velocity property
            velocityProperty.setType(complexPropertyType);

            // add the reversed property to the entity which is the of the type of this property
            addPropertyToReversedMappedEntity(property, parentEntityName, complexPropertyType);
        }

		// check if the property if a collection
		if (StringUtils.equalsIgnoreCase(velocityProperty.getAnnotation(), "OneToMany") ||
		    StringUtils.equalsIgnoreCase(velocityProperty.getAnnotation(), "ManyToMany"))
		{
			velocityProperty.setCollection(true);
		}

		return velocityProperty;
	}


	private void addPropertyToReversedMappedEntity(final PropertyType property, final String parentEntityName, final String entityName)
	{
        // check if velocity entity with that name exists
        for (VelocityEntityType velocityEntity : velocityEntityList)
        {
            // if does add a property with name property.incoming of type parentEntity
            if (StringUtils.equalsIgnoreCase(velocityEntity.getName(), entityName))
            {
                velocityEntity.getProperties().add(getPropertyForReversedMappedEntity(property, parentEntityName));

                return;
            }
        }


        // if not create a new entity with name propertyTypeName
        VelocityEntityType velocityEntity = new VelocityEntityType();

        velocityEntity.setName(entityName);
        velocityEntity.setProperties(new LinkedList<VelocityPropertyType>());

        // then add property with name property.incoming of type parentEntity
        velocityEntity.getProperties().add(getPropertyForReversedMappedEntity(property, parentEntityName));

        velocityEntityList.add(velocityEntity);
	}


	// get a property with name property.incoming of type parentEntity for reverse mapping a relationship
	private VelocityPropertyType getPropertyForReversedMappedEntity(final PropertyType property,
			final String parentEntityName)
	{
		VelocityPropertyType velocityProperty = new VelocityPropertyType();

		velocityProperty.setType(getFormattedEntityName(parentEntityName));
		velocityProperty.setComplex(true);

		if (StringUtils.isEmpty(property.getIncoming()))
		{
			throw new IllegalArgumentException(
					"The complex property " + property.getName() + " in entity " + parentEntityName +
					" has no incoming value set! This is required for complex properties!"
			);
		}
		velocityProperty.setName(getFormattedPropertyName(property.getIncoming()));
		// id must be serialised to allow for updating/deleting in UI
		if (property.isId())
		{
			velocityProperty.setSerialise(true);
		}
		else
		{
			velocityProperty.setSerialise(property.isSerialise());
		}
		velocityProperty.setAnnotation(getReversedPropertyMapping(property.getMapping()));

		// set isComplex based on mapping
		if (StringUtils.equalsIgnoreCase(property.getMapping(), "ManyToOne") ||
		    StringUtils.equalsIgnoreCase(property.getMapping(), "ManyToMany"))
		{
			velocityProperty.setCollection(true);
		}

		velocityProperty.setTextArea(property.isTextArea());

		return velocityProperty;
	}

    private String getValidPropertyType(final PropertyType property)
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
                    return "Boolean";
                }
                else
                {
                    return "boolean";
                }
            case "string":
                return "String";
            case "int":
            case "integer":
                if (isNullable)
                {
                    return "Integer";
                }
                else
                {
                    return "int";
                }
            case "long":
                if (isNullable)
                {
                    return "Long";
                }
                else
                {
                    return "long";
                }
            case "datetime":
            case "date":
                return "DateTime";
        }

       return null;
    }

    private String getComplexPropertyType(final PropertyType property)
    {
        // check for a property type within the entities
        for (EntityType entity : entities.getEntity())
        {
            final String entityName = getFormattedEntityName(entity.getName());

            // entity exists which is of the type we want to add
            if (StringUtils.equalsIgnoreCase(entityName, property.getType())) {
                return entityName;
            }}
        throw new IllegalArgumentException(property.getType() + " is not a valid type!");
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


	private String getFormattedEntityName(String entityName)
	{
		if (StringUtils.isNumeric("" + entityName.charAt(0)))
		{
			throw new IllegalArgumentException("Entity name cannot start with a number");
		}

		return StringUtils.capitalize(entityName.replaceAll("[^A-Za-z0-9]", ""));
	}

	private String getFormattedPropertyName(String propertyName)
	{
		if (StringUtils.isNumeric("" + propertyName.charAt(0)))
		{
			throw new IllegalArgumentException("Property name cannot start with a number");
		}

		return propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
	}

}
