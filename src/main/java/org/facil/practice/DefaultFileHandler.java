package org.facil.practice;

import java.io.*;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:51 AM
 */
public class DefaultFileHandler implements FileHandler {

    @Override
    public boolean saveFile(File file, String version) {
        if (null == file || null == version) {
            return false;
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(version);
            return true;
        } catch (IOException e) {
            System.out.printf("^error^ Could not write version [ %s ] into file [ %s]^r^%n",
                    version, file.getName());
            return false;
        }
    }

    @Override
    public String readFile(File file) {
        if (null == file) {
            return null;
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            System.out.printf("^error^ Cannot read file [ %s ]^r^%n", file.getName());
            return null;
        }
    }
}
