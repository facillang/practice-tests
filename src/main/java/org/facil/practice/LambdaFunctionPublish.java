package org.facil.practice;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.PublishVersionRequest;
import com.amazonaws.services.lambda.model.PublishVersionResult;

import java.io.File;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:50 AM
 */
public class LambdaFunctionPublish extends AbstractLambdaFileHandler {

    private final String versionDescription;

    private final FileHandler fileHandler;

    public LambdaFunctionPublish(String functionName, String versionDescription, String fileName) {
        this(functionName, versionDescription,
                new AWSLambdaClient(new SystemPropertiesCredentialsProvider()),
                fileName, new File(fileName), new DefaultFileHandler());
    }

    protected LambdaFunctionPublish(String functionName, String versionDescription,
                                    AWSLambda awsLambda, String fileName, File file, FileHandler fileHandler) {
        super(functionName, fileName, file, awsLambda);
        this.versionDescription = versionDescription;
        this.fileHandler = fileHandler;
    }

    public boolean publishNewVersion() {
        if (!createFileIfNotExists()) {
            return false;
        }
        if (!doesLambdaFunctionExist()) {
            return false;
        }
        PublishVersionResult result = publishVersion();
        return ((result != null) && fileHandler.saveFile(file, result.getVersion()));
    }

    private PublishVersionResult publishVersion() {
        try {
            return awsLambda.publishVersion(new PublishVersionRequest()
                    .withFunctionName(functionName)
                    .withDescription(versionDescription));
        } catch (AmazonClientException e) {
            System.out.printf("^error^ Lambda function [ %s ] does not exist^r^%n",
                    this.functionName);
        }
        return null;
    }

}
