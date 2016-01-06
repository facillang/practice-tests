package org.facil.practice;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.GetFunctionRequest;
import com.amazonaws.services.lambda.model.PublishVersionRequest;
import com.amazonaws.services.lambda.model.PublishVersionResult;
import org.junit.After;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static junit.framework.Assert.assertNull;
import static org.facil.practice.ReflectionUtils.setNonAccesibleField;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:59 AM
 */
public class LambdaFunctionPublishTest {

    private AWSLambda awsLambda;
    private String versionDescription = "This version adds new functionality";
    private FileHandler fileHandler;
    private String functionName = "getSomething";
    private String fileName = "test.txt";
    private String version = "Version 9.08";
    private File file;
    private PublishVersionRequest publishVersionRequest = new PublishVersionRequest();//mock(PublishVersionRequest.class, CALLS_REAL_METHODS);
    private PublishVersionResult publishVersionResult = new PublishVersionResult();//mock(PublishVersionResult.class, CALLS_REAL_METHODS);
    private LambdaFunctionPublish lambdaFunctionPublish;

    public LambdaFunctionPublishTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        publishVersionResult.setVersion(version);
        fileHandler = new DefaultFileHandler();
        file = new File(fileName);
        awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);

        Constructor constructor = LambdaFunctionPublish.class.getDeclaredConstructor(String.class, String.class, AWSLambda.class,
                String.class, File.class, FileHandler.class);
        constructor.setAccessible(true);
        lambdaFunctionPublish = (LambdaFunctionPublish) constructor.newInstance(functionName, versionDescription, awsLambda, fileName, file, fileHandler);
        when(awsLambda.publishVersion(publishVersionRequest.withFunctionName(functionName).withDescription(versionDescription))).thenReturn(publishVersionResult);
    }


    @After
    public void deleteFile(){
        if(file.exists()){
            file.delete();
        }
    }

    @Test
    public void publishVersion() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = lambdaFunctionPublish.getClass().getDeclaredMethod("publishVersion",null);
        method.setAccessible(true);
        assertNotNull(method.invoke(lambdaFunctionPublish, null));
        verify(awsLambda).publishVersion((PublishVersionRequest) any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void publishVersionException() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        when(awsLambda.publishVersion((PublishVersionRequest) any())).thenThrow(AmazonClientException.class);
        Method method = lambdaFunctionPublish.getClass().getDeclaredMethod("publishVersion",null);
        method.setAccessible(true);
        assertNull(method.invoke(lambdaFunctionPublish, null));
        verify(awsLambda).publishVersion((PublishVersionRequest) any());
    }

    @Test
    public void verifyPublishVersionResultObject() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = lambdaFunctionPublish.getClass().getDeclaredMethod("publishVersion",null);
        method.setAccessible(true);
        PublishVersionResult publishVersionResultLocal = (PublishVersionResult) method.invoke(lambdaFunctionPublish, null);
        assertEquals(version, publishVersionResultLocal.getVersion());
    }

    @Test
    public void publishNewVersion() {
        assertTrue(lambdaFunctionPublish.publishNewVersion());
    }

    @Test
    public void publishNewVersionReadSavedFile() throws IOException {
        lambdaFunctionPublish.publishNewVersion();
        assertTrue(file.exists());
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){
            assertEquals(version, bufferedReader.readLine());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void publishNewVersionException() {
        when(awsLambda.publishVersion((PublishVersionRequest) any())).thenThrow(AmazonClientException.class);
        assertFalse(lambdaFunctionPublish.publishNewVersion());
    }

    @Test
    public void publishNewVersionVerifyMethodCalls(){
        lambdaFunctionPublish.publishNewVersion();
        verify(awsLambda).publishVersion((PublishVersionRequest) any());
    }

    @Test
    public void publishNewVersionWhenCreateFileNotsExistsFalse() throws NoSuchFieldException, IllegalAccessException {
        file = mock(File.class, new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if(invocation.getMethod().getName().equals("createNewFile")){
                    return Boolean.FALSE;
                }
                if(invocation.getMethod().getName().equals("exists")){
                    return Boolean.FALSE;
                }
                return null;
            }
        });
        setNonAccesibleField(lambdaFunctionPublish.getClass(), lambdaFunctionPublish, "file", file);
        assertFalse(lambdaFunctionPublish.publishNewVersion());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void publishNewVersionWhenDoesLambdaFunctionExistsFalse(){
        when(awsLambda.getFunction((GetFunctionRequest) any())).thenThrow(AmazonClientException.class);
        assertFalse(lambdaFunctionPublish.publishNewVersion());
    }

    @Test
    public void publishNewVersionPublishVersionResultNull(){
        when(awsLambda.publishVersion(publishVersionRequest.withFunctionName(functionName).withDescription(versionDescription))).thenReturn(null);
        assertFalse(lambdaFunctionPublish.publishNewVersion());
    }

}
