package matthew.generation.hibernate.entities;

import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class project
{


	@Id
	public long id;
	@Column(nullable = true)
	public String title;
	@ManyToOne
	public List<user> users;
	@Column(nullable = true)
	public String description;
	@Column(nullable = true)
	public DateTime created;

}
