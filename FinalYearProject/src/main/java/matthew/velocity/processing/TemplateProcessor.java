package matthew.velocity.processing;

import matthew.jaxb.types.EntitiesType;
import matthew.velocity.codegen.Templater;
import matthew.velocity.codegen.VelocityMarshaller;
import matthew.velocity.types.VelocityEntitiesType;
import matthew.velocity.types.VelocityEntityType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TemplateProcessor
{
	private final String packagePath;
	private final String outputDir;
	private final String projectName;

	public TemplateProcessor(final String packagePath, final String outputDir, final String projectName)
	{
		this.packagePath = packagePath;
		this.projectName = projectName;
		this.outputDir = outputDir;
	}

    /**
     * This processes an entitesType and ouputs the processed templates to file.
     *
     * @param entitiesType
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
			createEntities(entity);
			createDaos(entity);
			createDaoImpls(entity);
			createJaxbTypes(entity);
			createRESTServices(entity);
			createRESTServiceImpls(entity);
			createThymeleafCreateEntityPage(entity);
			createThymeleafEntitiesPage(entity);
			createThymeleafEntityPage(entity);
			createThymeleafUpdateEntityPage(entity);
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
	}

    /**
     * create the project pom. This supplies all required information for maven to build the output project.
     */
	private void createPom()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/PomTemplate.vm");

		entityTemplater.put("groupId", projectName);
		entityTemplater.put("artifactId", projectName);

		outputToFile("pom.xml", entityTemplater.process());
	}

    /**
     * Create the web.xml file. This provides the parameters for REST listener service and bootstrap location
     */
	private void createWebXML()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/WebXMLTemplate.vm");

		entityTemplater.put("displayName", projectName);

		outputToFile("src/main/webapp/WEB-INF/web.xml", entityTemplater.process());
	}

    /**
     * Create the service.properties. This contains any Guice configuration properties
     */
	private void createServiceProperties()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ServicePropertiesTemplate.vm");

		entityTemplater.put("package", packagePath);

		outputToFile("src/main/resources/service.properties", entityTemplater.process());
	}

    /**
     * Create the hibernate.properties file. This contains the hibernate connection configuration.
     */
	private void createHibernateProperties()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/HibernatePropertiesTemplate.vm");

        entityTemplater.put("projectName", projectName);

		outputToFile("src/main/resources/hibernate.properties", entityTemplater.process());
	}

    /**
     * Create the log4j.properties file. This contains the config for using a log4j Logger
     */
	private void createLoggingProperties()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/Log4jPropertiesTemplate.vm");

		outputToFile("src/main/resources/log4j.properties", entityTemplater.process());
	}

    /**
     * Creates the Hibernate entity Java file for the given entity
     * @param entity The entity to use in the generation
     */
	private void createEntities(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/EntityTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);
		entityTemplater.put("at", "@");

		outputToFile(getPackageFilePath("/hibernate/entities/" + entity.getName() + ".java"),
		             entityTemplater.process());
	}

    /**
     * Create the Hibernate Dao interface for the given entity.
     * @param entity
     */
	private void createDaos(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/DaoTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/hibernate/dao/" + entity.getName() + "Dao.java"), entityTemplater.process());
	}

    /**
     * Create the Hibernate Dao implementation for the given entity
     * @param entity
     */
	private void createDaoImpls(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/DaoImplTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/hibernate/dao/impl/" + entity.getName() + "DaoImpl.java"),
		             entityTemplater.process());
	}

    /**
     * Create the Jaxb type for the given entity
     * @param entity
     */
	private void createJaxbTypes(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/JaxbTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/jaxb/types/" + entity.getName() + "Type.java"), entityTemplater.process());
	}

    /**
     * Create the REST service interface for the given entity
     * @param entity
     */
	private void createRESTServices(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/RESTServiceTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/rest/service/" + entity.getName() + "Service.java"),
		             entityTemplater.process());
	}

    /**
     * Create the REST service implementations for the given entity
     * @param entity
     */
	private void createRESTServiceImpls(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/RESTServiceImplTemplate.vm");

		entityTemplater.put("entity", entity);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/rest/service/impl/" + entity.getName() + "ServiceImpl.java"),
		             entityTemplater.process());
	}

    /**
     * Create the Marshaller for the project. There will be a marshall method created for all serialisable non-complex properties of each entity in entities
     * @param entities
     */
	private void createMarshaller(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/MarshallerTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/jaxb/serialisation/Marshaller.java"), entityTemplater.process());
	}

    /**
     * Create the Unmarshaller for the project. There will be an unmarshall method created for all serialisable non-complex properties of each entity in entities
     * @param entities
     */
	private void createUnmarshaller(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/UnmarshallerTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/jaxb/serialisation/Unmarshaller.java"), entityTemplater.process());
	}

    /**
     * Create the Hibernate module java file. A binding to the hibernate entity will be created for each entity in entities
     * @param entities
     */
	private void createHibernateModule(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/GuiceHibernateModuleTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/guice/modules/DBModule.java"), entityTemplater.process());
	}

    /**
     * Create the REST module which will bind the REST service to the implementation for each entity in entities and
     * register the service with the index service.
     * @param entities
     */
	private void createRESTModule(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/GuiceRESTModuleTemplate.vm");

		entityTemplater.put("entities", entities);
		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/guice/modules/RESTModule.java"), entityTemplater.process());
	}

    /**
     * Create the Guice setup class which adds the modules for the hibernate entities, the REST services and thymeleaf
     */
	private void createGuiceSetup()
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/GuiceSetupTemplate.vm");

		entityTemplater.put("package", packagePath);

		outputToFile(getPackageFilePath("/guice/setup/Setup.java"), entityTemplater.process());
	}

	private void createDateHelper()
	{
		Templater templater = getTemplater("/VelocityTemplates/DateHelperTemplate.vm");

		templater.put("package", packagePath);

		outputToFile(getPackageFilePath("/util/DateHelper.java"), templater.process());
	}

	private void createIndexRESTService()
	{
		Templater templater = getTemplater("/VelocityTemplates/RESTIndexServiceTemplate.vm");

		templater.put("package", packagePath);

		outputToFile(getPackageFilePath("/rest/service/IndexService.java"), templater.process());
	}

	private void createIndexRESTServiceImpl()
	{
		Templater templater = getTemplater("/VelocityTemplates/RESTIndexServiceImplTemplate.vm");

		templater.put("package", packagePath);

		outputToFile(getPackageFilePath("/rest/service/impl/IndexServiceImpl.java"), templater.process());
	}

	private void createThymeleafIndexPage(final VelocityEntitiesType entities)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafIndexTemplate.vm");

		entityTemplater.put("entities", entities);

		outputToFile("src/main/webapp/WEB-INF/template/Index.html", entityTemplater.process());
	}

	private void createThymeleafCreateEntityPage(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafCreateEntityTemplate.vm");

		entityTemplater.put("entity", entity);

		outputToFile("src/main/webapp/WEB-INF/template/Create" + entity.getName() + "Template.html",
		             entityTemplater.process());
	}

	private void createThymeleafEntitiesPage(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafEntitiesTemplate.vm");

		entityTemplater.put("entity", entity);

		outputToFile("src/main/webapp/WEB-INF/template/" + entity.getName() + "ListTemplate.html",
		             entityTemplater.process());
	}

	private void createThymeleafEntityPage(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafEntityTemplate.vm");

		entityTemplater.put("entity", entity);

		outputToFile("src/main/webapp/WEB-INF/template/" + entity.getName() + "Template.html",
		             entityTemplater.process());
	}

	private void createThymeleafUpdateEntityPage(final VelocityEntityType entity)
	{
		Templater entityTemplater = getTemplater("/VelocityTemplates/ThymeleafUpdateEntityTemplate.vm");

		entityTemplater.put("entity", entity);

		outputToFile("src/main/webapp/WEB-INF/template/Update" + entity.getName() + "Template.html",
		             entityTemplater.process());
	}

	private Templater getTemplater(final String templatePath)
	{
		try
		{
			// checking file exists before trying to open
			InputStream is = getClass().getResourceAsStream(templatePath);

			if (is == null)
			{
				throw new RuntimeException("Could not read file at resources" + templatePath);
			}

			String entityTemplate = IOUtils.toString(is, "UTF-8");

			return new Templater(entityTemplate);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String getPackageFilePath(final String filePath)
	{
		return "src/main/java/" + packagePath.replace(".", "/") + filePath;
	}

	private void outputToFile(final String filePath, final String fileContents)
	{
		try
		{
			File file = new File(outputDir + "/" + projectName + "/" + filePath);

			if (file.exists())
			{
				file.delete();
			}

			file.getParentFile().mkdirs();


			System.out.println("Trying to output file to " + file.getAbsolutePath());

			FileUtils.writeStringToFile(file, fileContents);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}


}
