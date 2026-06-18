package it.brunasti.java.diagrammer.spring.api;

public class RestControllerInfo {
    public String packageName;
    public String className;
    public String baseUrl;

    @Override
    public String toString() {
        return String.format("%-40s  %s.%s",
                baseUrl.isEmpty() ? "(no base mapping)" : baseUrl,
                packageName, className);
    }
}
