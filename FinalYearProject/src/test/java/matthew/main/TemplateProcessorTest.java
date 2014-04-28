package matthew.main;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TemplateProcessorTest
{
	private TemplateProcessor templateProcessor;

	private final String packagePath = "matthew.generation";
	private final String outputDir = "/home/matthew/Code";
	private final String projectName = "Demo";
	private final String dataModelLocation = "/DataModel/DataModel.xml";

	@Before
	public void setup()
	{
		templateProcessor =
				new TemplateProcessor(packagePath, outputDir, projectName, dataModelLocation);

	}

	@Test
	public void testGetPackageFilePath()
	{
		final String testPath = "test/File.Path";
		final String expectedString = "src/main/java/" + packagePath.replace(".", "/") + testPath;

		final String returnedString = templateProcessor.getPackageFilePath(testPath);

		Assert.assertEquals(expectedString, returnedString);

	}


}
