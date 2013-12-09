package matthew.generation.hibernate.dao.impl;

import com.peterphi.std.guice.hibernate.dao.HibernateDao;
import matthew.generation.hibernate.dao.userDao;
import matthew.generation.hibernate.entities.project;
import matthew.generation.hibernate.entities.user;

public class userDaoImpl extends HibernateDao<user, Long> implements userDao
{


	public project getprojectByuserId(long id)
	{
		return getById(id).projectForUser;
	}

}
