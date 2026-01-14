/*
 * Copyright (c) 2026. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package it.brunasti.java.diagrammer.spring.web;

import java.io.File;

public class WebPageMaping {
    public File file;
    public String fileName;
    public String mapping;
    public String method;
    public String function;

  @Override
  public String toString() {
    return "WebPageMaping{" +
            "file=" + file +
            ", fileName='" + fileName + '\'' +
            ", mapping='" + mapping + '\'' +
            ", method='" + method + '\'' +
            ", function='" + function + '\'' +
            '}';
  }
}
