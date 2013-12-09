package matthew.generation.jaxb.serialisation;

import matthew.generation.hibernate.entities.project;
import matthew.generation.hibernate.entities.user;
import matthew.generation.jaxb.types.projectType;
import matthew.generation.jaxb.types.userType;

public class Marshaller
{

	public userType marshall(user user)
	{
		userType userType = new userType();

		userType.id = user.id;
		userType.name = user.name;

		return userType;
	}

	public projectType marshall(project project)
	{
		projectType projectType = new projectType();

		projectType.title = project.title;
		projectType.description = project.description;
		projectType.created = project.created.toDate();

		return projectType;
	}

}
