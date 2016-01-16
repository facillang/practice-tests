package org.facil.practice;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.GetAliasRequest;
import com.amazonaws.services.lambda.model.GetAliasResult;
import com.amazonaws.services.lambda.model.GetFunctionRequest;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.facil.practice.ReflectionUtils.setNonAccessibleField;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:57 AM
 */
public class AbstractLambdaHandlerTest {

    @Test
    public void doesLambdaFunctionExist() throws NoSuchFieldException, IllegalAccessException {
        AWSLambda awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);
        AbstractLambdaHandler abstractLambdaHandler = mock(AbstractLambdaHandler.class, CALLS_REAL_METHODS);
        setNonAccessibleField(abstractLambdaHandler,"awsLambda", awsLambda);
        assertTrue(abstractLambdaHandler.doesLambdaFunctionExist());
        verify(awsLambda).getFunction((GetFunctionRequest) any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void doesLambdaFunctionExistWhenException() throws NoSuchFieldException, IllegalAccessException {
        AWSLambda awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);
        AbstractLambdaHandler abstractLambdaHandler = mock(AbstractLambdaHandler.class, CALLS_REAL_METHODS);
        setNonAccessibleField(abstractLambdaHandler,"awsLambda", awsLambda);
        setNonAccessibleField(abstractLambdaHandler,"functionName", "getSomething");
        when(awsLambda.getFunction((GetFunctionRequest) any())).thenThrow(AmazonClientException.class);
        assertFalse(abstractLambdaHandler.doesLambdaFunctionExist());
        verify(awsLambda).getFunction((GetFunctionRequest) any());
    }

    //Obviously not a good test case(because verifying a print message is error prone).
    // Couldn't able to test in any other way because it is an overloaded method(the method that has implementation already been tested)
    // with just a flag to print a message.
    @SuppressWarnings("unchecked")
    @Test
    public void doesLambdaFunctionExistWithArgument() throws IOException, NoSuchFieldException, IllegalAccessException {
        PrintStream stdOut = System.out;
        try(ByteArrayOutputStream content = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(content));
            AWSLambda awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);
            String functionName = "getSomething";
            when(awsLambda.getFunction((GetFunctionRequest) any())).thenThrow(AmazonClientException.class);
            AbstractLambdaHandler abstractLambdaHandler = mock(AbstractLambdaHandler.class, CALLS_REAL_METHODS);
            setNonAccessibleField(abstractLambdaHandler,"awsLambda", awsLambda);
            setNonAccessibleField(abstractLambdaHandler,"functionName", functionName);
            abstractLambdaHandler.doesLambdaFunctionExist(true);
            assertEquals(String.format("^error^ Lambda function [ %s ] does not exist^r^%n", functionName), content.toString());
        }finally {
            System.setOut(stdOut);
        }
    }


    @Test
    public void getAliasVersion() throws NoSuchFieldException, IllegalAccessException {
        AWSLambda awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);
        GetAliasResult getAliasResult = mock(GetAliasResult.class);
        String functionVersion = "10.9";
        String aliasType = "someAliasType";
        when(getAliasResult.getFunctionVersion()).thenReturn(functionVersion);
        when(awsLambda.getAlias((GetAliasRequest) any())).thenReturn(getAliasResult);
        AbstractLambdaHandler abstractLambdaHandler = mock(AbstractLambdaHandler.class, CALLS_REAL_METHODS);
        setNonAccessibleField(abstractLambdaHandler,"awsLambda", awsLambda);
        assertEquals(functionVersion, abstractLambdaHandler.getAliasVersion(aliasType));
        verify(awsLambda, times(1)).getAlias((GetAliasRequest) any());
        verify(getAliasResult, times(1)).getFunctionVersion();
    }

    @Test
    public void getAliasVersionNotSame() throws NoSuchFieldException, IllegalAccessException {
        AWSLambda awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);
        GetAliasResult getAliasResult = mock(GetAliasResult.class);
        String functionVersion = "10.9";
        String aliasType = "someAliasType";
        when(getAliasResult.getFunctionVersion()).thenReturn(functionVersion);
        when(awsLambda.getAlias((GetAliasRequest) any())).thenReturn(getAliasResult);
        AbstractLambdaHandler abstractLambdaHandler = mock(AbstractLambdaHandler.class, CALLS_REAL_METHODS);
        setNonAccessibleField(abstractLambdaHandler,"awsLambda", awsLambda);
        assertNotSame("10.8", abstractLambdaHandler.getAliasVersion(aliasType));
        verify(awsLambda, times(1)).getAlias((GetAliasRequest) any());
        verify(getAliasResult, times(1)).getFunctionVersion();
    }


    @SuppressWarnings("unchecked")
    @Test
    public void getAliasVersionWhenException() throws NoSuchFieldException, IllegalAccessException {
        AWSLambda awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);
        when(awsLambda.getAlias((GetAliasRequest) any())).thenThrow(AmazonClientException.class);
        GetAliasResult getAliasResult = mock(GetAliasResult.class);
        String aliasType = "someAliasType";
        String functionName = "getSomething";
        AbstractLambdaHandler abstractLambdaHandler = mock(AbstractLambdaHandler.class, CALLS_REAL_METHODS);
        setNonAccessibleField(abstractLambdaHandler,"awsLambda", awsLambda);
        setNonAccessibleField(abstractLambdaHandler,"functionName", functionName);
        assertNull(abstractLambdaHandler.getAliasVersion(aliasType));
        verify(awsLambda).getAlias((GetAliasRequest) any());
        verify(getAliasResult, never()).getFunctionVersion();
    }


    //Obviously not a good test case(because verifying a print message is error prone).
    // Looks like there is no other way to test because it is just a overloaded method
    // with just a flag to print a message.
    @SuppressWarnings("unchecked")
    @Test
    public void getAliasVersionWithTwoArguments() throws IOException, NoSuchFieldException, IllegalAccessException {
        PrintStream stdOut = System.out;
        try(ByteArrayOutputStream content = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(content));
            AWSLambda awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);
            String aliasType = "someAliasType";
            String functionName = "getSomething";
            AbstractLambdaHandler abstractLambdaHandler = mock(AbstractLambdaHandler.class, CALLS_REAL_METHODS);
            setNonAccessibleField(abstractLambdaHandler,"awsLambda", awsLambda);
            setNonAccessibleField(abstractLambdaHandler,"functionName", functionName);
            when(awsLambda.getAlias((GetAliasRequest) any())).thenThrow(AmazonClientException.class);
            abstractLambdaHandler.getAliasVersion(aliasType, true);
            assertEquals(String.format("^error^ Alias [ %s ] does not exist for Lambda function [ %s ]^r^%n", aliasType,
                                                    functionName), content.toString());
        }finally {
            System.setOut(stdOut);
        }
    }

}
