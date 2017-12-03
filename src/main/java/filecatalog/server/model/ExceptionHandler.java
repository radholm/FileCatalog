package filecatalog.server.model;

public class ExceptionHandler extends Exception {
    public ExceptionHandler(String reason){
        super(reason);
    }

    public ExceptionHandler(String reason, Throwable cause) {
        super(reason,cause);
    }
}
