package org.facil.practice;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.*;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.GetAliasResult;
import org.junit.Before;
import org.junit.Test;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:57 AM
 */
public class AbstractLambdaHandlerTest {

    //Use an inheritance class to test the abstract class
    class AbstractClassImplement extends AbstractLambdaHandler{
        public AbstractClassImplement(String functionName, AWSLambda awsLambda){
            super(functionName, awsLambda);
        }
    }

    AbstractLambdaHandler mockAbstractLambdaHandler;
    String functionName;
    AWSLambda awsLambda;
    AbstractClassImplement abstractClassImplement;

    @Before
    public void initialize(){
        functionName = "FunctionName";
        awsLambda = mock(AWSLambda.class);
        mockAbstractLambdaHandler = mock(AbstractLambdaHandler.class);
        abstractClassImplement = new AbstractClassImplement(functionName, awsLambda);
    }

    @Test
    public void doesLambdaFunctionExist() {
        // TODO
        when(mockAbstractLambdaHandler.doesLambdaFunctionExist()).thenCallRealMethod();
        doReturn(true).when(mockAbstractLambdaHandler).doesLambdaFunctionExist(true);
        assertEquals(mockAbstractLambdaHandler.doesLambdaFunctionExist(), true);
    }

    @Test
    public void doesLambdaFunctionExistWithArgument() {
        // TODO
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String errorMessage = "^error^ Lambda function [ FunctionName ] does not exist^r^";

        when(awsLambda.getFunction(any())).thenReturn(any());
        assertEquals(abstractClassImplement.doesLambdaFunctionExist(true), true);

        doThrow(new AmazonClientException("UnitTest")).when(awsLambda).getFunction(any());
        assertEquals(abstractClassImplement.doesLambdaFunctionExist(false), false);


        assertEquals(abstractClassImplement.doesLambdaFunctionExist(true), false);
        assertTrue(outContent.toString().indexOf(errorMessage) >= 0);
    }

    @Test
    public void getAliasVersion() {
        // TODO
        String aliasType = "AliasType";
        when(mockAbstractLambdaHandler.getAliasVersion(aliasType)).thenCallRealMethod();
        doReturn(aliasType).when(mockAbstractLambdaHandler).getAliasVersion(aliasType, true);
        assertEquals(mockAbstractLambdaHandler.getAliasVersion(aliasType), aliasType);
    }

    @Test
    public void getAliasVersionWithTwoArguments() {
        // TODO
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String functionVersion = "FunctionVersion";
        String aliasType = "AliasType";
        String errorMessage = "^error^ Alias [ AliasType ] does not exist for Lambda function [ FunctionName ]^r^";

        GetAliasResult getAliasResult = mock(GetAliasResult.class);
        when(awsLambda.getAlias(any())).thenReturn(getAliasResult);
        when(getAliasResult.getFunctionVersion()).thenReturn(functionVersion);
        assertEquals(abstractClassImplement.getAliasVersion(aliasType, true), functionVersion);

        doThrow(new AmazonClientException("Exception")).when(awsLambda).getAlias(any());
        assertEquals(abstractClassImplement.getAliasVersion(aliasType, false), null);
        assertEquals(abstractClassImplement.getAliasVersion(aliasType, true), null);
        assertTrue(outContent.toString().indexOf(errorMessage) >= 0);
    }

}
