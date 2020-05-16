package dataLayer.readers.schemaReader;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import dataLayer.crud.Pair;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedPseudograph;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class Schema
{
	private final static ObjectMapper objectMapper = new ObjectMapper();

	private static Schema schema;

	private static Graph<String, Pair<String, String>> schemaGraph;

	@JsonProperty("classes")
	private final Map<String/*class name*/, EntityClassData> classes = null;

	private Schema()
	{
	}

	public static void loadSchema(URL url) throws IOException
	{
		schema = objectMapper.readValue(url, Schema.class);
		schema.checkValidity();

		//noinspection unchecked
		schemaGraph = new DirectedPseudograph<>((Class<? extends Pair<String, String>>) new Pair<>("", "").getClass());
		Deque<String> unvisitedClasses = new LinkedList<>(Schema.getClassesName());

		while (!unvisitedClasses.isEmpty())
		{
			String classToVisit = unvisitedClasses.poll();

			schemaGraph.addVertex(classToVisit);
			getEntityClass(classToVisit).getRelatedClasses()
					.forEach((propertyName, relatedClassName) ->
					{
						schemaGraph.addVertex(relatedClassName);
						schemaGraph.addEdge(classToVisit, relatedClassName, new Pair<>(classToVisit, propertyName));
					});
		}

//		for (Pair<String, String> e : schemaGraph.edgeSet())
//			System.out.println(schemaGraph.getEdgeSource(e) + " -[" + e + "]-> " + schemaGraph.getEdgeTarget(e));
	}

	public static GraphPath<String, Pair<String, String>> getClassRelationPath(String srcClass, String destClass)
	{
		return new DijkstraShortestPath<>(schemaGraph).getPath(srcClass, destClass);
	}

	public static EntityPropertyData getPropertyType(String className, String propertyName)
	{
		return schema.classes.get(className).getPropertyData(propertyName);
	}

	public static String getPropertyJavaType(String className, String propertyName)
	{
		return schema.classes.get(className).getPropertyData(propertyName).getJavaType();
	}

	static void containsClass(String className)
	{
		if (!schema.classes.containsKey(className))
			throw new InputMismatchException("Class '" + className + "' doesn't exist!");
	}

	public static void containsAllClasses(Collection<String> classNames)
	{
		if (!schema.classes.keySet().containsAll(classNames))
			throw new InputMismatchException("There is an unknown class in Schema!");
	}

	public static EntityClassData getEntityClass(String className)
	{
		return schema.classes.get(className);
	}

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
