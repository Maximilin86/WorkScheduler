package me.maxpro.workscheduler.client;

public class MyResponse {

    public final String body;
    public final int statusCode;
    public final String reasonPhrase;
    public final String path;

    public MyResponse(String body, int statusCode, String reasonPhrase, String path) {
        this.body = body;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.path = path;
    }
}
