import java.util.Arrays;

public class Main
{
	public static void main(String[] args)
	{
//		try (MongoClient mongoClient = MongoClients.create())
//		{
//			mongoClient.getDatabase("myDB").drop();
//			mongoClient.getDatabase("myDB")
//					.getCollection("Person")
//					.insertOne(new Document("name", "Alice")
//							.append("age", 18)
//							.append("phoneNumber", "0504563434")
//							.append("emailAddress", "Alice@Bob.com"));
//		}

		System.out.println(Arrays.stream(new int[]{1, 2, 3})
				.reduce((acc, b) ->
				{
					System.out.println("acc: " + acc + ", b: " + b);
					return acc + b;
				})
				.getAsInt());
//		new MongoDBAdapter()
//				.revealQuery(createMany()
//						.add(entity("Person")
//								.append("name", "Karin")
//								.append("age", 26)
//								.append("phoneNumber", "496351")
//								.append("emailAddress", "karin@gmail.com"))
//						.add(entity("Person")
//								.append("name", "Yossi")
//								.append("age", 21)
//								.append("phoneNumber", "0587158627")
//								.append("emailAddress", "yossi@gmail.com")));
	}
}
