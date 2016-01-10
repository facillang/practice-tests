package org.facil.practice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.GetAliasRequest;
import com.amazonaws.services.lambda.model.GetAliasResult;
import com.amazonaws.services.lambda.model.GetFunctionRequest;
import com.amazonaws.services.lambda.model.GetFunctionResult;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;


/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:57 AM
 */
public class AbstractLambdaHandlerTest {

    @Test
    public void doesLambdaFunctionExist() {
        AbstractLambdaHandler abstractLambdaHandler = mock(AbstractLambdaFileHandler.class);
        when(abstractLambdaHandler.doesLambdaFunctionExist()).thenCallRealMethod();
        when(abstractLambdaHandler.doesLambdaFunctionExist(true)).thenReturn(true);
        assertTrue(abstractLambdaHandler.doesLambdaFunctionExist());
        verify(abstractLambdaHandler).doesLambdaFunctionExist(true);
        verify(abstractLambdaHandler).doesLambdaFunctionExist();
    }

    @Test
    public void doesLambdaFunctionExistWithArgument() {
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);

        String functionName = "Lambda";
        AWSLambda awsLambda = mock(AWSLambda.class);
        GetFunctionResult getFunctionResult = mock(GetFunctionResult.class);
        AbstractLambdaHandler abstractLambdaHandler = spy(new AbstractLambdaHandler(functionName, awsLambda) {});

        when(awsLambda.getFunction(any(GetFunctionRequest.class)))
                .thenThrow(new AmazonClientException("Amazon client Exception"))
                .thenReturn(getFunctionResult);

        assertFalse(abstractLambdaHandler.doesLambdaFunctionExist(true));
        String errInfo = String.format("^error^ Lambda function [ %s ] does not exist^r^%n", functionName);
        assertEquals(errInfo, os.toString());
        assertTrue(abstractLambdaHandler.doesLambdaFunctionExist(true));

        verify(abstractLambdaHandler, times(2)).doesLambdaFunctionExist(true);
        verify(awsLambda, times(2)).getFunction(any(GetFunctionRequest.class));

        System.setOut(System.out);
    }

    @Test
    public void getAliasVersion() {
        String aliasType = "aliasType";
        String aliasVersion = "0.1";
        AbstractLambdaHandler abstractLambdaHandler = mock(AbstractLambdaFileHandler.class);
        when(abstractLambdaHandler.getAliasVersion(aliasType)).thenCallRealMethod();
        when(abstractLambdaHandler.getAliasVersion(aliasType, true)).thenReturn(aliasVersion);
        assertEquals(aliasVersion, abstractLambdaHandler.getAliasVersion(aliasType));
        verify(abstractLambdaHandler).getAliasVersion(aliasType, true);
        verify(abstractLambdaHandler).getAliasVersion(aliasType);
    }

    @Test
    public void getAliasVersionWithTwoArguments() {
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);

        String functionName = "Lambda";
        String aliasType = "aliasType";
        String aliasVersion = "0.1";
        AWSLambda awsLambda = mock(AWSLambda.class);
        GetAliasResult getAliasResult = mock(GetAliasResult.class);
        AbstractLambdaHandler abstractLambdaHandler = spy(new AbstractLambdaHandler(functionName, awsLambda) {});

        when(awsLambda.getAlias(any(GetAliasRequest.class)))
                .thenThrow(new AmazonClientException("Amazon client Exception"))
                .thenReturn(getAliasResult);
        when(getAliasResult.getFunctionVersion()).thenReturn(aliasVersion);

        assertNull(abstractLambdaHandler.getAliasVersion(aliasType, true));
        String printOut = String.format("^error^ Alias [ %s ] does not exist for Lambda function [ %s ]^r^%n", aliasType, functionName);
        assertEquals(printOut, os.toString());
        assertEquals(aliasVersion, abstractLambdaHandler.getAliasVersion(aliasType, true));
        verify(abstractLambdaHandler, times(2)).getAliasVersion(aliasType, true);
        verify(awsLambda, times(2)).getAlias(any(GetAliasRequest.class));
        verify(getAliasResult).getFunctionVersion();

        System.setOut(System.out);
    }

}
