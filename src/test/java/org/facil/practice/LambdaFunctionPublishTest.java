package org.facil.practice;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.AWSLambda;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.amazonaws.services.lambda.model.GetFunctionResult;
import com.amazonaws.services.lambda.model.PublishVersionResult;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:59 AM
 */
public class LambdaFunctionPublishTest {

    @Test
    public void publishVersion() {
        // TODO
        String functionName = "FunctionName";
        String versionDescription = "VersionDescription";
        String fileName = "FileName";
        AWSLambda awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);
        File file = mock(File.class);
        FileHandler fileHandler = mock(FileHandler.class);
        LambdaFunctionPublish lambdaFunctionPublish = new LambdaFunctionPublish(functionName, versionDescription, awsLambda, fileName, file, fileHandler);

        when(file.exists()).thenReturn(false);
        assertTrue(!lambdaFunctionPublish.publishNewVersion());

        when(file.exists()).thenReturn(true);
        doThrow(new AmazonClientException("UnitTest")).when(awsLambda).getFunction(any());
        assertTrue(!lambdaFunctionPublish.publishNewVersion());

        doReturn(new GetFunctionResult()).when(awsLambda).getFunction(any());
        when(awsLambda.publishVersion(any())).thenReturn(new PublishVersionResult());
        when(fileHandler.saveFile(any())).thenReturn(true);
        assertTrue(lambdaFunctionPublish.publishNewVersion());

        doReturn(new GetFunctionResult()).when(awsLambda).getFunction(any());
        when(awsLambda.publishVersion(any())).thenReturn(new PublishVersionResult());
        when(fileHandler.saveFile(any())).thenReturn(false);
        assertTrue(!lambdaFunctionPublish.publishNewVersion());

        doReturn(new GetFunctionResult()).when(awsLambda).getFunction(any());
        when(awsLambda.publishVersion(any())).thenReturn(null);
        when(fileHandler.saveFile(any())).thenReturn(true);
        assertTrue(!lambdaFunctionPublish.publishNewVersion());


    }

    @Test
    public void publishNewVersion() {
        // TODO
        String functionName = "FunctionName";
        String versionDescription = "VersionDescription";
        String fileName = "FileName";
        AWSLambda awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);
        File file = mock(File.class);
        FileHandler fileHandler = mock(FileHandler.class);
        LambdaFunctionPublish lambdaFunctionPublish = new LambdaFunctionPublish(functionName, versionDescription, awsLambda, fileName, file, fileHandler);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String errorMessage = "^error^ Lambda function [ FunctionName ] does not exist^r^";

        when(file.exists()).thenReturn(true);
        when(fileHandler.saveFile(any())).thenReturn(true);
        doReturn(new GetFunctionResult()).when(awsLambda).getFunction(any());

        when(awsLambda.publishVersion(any())).thenReturn(new PublishVersionResult());
        assertTrue(lambdaFunctionPublish.publishNewVersion());

        doThrow(new AmazonClientException("UnitTest")).when(awsLambda).publishVersion(any());
        assertTrue(!lambdaFunctionPublish.publishNewVersion());
        assertTrue(outContent.toString().indexOf(errorMessage) >= 0);
    }

}
