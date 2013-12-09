package matthew.generation.jaxb.types;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class projectType
{
	@XmlElement(name = "title")
	public String title;
	@XmlElement(name = "description")
	public String description;
	@XmlElement(name = "created")
	public Date created;


}
