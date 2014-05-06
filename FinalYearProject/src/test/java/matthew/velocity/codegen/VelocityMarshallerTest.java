package matthew.velocity.codegen;

import matthew.jaxb.types.EntitiesType;
import matthew.jaxb.types.EntityType;
import matthew.jaxb.types.PropertyType;
import matthew.velocity.types.VelocityPropertyType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VelocityMarshallerTest
{
	private VelocityMarshaller marshaller;

	@Before
	public void setup()
	{
		marshaller = new VelocityMarshaller(new EntitiesType());


	}

	@Test
	public void testGetFormattedPropertyName()
	{
		Assert.assertEquals("testName", marshaller.getFormattedPropertyName("TestName"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFailGetFormattedPropertyName()
	{
		marshaller.getFormattedPropertyName("2TestName");
	}

	@Test
	public void testGetFormattedEntityName()
	{
		Assert.assertEquals("TestName", marshaller.getFormattedEntityName("testName"));
		Assert.assertEquals("TestName", marshaller.getFormattedEntityName("testName$"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFailGetFormattedEntityName()
	{
		marshaller.getFormattedEntityName("2testName");
	}

	@Test
	public void testGetReversedPropertyMapping()
	{
		Assert.assertEquals("OneToMany", marshaller.getReversedPropertyMapping("ManyToOne"));
		Assert.assertEquals("ManyToOne", marshaller.getReversedPropertyMapping("OneToMany"));
		Assert.assertEquals("ManyToMany", marshaller.getReversedPropertyMapping("ManyToMany"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFailGetReversedPropertyMapping()
	{
		marshaller.getReversedPropertyMapping("invalidMapping");
	}

	@Test
	public void testGetValidPropertyMapping()
	{
		Assert.assertEquals("OneToMany", marshaller.getValidPropertyMapping("onetomany"));
		Assert.assertEquals("ManyToOne", marshaller.getValidPropertyMapping("manytoone"));
		Assert.assertEquals("ManyToMany", marshaller.getValidPropertyMapping("manytomany"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFailGetValidPropertyMapping()
	{
		marshaller.getValidPropertyMapping("invalidMapping");
	}

	@Test
	public void testGetPropertyAnnotation()
	{
		PropertyType idProperty = new PropertyType();
		idProperty.setId(true);
		Assert.assertEquals("Id", marshaller.getPropertyAnnotation(idProperty, false));

		PropertyType simpleNonNullableProperty = new PropertyType();
		simpleNonNullableProperty.setNullable(false);
		Assert.assertEquals("Column", marshaller.getPropertyAnnotation(simpleNonNullableProperty, false));

		PropertyType simpleNullableProperty = new PropertyType();
		simpleNullableProperty.setNullable(true);
		Assert.assertEquals("Column(nullable = true)", marshaller.getPropertyAnnotation(simpleNullableProperty,
		                                                                                false));

		// no point testing a complex property as this just calls getValidPropertyMapping which is already tested
	}


	@Test
	public void testGetPropertyForReversedMappedEntity()
	{
		VelocityPropertyType velocityProperty = new VelocityPropertyType();

		velocityProperty.setName("reverseTestProperty");
		velocityProperty.setType("TestEntity");
		velocityProperty.setAnnotation("OneToMany");
		velocityProperty.setId(false);
		velocityProperty.setTextArea(false);
		velocityProperty.setCollection(true);
		velocityProperty.setComplex(true);
		velocityProperty.setSerialise(false);


		PropertyType property = new PropertyType();

		property.setName("TestProperty");
		property.setIncoming("ReverseTestProperty");
		property.setMapping("ManyToOne");

		VelocityPropertyType marshalledProperty = marshaller.getPropertyForReversedMappedEntity(property,
		                                                                                        "testEntity");

		Assert.assertEquals(velocityProperty.getName(), marshalledProperty.getName());
		Assert.assertEquals(velocityProperty.getType(), marshalledProperty.getType());
		Assert.assertEquals(velocityProperty.getAnnotation(), marshalledProperty.getAnnotation());

		Assert.assertEquals(velocityProperty.isCollection(), marshalledProperty.isCollection());
		Assert.assertEquals(velocityProperty.isComplex(), marshalledProperty.isComplex());
		Assert.assertEquals(velocityProperty.isId(), marshalledProperty.isId());
		Assert.assertEquals(velocityProperty.isSerialise(), marshalledProperty.isSerialise());
		Assert.assertEquals(velocityProperty.isTextArea(), marshalledProperty.isTextArea());
	}

	@Test
	public void testMarshallProperty()
	{
		VelocityPropertyType velocityProperty = new VelocityPropertyType();

		velocityProperty.setName("testProperty");
		velocityProperty.setType("String");
		velocityProperty.setAnnotation("Column(nullable = true)");
		velocityProperty.setId(false);
		velocityProperty.setTextArea(false);
		velocityProperty.setCollection(false);
		velocityProperty.setComplex(false);
		velocityProperty.setSerialise(true);


		PropertyType property = new PropertyType();

		property.setName("TestProperty");
		property.setType("String");

		VelocityPropertyType marshalledProperty = marshaller.marshallProperty(property,
		                                                                      "testEntity");

		Assert.assertEquals(velocityProperty.getName(), marshalledProperty.getName());
		Assert.assertEquals(velocityProperty.getType(), marshalledProperty.getType());
		Assert.assertEquals(velocityProperty.getAnnotation(), marshalledProperty.getAnnotation());

		Assert.assertEquals(velocityProperty.isCollection(), marshalledProperty.isCollection());
		Assert.assertEquals(velocityProperty.isComplex(), marshalledProperty.isComplex());
		Assert.assertEquals(velocityProperty.isId(), marshalledProperty.isId());
		Assert.assertEquals(velocityProperty.isSerialise(), marshalledProperty.isSerialise());
		Assert.assertEquals(velocityProperty.isTextArea(), marshalledProperty.isTextArea());
	}

	@Test
	public void testGetEntityIdType()
	{
		EntityType entityType = new EntityType();

		PropertyType propertyType = new PropertyType();
		propertyType.setId(true);
		propertyType.setType("long");

		entityType.getProperty().add(propertyType);

		Assert.assertEquals("long", marshaller.getEntityIdType(entityType));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMultipleIdsGetEntityIdType()
	{
		EntityType entityType = new EntityType();

		PropertyType propertyType1 = new PropertyType();
		propertyType1.setId(true);
		propertyType1.setType("long");

		PropertyType propertyType2 = new PropertyType();
		propertyType2.setId(true);
		propertyType2.setType("long");

		entityType.getProperty().add(propertyType1);
		entityType.getProperty().add(propertyType2);

		marshaller.getEntityIdType(entityType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFailNoIdGetEntityIdType()
	{
		marshaller.getEntityIdType(new EntityType());
	}


}
