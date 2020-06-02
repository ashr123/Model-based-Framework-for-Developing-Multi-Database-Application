package dataLayer.readers.schemaReader;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.InputMismatchException;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class Schema
{
	private static Schema schema;

//	private static Graph<String, Pair<String, String>> schemaGraph;

	@JsonProperty("classes")
	private final Map<String/*class name*/, EntityClassData> classes = null;

	private Schema()
	{
	}

	public static void loadSchema(String url, ObjectMapper objectMapper) throws IOException
	{
		(schema = objectMapper.readValue(new File(url), Schema.class)).checkValidity();

////		noinspection unchecked
//		schemaGraph = new DirectedPseudograph<>((Class<? extends Pair<String, String>>) new Pair<>("", "").getClass());
//		Deque<String> unvisitedClasses = new LinkedList<>(Schema.getClassesName());
//
//		while (!unvisitedClasses.isEmpty())
//		{
//			String classToVisit = unvisitedClasses.poll();
//
//			schemaGraph.addVertex(classToVisit);
//			getEntityClass(classToVisit).getRelatedClasses()
//					.forEach((propertyName, relatedClassName) ->
//					{
//						schemaGraph.addVertex(relatedClassName);
//						schemaGraph.addEdge(classToVisit, relatedClassName, new Pair<>(classToVisit, propertyName));
//					});
//		}

//		for (Pair<String, String> e : schemaGraph.edgeSet())
//			System.out.println(schemaGraph.getEdgeSource(e) + " -[" + e + "]-> " + schemaGraph.getEdgeTarget(e));
	}

//	public static GraphPath<String, Pair<String, String>> getClassRelationPath(String srcClass, String destClass)
//	{
//		return new DijkstraShortestPath<>(schemaGraph).getPath(srcClass, destClass);
//	}

	public static EntityPropertyData getPropertyType(String className, String propertyName)
	{
		return schema.classes.get(className).getPropertyData(propertyName);
	}

	public static String getPropertyJavaType(String className, String propertyName)
	{
		final EntityPropertyData propertyData = schema.classes.get(className).getPropertyData(propertyName);
		return propertyData.getType().equals(PropertyType.ARRAY) ?  propertyData.getItems().getJavaType(): propertyData.getJavaType();
	}

	static void containsClass(String className)
	{
		if (!schema.classes.containsKey(className))
			throw new InputMismatchException("Class '" + className + "' doesn't exist!");
	}

	public static Collection<String> getClassPrimaryKey(String className)
	{
		return schema.classes.get(className).getPrimaryKey();
	}

//	public static EntityClassData getEntityClass(String className)
//	{
//		return schema.classes.get(className);
//	}

	public static Collection<String> getClassesName()
	{
		return schema.classes.keySet();
	}

	public static Collection<String> getClassesFields(String className)
	{
		return schema.classes.get(className).getClassPropertiesNames();
	}

	private void checkValidity()
	{
		classes.values()
				.forEach(EntityClassData::checkValidity);
	}

	@Override
	public String toString()
	{
		return "Schema{" +
		       "classes=" + classes +
		       '}';
	}
}
