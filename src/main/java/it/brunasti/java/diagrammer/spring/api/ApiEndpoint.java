package it.brunasti.java.diagrammer.spring.api;

public class ApiEndpoint {
    public String packageName;
    public String className;
    public String functionName;
    public String httpMethod;
    public String url;

    @Override
    public String toString() {
        return String.format("%-10s  %-60s  %s.%s::%s",
                httpMethod, url, packageName, className, functionName);
    }
}
