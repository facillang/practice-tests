package org.facil.practice;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.GetFunctionRequest;
import com.amazonaws.services.lambda.model.PublishVersionRequest;
import com.amazonaws.services.lambda.model.PublishVersionResult;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:59 AM
 */
public class LambdaFunctionPublishTest {

    @Test
    public void publishVersion() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException,
                                                                                        InstantiationException {
        AWSLambda awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);
        String functionName = "getSomething";
        String fileName = "test.txt";
        String versionDescription = "This version adds new functionality";
        FileHandler fileHandler = mock(DefaultFileHandler.class);
        File file = mock(File.class);
        LambdaFunctionPublish lambdaFunctionPublish = getLambdaFunctionPublish(awsLambda, functionName,
                                                    fileName, versionDescription, fileHandler, file);
        Method publishVersion = lambdaFunctionPublish.getClass().getDeclaredMethod("publishVersion",null);
        publishVersion.setAccessible(true);
        assertNotNull(publishVersion.invoke(lambdaFunctionPublish, null));
        verify(awsLambda).publishVersion((PublishVersionRequest) any());
    }

    @Test
    public void publishVersionCheckPublishVersionResultObject() throws NoSuchMethodException, InvocationTargetException,
                                                                    IllegalAccessException, InstantiationException {
        AWSLambda awsLambda = mock(AWSLambda.class);
        String functionName = "getSomething";
        String fileName = "test.txt";
        String versionDescription = "This version adds new functionality";
        FileHandler fileHandler = mock(DefaultFileHandler.class);
        File file = mock(File.class);
        PublishVersionResult publishVersionResult = mock(PublishVersionResult.class);
        when(awsLambda.publishVersion((PublishVersionRequest) any())).thenReturn(publishVersionResult);
        LambdaFunctionPublish lambdaFunctionPublish = getLambdaFunctionPublish(awsLambda, functionName, fileName,
                                                                                versionDescription, fileHandler, file);
        Method publishVersion = lambdaFunctionPublish.getClass().getDeclaredMethod("publishVersion",null);
        publishVersion.setAccessible(true);
        assertEquals(publishVersionResult, publishVersion.invoke(lambdaFunctionPublish, null));
        verify(awsLambda).publishVersion((PublishVersionRequest) any());
    }

    @Test
    public void PublishVersionCheckVersion() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException,
                                                            InstantiationException {
        AWSLambda awsLambda = mock(AWSLambda.class);
        String functionName = "getSomething";
        String fileName = "test.txt";
        String versionDescription = "This version adds new functionality";
        String version = "Version 9.08";
        FileHandler fileHandler = mock(DefaultFileHandler.class);
        File file = mock(File.class);
        PublishVersionResult publishVersionResult = mock(PublishVersionResult.class);
        when(publishVersionResult.getVersion()).thenReturn(version);
        when(awsLambda.publishVersion((PublishVersionRequest) any())).thenReturn(publishVersionResult);
        LambdaFunctionPublish lambdaFunctionPublish = getLambdaFunctionPublish(awsLambda, functionName, fileName,
                                                                                    versionDescription, fileHandler, file);
        Method publishVersion = lambdaFunctionPublish.getClass().getDeclaredMethod("publishVersion",null);
        publishVersion.setAccessible(true);
        PublishVersionResult publishVersionResultLocal = (PublishVersionResult) publishVersion.invoke(lambdaFunctionPublish, null);
        assertEquals(version, publishVersionResultLocal.getVersion());
        verify(awsLambda).publishVersion((PublishVersionRequest) any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void publishVersionWhenExceptionReturnNull() throws NoSuchMethodException, InvocationTargetException,
                                                                            IllegalAccessException, InstantiationException {
        AWSLambda awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);
        String functionName = "getSomething";
        String fileName = "test.txt";
        String versionDescription = "This version adds new functionality";
        FileHandler fileHandler = mock(DefaultFileHandler.class);
        File file = mock(File.class);
        LambdaFunctionPublish lambdaFunctionPublish = getLambdaFunctionPublish(awsLambda, functionName, fileName,
                                                                                versionDescription, fileHandler, file);
        when(awsLambda.publishVersion((PublishVersionRequest) any())).thenThrow(AmazonClientException.class);
        Method publishVersion = lambdaFunctionPublish.getClass().getDeclaredMethod("publishVersion",null);
        publishVersion.setAccessible(true);
        assertNull(publishVersion.invoke(lambdaFunctionPublish, null));
        verify(awsLambda).publishVersion((PublishVersionRequest) any());
    }



    @Test
    public void publishNewVersion() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
                                                                                                InstantiationException {
        AWSLambda awsLambda = mock(AWSLambda.class);// RETURNS_DEEP_STUBS can also be used instead of mocking PublishVersionResult
                                                        //then returning it when awsLambda.publishVersion is called.
        String functionName = "getSomething";
        String fileName = "test.txt";
        String version = "9.9";
        String versionDescription = "This version adds new functionality";
        FileHandler fileHandler = mock(DefaultFileHandler.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if(invocation.getMethod().getName().equals("saveFile")){
                    return Boolean.TRUE;
                }
                return invocation.callRealMethod();            }
        });
        File file = mock(File.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if(invocation.getMethod().getName().equals("exists")){
                    return Boolean.TRUE;
                }
                return invocation.callRealMethod();
            }
        });

        LambdaFunctionPublish lambdaFunctionPublish = getLambdaFunctionPublish(awsLambda, functionName, fileName,
                                                                        versionDescription, fileHandler, file);
        PublishVersionResult publishVersionResult = mock(PublishVersionResult.class);
        when(publishVersionResult.getVersion()).thenReturn(version);
        when(awsLambda.publishVersion((PublishVersionRequest) any())).thenReturn(publishVersionResult);
        assertTrue(lambdaFunctionPublish.publishNewVersion());
        verify(file).exists();
        verify(fileHandler).saveFile(file, version);
        verify(awsLambda).publishVersion((PublishVersionRequest) any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void publishNewVersionFalseWhenPublishResultIsNull()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        AWSLambda awsLambda = mock(AWSLambda.class);
        String functionName = "getSomething";
        String fileName = "test.txt";
        String versionDescription = "This version adds new functionality";
        FileHandler fileHandler = mock(DefaultFileHandler.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if(invocation.getMethod().getName().equals("saveFile")){
                    return Boolean.TRUE;
                }
                return invocation.callRealMethod();
            }
        });
        File file = mock(File.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if(invocation.getMethod().getName().equals("exists")){
                    return Boolean.TRUE;
                }
                return invocation.callRealMethod();
            }
        });
        when(awsLambda.publishVersion((PublishVersionRequest) any())).thenReturn(null);
        LambdaFunctionPublish lambdaFunctionPublish = getLambdaFunctionPublish(awsLambda, functionName, fileName,
                                                                                versionDescription, fileHandler, file);
        assertFalse(lambdaFunctionPublish.publishNewVersion());
        verify(file).exists();
        // fileHandler.saveFile should not be invoked because implementation uses short circuit &&
        //so return statement should return false immediately when it sees  result != null
        verify(fileHandler, never()).saveFile((File) any() , (String) any());
        verify(awsLambda).publishVersion((PublishVersionRequest) any());
    }


    @Test
    public void publishNewVersionFalseWhenCreateFileNotExistsFalse() throws NoSuchFieldException, IllegalAccessException,
                                NoSuchMethodException, InvocationTargetException, InstantiationException, IOException {
        AWSLambda awsLambda = mock(AWSLambda.class);
        String functionName = "getSomething";
        String fileName = "test.txt";
        String versionDescription = "This version adds new functionality";
        FileHandler fileHandler = mock(DefaultFileHandler.class);
        File file = mock(File.class, new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if(invocation.getMethod().getName().equals("createNewFile")){
                    return Boolean.FALSE;
                }
                if(invocation.getMethod().getName().equals("exists")){
                    return Boolean.FALSE;
                }
                return invocation.callRealMethod();
            }
        });
        PublishVersionResult publishVersionResult = mock(PublishVersionResult.class);
        when(awsLambda.publishVersion((PublishVersionRequest) any())).thenReturn(publishVersionResult);

        LambdaFunctionPublish lambdaFunctionPublish = getLambdaFunctionPublish(awsLambda, functionName, fileName, versionDescription, fileHandler, file);
        assertFalse(lambdaFunctionPublish.publishNewVersion());
        verify(file).exists();
        verify(file).createNewFile();
        verify(fileHandler, never()).saveFile((File) any(), (String) any());
        verify(awsLambda, never()).publishVersion((PublishVersionRequest) any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void publishNewVersionFalseWhenDoesLambdaFunctionExistsFalse()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        AWSLambda awsLambda = mock(AWSLambda.class);
        String functionName = "getSomething";
        String fileName = "test.txt";
        String versionDescription = "This version adds new functionality";
        FileHandler fileHandler = mock(DefaultFileHandler.class);
        File file = mock(File.class, new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if(invocation.getMethod().getName().equals("exists")){
                    return Boolean.TRUE;
                }
                return invocation.callRealMethod();
            }
        });
        LambdaFunctionPublish lambdaFunctionPublish = spy(new LambdaFunctionPublish(functionName, versionDescription,
                                awsLambda, fileName, file, fileHandler));
        when(awsLambda.getFunction((GetFunctionRequest) any())).thenThrow(AmazonClientException.class);
        assertFalse(lambdaFunctionPublish.publishNewVersion());
        verify(file).exists();
        verify(file, never()).createNewFile();
        verify(awsLambda).getFunction((GetFunctionRequest) any());
        verify(fileHandler, never()).saveFile((File) any(), (String) any());
    }


    private LambdaFunctionPublish getLambdaFunctionPublish(AWSLambda awsLambda, String functionName, String fileName,
                                                           String versionDescription, FileHandler fileHandler, File file)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor constructor = LambdaFunctionPublish.class.getDeclaredConstructor(String.class, String.class, AWSLambda.class,
                String.class, File.class, FileHandler.class);
        constructor.setAccessible(true);
        return (LambdaFunctionPublish) constructor.newInstance(functionName, versionDescription, awsLambda, fileName, file, fileHandler);
    }
}
