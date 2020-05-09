package dataLayer.readers.schemaReader;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedPseudograph;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class Schema
{
	private final static ObjectMapper objectMapper = new ObjectMapper();

	private static Schema schema;

	private static Graph<Map.Entry<String, EntityClassData>, RelationType> schemaGraph;

	@JsonProperty("classes")
	private final Map<String/*class name*/, EntityClassData> classes = null;

	private Schema()
	{
	}

	public static void loadSchema(URL url) throws IOException
	{
		schema = objectMapper.readValue(url, Schema.class);
		schema.checkValidity();

		schemaGraph = new DirectedPseudograph<>(RelationType.class);
		Deque<String> unvisitedClasses = new LinkedList<>(Schema.getClassesName());
		String classToVisit;

		while(!unvisitedClasses.isEmpty()){
			classToVisit = unvisitedClasses.peekFirst();
			Map<String, EntityClassData> relatedClasses = getEntityClass(classToVisit).getRelatedClasses();

			Map.Entry<String, EntityClassData> v = new AbstractMap.SimpleEntry<>(classToVisit,getEntityClass(classToVisit));
			schemaGraph.addVertex(v);
			relatedClasses.forEach((relatedClassName, relatedClassData) ->
			{
				Map.Entry<String, EntityClassData> v1 = new AbstractMap.SimpleEntry<>(relatedClassName,relatedClassData);
				schemaGraph.addVertex(v1);
				schemaGraph.addEdge(v, v1);
			});
			unvisitedClasses.pollFirst();
		}
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
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof Schema))
			return false;
		return classes.equals(((Schema) o).classes);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(classes);
	}

	@Override
	public String toString()
	{
		return "Schema{" +
		       "classes=" + classes +
		       '}';
	}
}
