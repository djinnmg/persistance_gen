package matthew.generation.hibernate.dao.impl;

import com.peterphi.std.guice.hibernate.dao.HibernateDao;
import matthew.generation.hibernate.dao.projectDao;
import matthew.generation.hibernate.entities.project;
import matthew.generation.hibernate.entities.user;

import java.util.List;

public class projectDaoImpl extends HibernateDao<project, Long> implements projectDao
{


	public List<user> getuserByprojectId(long id)
	{
		return getById(id).users;
	}

}
