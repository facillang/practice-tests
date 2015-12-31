package org.facil.practice;

import com.amazonaws.services.lambda.AWSLambda;

import java.io.File;
import java.io.IOException;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:49 AM
 */
public abstract class AbstractLambdaFileHandler extends AbstractLambdaHandler {

    protected final String fileName;

    protected final File file;

    public AbstractLambdaFileHandler(String functionName, String fileName, File file, AWSLambda awsLambda) {
        super(functionName, awsLambda);
        this.fileName = fileName;
        this.file = file;
    }

    protected boolean createFileIfNotExists() {
        if (!file.exists()) {
            try {
                boolean isCreated = file.createNewFile();
                if (!isCreated) {
                    System.out.printf("^error^ Could not create file [ %s ]^r^%n", fileName);
                    return false;
                }
            } catch (IOException e) {
                System.out.printf("^error^ Could not create file [ %s ]^r^%n", fileName);
                return false;
            }
        }
        return true;
    }

}
