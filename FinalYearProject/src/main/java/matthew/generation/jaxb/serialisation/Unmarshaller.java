package matthew.generation.jaxb.serialisation;

import matthew.generation.hibernate.entities.project;
import matthew.generation.hibernate.entities.user;
import matthew.generation.jaxb.types.projectType;
import matthew.generation.jaxb.types.userType;
import org.joda.time.DateTime;

public class Unmarshaller
{

	public user unmarshall(userType userType)
	{
		user user = new user();

		user.id = userType.id;
		user.name = userType.name;
		return user;
	}

	public project unmarshall(projectType projectType)
	{
		project project = new project();

		project.title = projectType.title;
		project.description = projectType.description;
		project.created = new DateTime(projectType.created);
		return project;
	}

}
