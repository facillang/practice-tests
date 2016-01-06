package org.facil.practice;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.GetAliasRequest;
import com.amazonaws.services.lambda.model.GetAliasResult;
import com.amazonaws.services.lambda.model.GetFunctionRequest;
import com.amazonaws.services.lambda.model.GetFunctionResult;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.facil.practice.ReflectionUtils.setNonAccesibleField;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:57 AM
 */
public class AbstractLambdaHandlerTest {

    private AbstractLambdaHandler abstractLambdaHandler;
    private AWSLambda awsLambda;
    private String functionName;
    private GetFunctionRequest getFunctionRequest;
    private GetFunctionResult getFunctionResult;
    private String aliasType;
    private String functionVersion;
    private GetAliasRequest getAliasRequest;
    private GetAliasResult getAliasResult;

    public AbstractLambdaHandlerTest() throws NoSuchFieldException, IllegalAccessException {
        abstractLambdaHandler = mock(AbstractLambdaHandler.class, CALLS_REAL_METHODS);
        awsLambda = mock(AWSLambda.class, RETURNS_DEEP_STUBS);
        functionName = "getSomething";
        setNonAccesibleField(abstractLambdaHandler.getClass(),abstractLambdaHandler,"awsLambda", awsLambda);
        setNonAccesibleField(abstractLambdaHandler.getClass(),abstractLambdaHandler,"functionName", functionName);
        getFunctionRequest = mock(GetFunctionRequest.class, RETURNS_DEEP_STUBS);
        getFunctionResult = mock(GetFunctionResult.class);
        when(getFunctionRequest.withFunctionName(functionName)).thenReturn(getFunctionRequest);
        when(awsLambda.getFunction(getFunctionRequest.withFunctionName(functionName))).thenReturn(getFunctionResult);

        aliasType = "someAlias";
        functionVersion = "10.1";
        getAliasRequest = new GetAliasRequest();
        getAliasResult = new GetAliasResult();
        getAliasResult.setFunctionVersion(functionVersion);
        when(awsLambda.getAlias(getAliasRequest.withFunctionName(functionName).withName(aliasType))).thenReturn(getAliasResult);
    }

    @Test
    public void doesLambdaFunctionExist() {
        assertTrue(abstractLambdaHandler.doesLambdaFunctionExist());
    }


    @Test
    public void doesLambdaFunctionExistVerifyMethodCalls(){
        abstractLambdaHandler.doesLambdaFunctionExist();
        verify(getFunctionRequest,times(1)).withFunctionName(functionName);
        verify(awsLambda).getFunction((GetFunctionRequest) any());
    }


    @SuppressWarnings("unchecked")
    @Test
    public void doesLambdaFunctionExistException(){
        when(awsLambda.getFunction((GetFunctionRequest) any())).thenThrow(AmazonClientException.class);
        assertFalse(abstractLambdaHandler.doesLambdaFunctionExist());
        verify(awsLambda).getFunction((GetFunctionRequest) any());
    }

    //Obviously not a good test case(because verifying a print message is error prone).
    // Couldn't able to test in any other way because it is an overloaded method(for which implementation has been tested)
    // with just a flag to print a message.
    @SuppressWarnings("unchecked")
    @Test
    public void doesLambdaFunctionExistWithArgument() throws IOException {
        PrintStream stdOut = System.out;
        try(ByteArrayOutputStream content = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(content));
            when(awsLambda.getFunction((GetFunctionRequest) any())).thenThrow(AmazonClientException.class);
            abstractLambdaHandler.doesLambdaFunctionExist(true);
            assertEquals(String.format("^error^ Lambda function [ %s ] does not exist^r^%n", functionName), content.toString());
        }finally {
            System.setOut(stdOut);
        }
    }


    @Test
    public void getAliasVersion() {
        assertEquals(functionVersion, abstractLambdaHandler.getAliasVersion(aliasType));
    }

    @Test
    public void getAliasVersionNotSame() {
        assertNotSame("9", abstractLambdaHandler.getAliasVersion(aliasType));
    }

    @Test
    public void getAliasVersionVerifyMethodCalls(){
        abstractLambdaHandler.getAliasVersion(aliasType);
        verify(awsLambda).getAlias((GetAliasRequest) any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getAliasVersionVerifyException(){
        when(awsLambda.getAlias((GetAliasRequest) any())).thenThrow(AmazonClientException.class);
        assertNull(abstractLambdaHandler.getAliasVersion(aliasType));
        verify(awsLambda).getAlias((GetAliasRequest) any());
    }


    //Obviously not a good test case(because verifying a print message is error prone).
    // Looks like there is no other way to test because it is just a overloaded method
    // with just a flag to print a message.
    @SuppressWarnings("unchecked")
    @Test
    public void getAliasVersionWithTwoArguments() throws IOException{
        PrintStream stdOut = System.out;
        try(ByteArrayOutputStream content = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(content));
            when(awsLambda.getAlias((GetAliasRequest) any())).thenThrow(AmazonClientException.class);
            abstractLambdaHandler.getAliasVersion(aliasType, true);
            assertEquals(String.format("^error^ Alias [ %s ] does not exist for Lambda function [ %s ]^r^%n", aliasType, functionName), content.toString());
        }finally {
            System.setOut(stdOut);
        }
    }

}
