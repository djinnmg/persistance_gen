package matthew.velocity.codegen;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;

/**
 * Helper class to ease Velocity template processing
 */
public class Templater
{
	private final String template;
	private final VelocityEngine engine;
	private final VelocityContext context;

	public Templater(final String template)
	{
		this.template = template;
		this.engine = new VelocityEngine();
		this.context = new VelocityContext();
	}

	/**
	 * Puts a provided value into the VelocityContext
	 *
	 * @param key   The name to key the provided value with
	 * @param value The value to insert into the VelocityContext
	 */
	public void put(String key, Object value)
	{
		context.put(key, value);
	}

	/**
	 * Processes the template using the VelocityEngine and outputs processed template as a String
	 *
	 * @return the processed template
	 */
	public String processTemplate()
	{
		final StringWriter sw = new StringWriter();

		engine.evaluate(context, sw, getClass().getName(), template);

		return sw.toString();
	}

}
