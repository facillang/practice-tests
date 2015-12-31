package org.facil.practice;

import java.io.File;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:46 AM
 */
public interface FileHandler {

    boolean saveFile(File file, String version);

    String readFile(File file);

}
