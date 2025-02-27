package dmsrosa.dns_server.messages;

public enum QueryType {
    UNKNOWN(-1),
    A(1);

    private final int value;

    private QueryType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    } 

    public static QueryType fromNum(int n){
        if(n == 1){
            return A;
        }

        return UNKNOWN;
    }
}
