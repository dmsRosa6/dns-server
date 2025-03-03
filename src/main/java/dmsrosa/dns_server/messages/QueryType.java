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
        if(n == 1){
            return new AQueryType();
        }

        return new UnknownQueryType(n);
    }

    @Override
    public String toString(){
        return "Query type {" +
                "value: " + value +
                "}";
    }

    public static class AQueryType extends QueryType{

        public AQueryType() {
            super(1);
        }
    }
    public static class UnknownQueryType extends QueryType{

        private UnknownQueryType(int value) {
            super(value);
        }

    }
}
