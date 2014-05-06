package matthew.velocity.types;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VelocityEntityTypeTest
{
	private VelocityEntityType entity;
	private VelocityEntityType failEntity;

	@Before
	public void setup()
	{
		entity = getTestVelocityEntity();
		failEntity = getFailTestVelocityEntity();
	}

	private VelocityEntityType getTestVelocityEntity()
	{
		final VelocityEntityType velocityEntityType = new VelocityEntityType();

		velocityEntityType.setName("TestName");
		velocityEntityType.setIdType("long");

		return velocityEntityType;
	}

	private VelocityEntityType getFailTestVelocityEntity()
	{
		final VelocityEntityType velocityEntityType = new VelocityEntityType();

		velocityEntityType.setName("");
		velocityEntityType.setIdType("invalidType");
		return velocityEntityType;
	}


	@Test
	public void testGetCamelCaseName()
	{
		Assert.assertEquals("testName", entity.getCamelCaseName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFailGetCamelCase()
	{
		failEntity.getCamelCaseName();
	}

	@Test
	public void testGetCapitalisedIdType()
	{
		Assert.assertEquals("Long", entity.getCapitalisedIdType());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFailGetCapitalisedIdType()
	{
		failEntity.getCapitalisedIdType();
	}

}
