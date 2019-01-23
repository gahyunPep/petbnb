package pet.eaters.ca.petbnb.core;

public class Result<T> {
    private T data = null;
    private Exception exception = null;

    private Result(T data) {
        this.data = data;
    }

    private Result(Exception exception) {
        this.exception = exception;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> failed(Exception exception) {
        return new Result<>(exception);
    }

    public T getData() {
        return data;
    }

    public Exception getException() {
        return exception;
    }
}
