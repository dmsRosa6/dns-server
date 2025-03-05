package dmsrosa.dns_server.messages;

public abstract class QueryType {
    private final int value;

    private QueryType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    } 

    public static QueryType fromNum(int n){
        return switch (n) {
            case 1 -> new AQueryType();
            case 2 -> new NSQueryType();
            case 5 -> new CNameQueryType();
            case 15 -> new MXQueryType();
            case 28 -> new AAAAQueryType();
            default -> new UnknownQueryType(n);
        };
    }

    @Override
    public String toString(){
        return "Query type { " +
                "value: " + value +
                "}";
    }

    public static class AQueryType extends QueryType{

        public AQueryType() {
            super(1);
        }
    }

    public static class NSQueryType extends QueryType{

        public NSQueryType() {
            super(2);
        }
    }

    public static class CNameQueryType extends QueryType{

        public CNameQueryType() {
            super(5);
        }
    }

    public static class MXQueryType extends QueryType{

        public MXQueryType() {
            super(15);
        }
    }

    public static class AAAAQueryType extends QueryType{

        public AAAAQueryType() {
            super(28);
        }
    }

    public static class UnknownQueryType extends QueryType{

        private UnknownQueryType(int value) {
            super(value);
        }

    }
}
