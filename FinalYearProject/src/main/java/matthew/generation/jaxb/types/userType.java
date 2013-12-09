package matthew.generation.jaxb.types;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class userType
{
	@XmlElement(name = "id")
	public long id;
	@XmlElement(name = "name")
	public String name;


}
