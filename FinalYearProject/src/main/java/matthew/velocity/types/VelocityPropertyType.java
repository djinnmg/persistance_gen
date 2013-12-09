
package matthew.velocity.types;

public class VelocityPropertyType
{
	private String annotation;
	private String name;
	private String type;
	private boolean isComplex;
	private boolean isCollection;
	private boolean isId;

	public boolean isSerialise()
	{
		return serialise;
	}

	public void setSerialise(boolean serialise)
	{
		this.serialise = serialise;
	}

	private boolean serialise;

	public String getAnnotation()
	{
		return annotation;
	}

	public void setAnnotation(String annotation)
	{
		this.annotation = annotation;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public boolean isComplex()
	{
		return isComplex;
	}

	public void setComplex(boolean complex)
	{
		isComplex = complex;
	}

	public boolean isCollection()
	{
		return isCollection;
	}

	public void setCollection(boolean collection)
	{
		isCollection = collection;
	}

	public boolean isId()
	{
		return isId;
	}

	public void setId(boolean id)
	{
		isId = id;
	}
}
