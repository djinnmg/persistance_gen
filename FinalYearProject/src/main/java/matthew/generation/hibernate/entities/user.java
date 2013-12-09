package matthew.generation.hibernate.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class user
{


	@Id
	public long id;
	@Column
	public String name;
	@OneToMany
	public project projectForUser;

}
