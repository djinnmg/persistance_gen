package matthew.velocity.processing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TemplateProcessorTest
{
	private TemplateProcessor templateProcessor;

	private final String packagePath = "matthew.generation";
	private final String outputDir = "/home/matthew/Code";
	private final String projectName = "Test";


	@Before
	public void setup()
	{
		templateProcessor =
				new TemplateProcessor(packagePath, outputDir, projectName);

	}

	@Test
	public void testGetPackageFilePath()
	{
		final String testPath = "test/File.Path";
		final String expectedString = "src/main/java/" + packagePath.replace(".", "/") + testPath;

		Assert.assertEquals(expectedString, templateProcessor.getPackageFilePath(testPath));
	}

	@Test
	public void testGetResourcesFilePath()
	{
		final String testPath = "test/File.Path";
		final String expectedString = "src/main/resources/" + testPath;

		Assert.assertEquals(expectedString, templateProcessor.getResourcesFilePath(testPath));
	}


	@Test
	public void testGetThymeleafFilePath()
	{
		final String testPath = "test/File.Path";
		final String expectedString = "src/main/webapp/WEB-INF/template/" + testPath;

		Assert.assertEquals(expectedString, templateProcessor.getThyleleafTemplateFilePath(testPath));
	}

	@Test
	public void testGetOutputFilePath()
	{
		final String testPath = "test/path/file.java";
		final String expectedString = outputDir + "/" + projectName + "/" + testPath;

		Assert.assertEquals(expectedString, templateProcessor.getOutputFilePath(testPath));
	}

	@Test(expected = RuntimeException.class)
	public void testFailedGetOutputFilePath()
	{
		final String testPath = "test";

		templateProcessor.getOutputFilePath(testPath);
	}

	@Test
	public void testValidateOutputFilePath()
	{
		// valid paths
		Assert.assertTrue(templateProcessor.validateOutputFilePath("/test/path.extension"));
		Assert.assertTrue(templateProcessor.validateOutputFilePath("/tes1t/path2.extnsion"));

		// invalid paths
		Assert.assertFalse(templateProcessor.validateOutputFilePath(""));
		Assert.assertFalse(templateProcessor.validateOutputFilePath("/"));
		Assert.assertFalse(templateProcessor.validateOutputFilePath("/.extension"));
		Assert.assertFalse(templateProcessor.validateOutputFilePath("/test"));
		Assert.assertFalse(templateProcessor.validateOutputFilePath("/1test/path.extension"));
		Assert.assertFalse(templateProcessor.validateOutputFilePath("/$test/path.extension"));
		Assert.assertFalse(templateProcessor.validateOutputFilePath("/test/path."));
	}

	@Test
	public void testValidateOutputDirectoryPath()
	{
		// valid paths
		Assert.assertTrue(templateProcessor.validateOutputDirectoryPath("/test"));
		Assert.assertTrue(templateProcessor.validateOutputDirectoryPath("/test/path"));
		Assert.assertTrue(templateProcessor.validateOutputDirectoryPath("/test2"));
		Assert.assertTrue(templateProcessor.validateOutputDirectoryPath("/test/path2"));

		// invalid paths
		Assert.assertFalse(templateProcessor.validateOutputDirectoryPath(""));
		Assert.assertFalse(templateProcessor.validateOutputDirectoryPath("/"));
		Assert.assertFalse(templateProcessor.validateOutputDirectoryPath("/test/"));
		Assert.assertFalse(templateProcessor.validateOutputDirectoryPath("/1test"));
		Assert.assertFalse(templateProcessor.validateOutputDirectoryPath("/$test"));
	}

	@Test
	public void testValidatePackagePath()
	{
		// valid paths
		Assert.assertTrue(templateProcessor.validatePackagePath("test"));
		Assert.assertTrue(templateProcessor.validatePackagePath("test.path"));
		Assert.assertTrue(templateProcessor.validatePackagePath("test2.path"));
		Assert.assertTrue(templateProcessor.validatePackagePath("test2.path2"));

		// invalid paths
		Assert.assertFalse(templateProcessor.validatePackagePath("test."));
		Assert.assertFalse(templateProcessor.validatePackagePath("test.path."));
		Assert.assertFalse(templateProcessor.validatePackagePath("2test"));
		Assert.assertFalse(templateProcessor.validatePackagePath("2Test.2path"));
		Assert.assertFalse(templateProcessor.validatePackagePath("$test"));
	}

	@Test
	public void testValidateProjectName()
	{
		// valid names
		Assert.assertTrue(templateProcessor.validateProjectName("Test"));
		Assert.assertTrue(templateProcessor.validateProjectName("Test2"));

		// invalid names
		Assert.assertFalse(templateProcessor.validateProjectName("2Test"));
		Assert.assertFalse(templateProcessor.validateProjectName("$test"));
		Assert.assertFalse(templateProcessor.validateProjectName("test.name"));
	}




}
