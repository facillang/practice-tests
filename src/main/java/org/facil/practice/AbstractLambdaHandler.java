package org.facil.practice;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.GetAliasRequest;
import com.amazonaws.services.lambda.model.GetAliasResult;
import com.amazonaws.services.lambda.model.GetFunctionRequest;

/**
 * User: blangel
 * Date: 12/31/15
 * Time: 11:47 AM
 */
public abstract class AbstractLambdaHandler {

    protected static final String LATEST_VERSION = "$LATEST";

    protected final String functionName;

    protected final AWSLambda awsLambda;

    protected AbstractLambdaHandler(String functionName, AWSLambda awsLambda) {
        this.functionName = functionName;
        this.awsLambda = awsLambda;
    }

    protected boolean doesLambdaFunctionExist() {
        return doesLambdaFunctionExist(true);
    }

    protected boolean doesLambdaFunctionExist(boolean printMessage) {
        try {
            awsLambda.getFunction(new GetFunctionRequest().withFunctionName(functionName));
        } catch (AmazonClientException e) {
            if (printMessage) {
                System.out.printf("^error^ Lambda function [ %s ] does not exist^r^%n", this.functionName);
            }
            return false;
        }
        return true;
    }

    protected String getAliasVersion(String aliasType) {
        return getAliasVersion(aliasType, true);
    }

    protected String getAliasVersion(String aliasType, boolean printMessage) {
        try {
            GetAliasResult getAliasResult = awsLambda.getAlias(new GetAliasRequest()
                    .withFunctionName(functionName)
                    .withName(aliasType));
            return getAliasResult.getFunctionVersion();
        } catch (AmazonClientException e) {
            if (printMessage) {
                System.out.printf("^error^ Alias [ %s ] does not exist for Lambda function [ %s ]^r^%n", aliasType, functionName);
            }
            return null;
        }
    }

}
