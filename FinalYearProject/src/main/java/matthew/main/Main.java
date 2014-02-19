package matthew.main;

/**
 * User: matthew
 */
public class Main
{

	public static void main(String[] args)
	{
		TemplateProcessor templateProcessor = new TemplateProcessor("matthew.generation", "/home/matthew/Code", "Demo ");

		templateProcessor.call();
	}
}

