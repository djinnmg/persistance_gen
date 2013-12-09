package matthew.velocity.codegen;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;

/**
 * User: matthew
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


	public void put(String key, Object value)
	{
		context.put(key, value);
	}


	public String process()
	{
		final StringWriter sw = new StringWriter();

		engine.evaluate(context, sw, getClass().getName(), template);

		return sw.toString();
	}

}
