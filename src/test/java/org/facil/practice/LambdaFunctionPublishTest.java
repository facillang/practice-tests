package org.facil.practice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.PublishVersionRequest;
import com.amazonaws.services.lambda.model.PublishVersionResult;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:59 AM
 */
public class LambdaFunctionPublishTest {

    @Test
    public void publishVersion() throws NoSuchMethodException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);
        String functionName = "Test";
        String versionDescription = "Latest";
        String fileName = "aFile";

        AWSLambda awsLambda = mock(AWSLambda.class);
        File file = mock(File.class);
        FileHandler fileHandler = mock(FileHandler.class);
        PublishVersionResult publishVersionResult = mock(PublishVersionResult.class);
        LambdaFunctionPublish lambdaFunctionPublish
                = spy(new LambdaFunctionPublish(functionName, versionDescription, awsLambda, fileName, file, fileHandler));
        Method publishVersion = LambdaFunctionPublish.class.getDeclaredMethod("publishVersion");
        publishVersion.setAccessible(true);
        when(awsLambda.publishVersion(any(PublishVersionRequest.class)))
                .thenThrow(new AmazonClientException("Amazon Client Exception"))
                .thenReturn(publishVersionResult);

        try {
            assertNull(publishVersion.invoke(lambdaFunctionPublish));
            String errInfo = String.format("^error^ Lambda function [ %s ] does not exist^r^%n", functionName);
            assertEquals(errInfo, outputStream.toString());
            assertEquals(publishVersionResult, publishVersion.invoke(lambdaFunctionPublish));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            fail("should not throw Exceptions here");
        }
        verify(awsLambda, times(2)).publishVersion(any(PublishVersionRequest.class));

        System.setOut(System.out);
    }

    @Test
    public void publishNewVersion() {
        String functionName = "Test";
        String versionDescription = "Latest";
        String fileName = "aFile";
        String version = "0.1";
        AWSLambda awsLambda = mock(AWSLambda.class);
        File file = mock(File.class);
        FileHandler fileHandler = spy(new DefaultFileHandler());
        PublishVersionResult publishVersionResult = mock(PublishVersionResult.class);
        LambdaFunctionPublish lambdaFunctionPublish
                = spy(new LambdaFunctionPublish(functionName, versionDescription, awsLambda, fileName, file, fileHandler));

        when(awsLambda.publishVersion(any(PublishVersionRequest.class)))
                .thenReturn(publishVersionResult);
        when(publishVersionResult.getVersion()).thenReturn(version);
        doReturn(false).doReturn(true).when(lambdaFunctionPublish).createFileIfNotExists();
        doReturn(false).doReturn(true).when(lambdaFunctionPublish).doesLambdaFunctionExist();
        doReturn(true).when(fileHandler).saveFile(file, version);

        assertFalse(lambdaFunctionPublish.publishNewVersion());
        assertFalse(lambdaFunctionPublish.publishNewVersion());
        assertTrue(lambdaFunctionPublish.publishNewVersion());

        verify(lambdaFunctionPublish, times(3)).publishNewVersion();
        verify(lambdaFunctionPublish, times(3)).createFileIfNotExists();
        verify(lambdaFunctionPublish, times(2)).doesLambdaFunctionExist();
        verify(fileHandler).saveFile(file, version);
        verify(awsLambda).publishVersion(any(PublishVersionRequest.class));
        verify(publishVersionResult).getVersion();
    }

}
