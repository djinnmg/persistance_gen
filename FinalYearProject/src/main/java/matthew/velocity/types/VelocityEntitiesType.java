package matthew.velocity.types;

import java.util.List;

public class VelocityEntitiesType
{

	private List<VelocityEntityType> entities;

	public List<VelocityEntityType> getEntities()
	{
		return entities;
	}

	public void setEntities(List<VelocityEntityType> entities)
	{
		this.entities = entities;
	}

	public String getPackagePath()
	{
		return packagePath;
	}

	public void setPackagePath(String packagePath)
	{
		this.packagePath = packagePath;
	}

	private String packagePath;
}
