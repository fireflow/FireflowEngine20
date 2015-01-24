package org.fireflow.demo.misc;

import javax.servlet.ServletContext;

public class UploadUtils {
	private ServletContext sc;

    public String getPath(String path) {
        return sc.getRealPath(path);
    }
}
