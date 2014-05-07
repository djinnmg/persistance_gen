package matthew.velocity.processing;

import matthew.jaxb.types.EntitiesType;
import matthew.velocity.codegen.Templater;
import matthew.velocity.codegen.VelocityMarshaller;
import matthew.velocity.types.VelocityEntitiesType;
import matthew.velocity.types.VelocityEntityType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

public class TemplateProcessor
{
	public static transient Logger log = Logger.getLogger(TemplateProcessor.class);

	private final String packagePath;
	private final String outputDir;
	private final String projectName;

	public TemplateProcessor(final String packagePath, final String outputDir, final String projectName)
	{
		if (!validatePackagePath(packagePath))
		{
			throw new RuntimeException("Package path " + packagePath +
			                           " is not valid! Package path must be alphanumeric with a non numeric first " +
			                           "character using periods for separation between package levels.");
		}

		if (!validateOutputDirectoryPath(outputDir))
		{
			throw new RuntimeException("Output directory " + outputDir +
			                           " is not valid! Output directory must match the following rules. Is " +
			                           "alphanumeric, first character is not numeric, " +
			                           "starts from root level but cannot be root, uses forward slashes to separate " +
			                           "directory levels and has no trailing slashes.");
		}

		if (!validateProjectName(projectName))
		{
			throw new RuntimeException("Project name " + projectName +
			                           " is not valid! Project name must be fully alphanumeric with the first " +
			                           "character being non-numeric.");
		}

		this.packagePath = packagePath;
		this.projectName = projectName;
		this.outputDir = outputDir;
	}

	/**
	 * This processes an entitiesType and outputs the processed templates to file.
	 *
	 * @param entitiesType The entities to processTemplate and generate file for
	 */
	public void processEntities(final EntitiesType entitiesType)
	{
		VelocityMarshaller velocityMarshaller = new VelocityMarshaller(entitiesType);
		VelocityEntitiesType entities = velocityMarshaller.marshall();

		createPom();
		createWebXML();
		createServiceProperties();
		createHibernateProperties();
		createLoggingProperties();

		// Create and output a file for each of these for each entity
		for (VelocityEntityType entity : entities.getEntities())
		{
			createEntity(entity);
			createDao(entity);
			createDaoImpl(entity);
			createJaxbType(entity);
			createRESTService(entity);
			createRESTServiceImpl(entity);
			createThymeleafCreateEntityPage(entity);
			createThymeleafEntitiesPage(entity);
			createThymeleafEntityPage(entity);
			createThymeleafUpdateEntityPage(entity);

			createRESTServiceTestImpl(entity);
		}

		createMarshaller(entities);
		createUnmarshaller(entities);
		createRESTModule(entities);
		createHibernateModule(entities);
		createGuiceSetup();
		createDateHelper();

		createIndexRESTService();
		createIndexRESTServiceImpl();
		createThymeleafIndexPage(entities);

		createTestRESTModule(entities);
		createMarshallerTest(entities);
		createUnmarshallerTest(entities);
		createDateHelperTest();
	}

	/**
	 * create the project pom. This supplies all required information for maven to build the output project.
	 */
	private void createPom()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/PomTemplate.vm");

		entityTemplater.put("groupId", projectName);
		entityTemplater.put("artifactId", projectName);

		outputToFile("pom.xml", entityTemplater.processTemplate());
	}

	/**
	 * Create the web.xml file. This provides the parameters for REST listener service and bootstrap location
	 */
	private void createWebXML()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/WebXMLTemplate.vm");

		entityTemplater.put("displayName", projectName);

		outputToFile("src/main/webapp/WEB-INF/web.xml", entityTemplater.processTemplate());
	}

	/**
	 * Create the service.properties. This contains any Guice configuration properties
	 */
	private void createServiceProperties()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ServicePropertiesTemplate.vm");

		entityTemplater.put("package", packagePath);

		outputToFile(getResourcesFilePath("service.properties"), entityTemplater.processTemplate());
	}

	/**
	 * Create the hibernate.properties file. This contains the hibernate connection configuration.
	 */
	private void createHibernateProperties()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/HibernatePropertiesTemplate.vm");

		entityTemplater.put("projectName", projectName);

		outputToFile(getResourcesFilePath("hibernate.properties"), entityTemplater.processTemplate());
	}

	/**
	 * Create the log4j.properties file. This contains the config for using a log4j Logger
	 */
	private void createLoggingProperties()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/Log4jPropertiesTemplate.vm");

		entityTemplater.put("package", packagePath);

		outputToFile(getResourcesFilePath("log4j.properties"), entityTemplater.processTemplate());
	}

	/**
	 * Creates the Hibernate entity Java file for the given entity
	 *
	 * @param entity The entity to use in the generation
	 */
	private void createEntity(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/EntityTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);
		entityTemplater.put("at", "@");

		outputToFile(getPackageFilePath("/hibernate/entities/" + entity.getName() + ".java"),
		             entityTemplater.processTemplate());
	}

	/**
	 * Create the Hibernate Dao interface for the given entity.
	 *
	 * @param entity The entity to create the DAO for
	 */
	private void createDao(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/DaoTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/hibernate/dao/" + entity.getName() + "Dao.java"),
		             entityTemplater.processTemplate());
	}

	/**
	 * Create the Hibernate Dao implementation for the given entity
	 *
	 * @param entity The entity to create the DAO implementation for
	 */
	private void createDaoImpl(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/DaoImplTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/hibernate/dao/impl/" + entity.getName() + "DaoImpl.java"),
		             entityTemplater.processTemplate());
	}

	/**
	 * Create the Jaxb type for the given entity
	 *
	 * @param entity The entity to create the JAX-B type for
	 */
	private void createJaxbType(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/JaxbTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/jaxb/types/" + entity.getName() + "Type.java"),
		             entityTemplater.processTemplate());
	}

	/**
	 * Create the REST service interface for the given entity
	 *
	 * @param entity The entity to create the REST service for
	 */
	private void createRESTService(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/RESTServiceTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/rest/service/" + entity.getName() + "Service.java"),
		             entityTemplater.processTemplate());
	}

	/**
	 * Create the REST service implementations for the given entity
	 *
	 * @param entity The entity to create the REST service implementation for
	 */
	private void createRESTServiceImpl(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/RESTServiceImplTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/rest/service/impl/" + entity.getName() + "ServiceImpl.java"),
		             entityTemplater.processTemplate());
	}

	/**
	 * Create the Marshaller for the project. There will be a marshall method created for all serialisable
	 * non-complex properties of each entity in entities
	 *
	 * @param entities The entities which need to have marshall methods created for them
	 */
	private void createMarshaller(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/MarshallerTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/jaxb/serialisation/Marshaller.java"), entityTemplater.processTemplate());
	}

	/**
	 * Create the Unmarshaller for the project. There will be an unmarshall method created for all serialisable
	 * non-complex properties of each entity in entities
	 *
	 * @param entities The entities which need to have unmarshall methods created for them
	 */
	private void createUnmarshaller(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/UnmarshallerTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/jaxb/serialisation/Unmarshaller.java"), entityTemplater.processTemplate());
	}

	/**
	 * Create the Hibernate module java file. A binding to the hibernate entity will be created for each entity in
	 * entities
	 *
	 * @param entities The entities which need to be added to the hibernate module
	 */
	private void createHibernateModule(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/GuiceHibernateModuleTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/guice/modules/DBModule.java"), entityTemplater.processTemplate());
	}

	/**
	 * Create the REST module which will bind the REST service to the implementation for each entity in entities and
	 * register the service with the index service.
	 *
	 * @param entities The entities which will need to have REST bindings created for them
	 */
	private void createRESTModule(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/GuiceRESTModuleTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/guice/modules/RESTModule.java"), entityTemplater.processTemplate());
	}

	/**
	 * Create the Guice setup class which adds the modules for the hibernate entities, the REST services and thymeleaf
	 */
	private void createGuiceSetup()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/GuiceSetupTemplate.vm");

		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/guice/setup/Setup.java"), entityTemplater.processTemplate());
	}

	/**
	 * Create the DataHelper utility which will help with the processing and formatting of dates for using in thymeleaf
	 */
	private void createDateHelper()
	{
		Templater templater = getTemplater("/VelocityTemplates/DateHelperTemplate.vm");

		templater.put("package", packagePath);

		outputToFile(getPackageFilePath("/util/DateHelper.java"), templater.processTemplate());
	}

	/**
	 * Create the REST service for the index page, this will be accessed at {tomcatHostURL}/ProjectName/
	 */
	private void createIndexRESTService()
	{
		Templater templater = getTemplater("/VelocityTemplates/RESTIndexServiceTemplate.vm");

		templater.put("package", packagePath);

		outputToFile(getPackageFilePath("/rest/service/IndexService.java"), templater.processTemplate());
	}

	/**
	 * Create the implementation for the REST index service. This will show all the entities and provide links to the
	 * overviews for each entity
	 */
	private void createIndexRESTServiceImpl()
	{
		Templater templater = getTemplater("/VelocityTemplates/RESTIndexServiceImplTemplate.vm");

		templater.put("package", packagePath);

		outputToFile(getPackageFilePath("/rest/service/impl/IndexServiceImpl.java"), templater.processTemplate());
	}

	/**
	 * Create the thymeleaf template for the index page. Contains a list of all entities with links to entity overviews
	 *
	 * @param entities The entities to create the entries and link for on the index page
	 */
	private void createThymeleafIndexPage(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafIndexTemplate.vm");

		entityTemplater.put("entities", entities);

		outputToFile(getThyleleafTemplateFilePath("Index.html"), entityTemplater.processTemplate());
	}

	/**
	 * Create the Thymeleaf template for creating a new entity of the type of the passed in VelocityEntity
	 *
	 * @param entity The VelocityEntity to get the type of entity creation page from
	 */
	private void createThymeleafCreateEntityPage(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafCreateEntityTemplate.vm");

		entityTemplater.put("entity", entity);

		outputToFile(getThyleleafTemplateFilePath("Create" + entity.getName() + "Template.html"),
		             entityTemplater.processTemplate());
	}

	/**
	 * Create the Thymeleaf template for the overview page of the passed in VelocityEntity
	 *
	 * @param entity The VelocityEntity to get the type of the overview page from
	 */
	private void createThymeleafEntitiesPage(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafEntitiesTemplate.vm");

		entityTemplater.put("entity", entity);

		outputToFile(getThyleleafTemplateFilePath(entity.getName() + "ListTemplate.html"),
		             entityTemplater.processTemplate());
	}

	/**
	 * Create the Thymeleaf template for the individual entity page of the type of the passed in VelocityEntity
	 *
	 * @param entity The VelocityEntity to get the type of the individual view page from
	 */
	private void createThymeleafEntityPage(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafEntityTemplate.vm");

		entityTemplater.put("entity", entity);

		outputToFile(getThyleleafTemplateFilePath(entity.getName() + "Template.html"),
		             entityTemplater.processTemplate());
	}

	/**
	 * Creates the Thymeleaf template for the update page of the type of the passed in VelocityEntity
	 *
	 * @param entity The VelocityEntity to get the type of entity update page from
	 */
	private void createThymeleafUpdateEntityPage(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafUpdateEntityTemplate.vm");

		entityTemplater.put("entity", entity);

		outputToFile(getThyleleafTemplateFilePath("Update" + entity.getName() + "Template.html"),
		             entityTemplater.processTemplate());
	}

	/**
	 * Create the test implementation of the REST service for the passed in entity
	 *
	 * @param entity The entity to create the REST service test implementation for
	 */
	private void createRESTServiceTestImpl(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/RESTServiceTestImplTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/rest/service/impl/test/" + entity.getName() + "ServiceTestImpl.java"),
		             entityTemplater.processTemplate());
	}

	/**
	 * Create the Guice module for binding the test implementations of the REST services.
	 *
	 * @param entities The entities which will need to have REST bindings created for them
	 */
	private void createTestRESTModule(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/GuiceTestRESTModuleTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/guice/modules/TestRESTModule.java"), entityTemplater.processTemplate());
	}

	/**
	 * Create the junit tests for the marshaller class
	 *
	 * @param entities The entities which will need test methods created for them.
	 */
	private void createMarshallerTest(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/MarshallerTestTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getTestPackageFilePath("/jaxb/serialisation/MarshallerTest.java"),
		             entityTemplater.processTemplate());
	}

	/**
	 * Create the junit tests for the unmarshaller class
	 *
	 * @param entities The entities which will need test methods created for them.
	 */
	private void createUnmarshallerTest(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/UnmarshallerTestTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getTestPackageFilePath("/jaxb/serialisation/UnmarshallerTest.java"),
		             entityTemplater.processTemplate());
	}

	/**
	 * Create the junit tests for the DateHelper class
	 */
	private void createDateHelperTest()
	{
		Templater templater = getTemplater("/VelocityTemplates/DateHelperTestTemplate.vm");

		templater.put("package", packagePath);

		outputToFile(getTestPackageFilePath("/util/DateHelperTest.java"), templater.processTemplate());
	}

	/**
	 * This will get a new instance of templater, using the passed in file path as the location of the template to load
	 *
	 * @param templatePath The location of the template to load
	 * @return The new instance of templater with the template at templatePath loaded
	 */
	private Templater getTemplater(final String templatePath)
	{
		try
		{
			InputStream is = getClass().getResourceAsStream(templatePath);

			// checking file exists before trying to open
			if (is == null)
			{
				throw new RuntimeException("Could not read file at resources" + templatePath);
			}

			String entityTemplate = IOUtils.toString(is, "UTF-8");

			return new Templater(entityTemplate);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Error while trying to read resource at " + templatePath + ". " + e
					.getMessage(),
			                           e
			);
		}
	}

	/**
	 * Gets the full package file path for a java file
	 *
	 * @param filePath The file path to append to the default location for java files
	 * @return The full package file path
	 */
	public String getPackageFilePath(final String filePath)
	{
		return "src/main/java/" + packagePath.replace(".", "/") + filePath;
	}

	/**
	 * Gets the full file path for a resource file.
	 *
	 * @param filePath The file path to append to the default resource location
	 * @return The full resources file path
	 */
	public String getResourcesFilePath(final String filePath)
	{
		return "src/main/resources/" + filePath;
	}

	/**
	 * Gets the full file path for a Thymeleaf template file.
	 *
	 * @param filePath The file path to append to the default template location
	 * @return The full template file path
	 */
	public String getThyleleafTemplateFilePath(final String filePath)
	{
		return "src/main/webapp/WEB-INF/template/" + filePath;
	}

	/**
	 * Gets the full file path for a Java test file.
	 *
	 * @param filePath The file path to append to the default location for Java test files
	 * @return The full test package file path
	 */
	public String getTestPackageFilePath(final String filePath)
	{
		return "src/test/java/" + packagePath.replace(".", "/") + filePath;
	}


	/**
	 * This will output fileContents to file at filePath within the generate project location
	 *
	 * @param filePath     The file path to append to the project location
	 * @param fileContents The contents of the file to be created
	 */
	private void outputToFile(final String filePath, final String fileContents)
	{
		try
		{
			File file = new File(getOutputFilePath(filePath));

			if (file.exists())
			{
				file.delete();
			}

			file.getParentFile().mkdirs();


			log.debug("Trying to output file to " + file.getAbsolutePath());

			FileUtils.writeStringToFile(file, fileContents);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Create the absolute file path for a file we wish to output to
	 *
	 * @param filePath The relative file path of where the file should be output
	 * @return The absolute file path
	 */
	public String getOutputFilePath(final String filePath)
	{
		final String absoluteFilePath = outputDir + "/" + projectName + "/" + filePath;

		if (!validateOutputFilePath(absoluteFilePath))
		{
			log.warn("Invalid output file path: " + absoluteFilePath);
			throw new RuntimeException("Absolute file path " + absoluteFilePath +
			                           " is not valid! The file path must be alphanumeric with a non-numeric first " +
			                           "character for each level, must start from root, " +
			                           "use a forward slash as the separator between directory levels and end with " +
			                           ".{extension}.");
		}
		else
		{
			return absoluteFilePath;
		}
	}

	/**
	 * Validates the absolute path of a file to be output
	 *
	 * @param outputFilePath The path to be validated
	 * @return true if valid, false otherwise
	 */
	public boolean validateOutputFilePath(final String outputFilePath)
	{
		log.debug("Validating output file path " + outputFilePath);

		// must be alphanumeric with 1st char of each level being non numeric, must start from /,
		// uses / for directoty level separation and ends with .{extension}
		return Pattern.matches("^/[a-zA-Z][a-zA-Z0-9-_]*(/[a-zA-Z][a-zA-Z0-9-_]*)*\\.[a-zA-Z0-9]+$", outputFilePath);
	}


	/**
	 * Validates an output directory
	 *
	 * @param outputPath the output path to validate
	 * @return true if valid, false otherwise
	 */
	public boolean validateOutputDirectoryPath(final String outputPath)
	{
		log.debug("Validating output directory: " + outputPath);

		// only alphanumeric with 1st char being a letter, no trailing slashes, / not allowed either, hyphens and
		// underscores are allowed
		return Pattern.matches("^/[a-zA-Z_-][a-zA-Z0-9_-]*(/[a-zA-Z_-][a-zA-Z0-9_-]*)*$", outputPath);
	}

	/**
	 * Validates a package path
	 *
	 * @param packagePath The package path to be validated
	 * @return true if valid, false otherwise
	 */
	public boolean validatePackagePath(final String packagePath)
	{
		log.debug("Validating package path...");
		return Pattern.matches("^[a-z][a-z0-9_]*(.[a-z][a-z0-9_]*)*$", packagePath);
	}

	/**
	 * Validates a project name
	 *
	 * @param projectName The project name to be validated
	 * @return true if valid, false otherwise
	 */
	public boolean validateProjectName(final String projectName)
	{
		log.debug("Validating project name...");
		return Pattern.matches("^[a-zA-Z][a-zA-Z0-9]*$", projectName);
	}

}
