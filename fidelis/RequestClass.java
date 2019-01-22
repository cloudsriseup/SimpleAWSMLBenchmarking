package fidelis;

import java.util.List;

public class RequestClass {
    List<Object> headers;

    public List<Object> getHeaders() {
        return this.headers;
    }

    public void setHeaders(List<Object> headers) {
        this.headers = headers;
    }

    public RequestClass(List<Object> headers) {
        this.headers = headers;
    }

    public RequestClass() {
    }
}
