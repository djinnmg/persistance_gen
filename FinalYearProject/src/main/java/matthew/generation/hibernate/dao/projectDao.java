package matthew.generation.hibernate.dao;

import matthew.generation.hibernate.entities.user;

import java.util.List;

public interface projectDao
{

	public List<user> getuserByprojectId(long id);
}
