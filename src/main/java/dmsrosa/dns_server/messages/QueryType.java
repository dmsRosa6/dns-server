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

    public static class AQueryType extends QueryType{

        private AQueryType() {
            super(1);
        }
    }
    public static class UnknownQueryType extends QueryType{

        private UnknownQueryType(int value) {
            super(value);
        }
    }
}
