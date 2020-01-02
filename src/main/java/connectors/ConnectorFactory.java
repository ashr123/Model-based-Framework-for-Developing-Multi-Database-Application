package connectors;

public class ConnectorFactory {
    public Connector getConnector(Enum connectorType){
        if(connectorType == null){
            return null;
        }
        if(connectorType.equalsIgnoreCase("NEO4J")){
            return new Circle();

        } else if(connectorType.equalsIgnoreCase("MONGODB")){
            return new Rectangle();

        } else if(connectorType.equalsIgnoreCase("MYSQL")){
            return new Square();

        } else if(connectorType.equalsIgnoreCase("CASSANDRA")){
            return new Square();

        } else if(connectorType.equalsIgnoreCase("REDIS")){
            return new Square();

        }

        switch (connectorType){
            case NEO4J:
            case MONGODB:
            case :
            case :
            case :
            default:
                return null;
        }

        return null;
    }
}
