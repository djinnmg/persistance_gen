package matthew.main;

import matthew.velocity.codegen.Templater;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * User: matthew
 */
public class Main
{
	public static void main(String[] args)
	{
		// load xml file
		// deserialise


		// for each entity in entities
			// create java entity file
			// create java dao file
			// create java daoImpl file
			// create jaxb file
			// create marshaller file
			// create unmarshaller file


		{
			// create java entity file

			// for each property

				//set property name

				// set property type (limit these if property is an id)
			    // need to check against other property names if type isn't one of predefined types (String Date etc.)
				// if exists in other properties, if incoming is set need to add a column of current property type to that property using incoming as variable name

				// set mapping (must be one of predefined types)


		}


		String entityTemplate = ""; // entity template
		VelocityEngine velocityEngine = new VelocityEngine();
		VelocityContext velocityContext = new VelocityContext();

		Templater entityTemplater = new Templater(entityTemplate,velocityEngine, velocityContext);

		String entityFileString = entityTemplater.process();

	}

}
