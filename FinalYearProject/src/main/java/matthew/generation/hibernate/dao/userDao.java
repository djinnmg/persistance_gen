package matthew.generation.hibernate.dao;

import matthew.generation.hibernate.entities.project;

public interface userDao
{

	public project getprojectByuserId(long id);
}
