package fidelis;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONObject;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;


public class SimpleBenchmarkAWSML implements RequestHandler<RequestClass, String> {

    public String handleRequest(RequestClass request, Context context) {
        String bucketName = "ResearchSomethingTestMLBucket";
        String key = "ml.arff";
        String access_key_id = "AB64NNCYUUN3ECCZL522";
        String secret_access_key = "030rt8k1ziRXwERA34cTfPVfAOKgRqlb763f5Bec";
        S3Object s3object = null;
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AB64NNCYUUN3ECCZL522", "030rt8k1ziRXwERA34cTfPVfAOKgRqlb763f5Bec");
        AmazonS3Client s3Client = new AmazonS3Client(awsCreds);
        try {
            System.out.println("Doing the needfull ->");
            s3object = s3Client.getObject(new GetObjectRequest("ResearchSomethingTestMLBucket", "ml.arff"));

            System.out.println("Content-Type: " + s3object
                    .getObjectMetadata().getContentType());
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException,Look in trails for exception.");


            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException.  Are you sure you are connected? ");


            System.out.println("Generic Error Response -> " + ace.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            List<Object> test = request.headers;
            LinkedHashMap row = (LinkedHashMap) test.get(0);
            JSONObject jsonString = new JSONObject(row);


            switch (jsonString.getString("Algorithm")) {
                case "Naive Bayes Classifier":
                    NaiveBayes nb = naiveBayesClassifier(jsonString, s3object);
                    try {
                        File myFile = new File("//tmp//rs_ml_aws_NBC");
                        FileOutputStream f = new FileOutputStream(myFile);
                        ObjectOutputStream o = new ObjectOutputStream(f);
                        o.writeObject(nb);
                        o.close();
                        f.close();

                        PutObjectRequest putRequest = new PutObjectRequest("ResearchSomethingTestMLBucket", "rs_ml_aws_NBC", myFile);
                        PutObjectResult response = s3Client.putObject(putRequest);
                        System.out.println("Encrypted droplets? -> " + response.getSSEAlgorithm());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                case "Support Vector Machine":
                    LibSVM svm = supportVectorMachine(jsonString, s3object);
                    try {
                        File myFile = new File("//tmp//rs_ml_aws_SVM");
                        FileOutputStream f = new FileOutputStream(myFile);
                        ObjectOutputStream o = new ObjectOutputStream(f);
                        o.writeObject(svm);
                        o.close();
                        f.close();

                        PutObjectRequest putRequest = new PutObjectRequest("ResearchSomethingTestMLBucket", "rs_ml_aws_SVM", myFile);
                        PutObjectResult response = s3Client.putObject(putRequest);
                        System.out.println("Encrypted droplets? -> " + response.getSSEAlgorithm());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                case "MultiLayer Perceptron":
                    MultilayerPerceptron mlp = multiLayerPerceptron(jsonString, s3object);
                    try {
                        File myFile = new File("//tmp//rs_ml_aws_MP");
                        FileOutputStream f = new FileOutputStream(myFile);
                        ObjectOutputStream o = new ObjectOutputStream(f);
                        o.writeObject(mlp);
                        o.close();
                        f.close();

                        PutObjectRequest putRequest = new PutObjectRequest("ResearchSomethingTestMLBucket", "rs_ml_aws_MP", myFile);
                        PutObjectResult response = s3Client.putObject(putRequest);
                        System.out.println("Encryopted droplet? -> " + response.getSSEAlgorithm());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                case "Logistic Regression":
                    Logistic logr = logisticRegression(jsonString, s3object);
                    try {
                        File myFile = new File("//tmp//rs_ml_aws_LR");
                        FileOutputStream f = new FileOutputStream(myFile);
                        ObjectOutputStream o = new ObjectOutputStream(f);
                        o.writeObject(logr);
                        o.close();
                        f.close();

                        PutObjectRequest putRequest = new PutObjectRequest("ResearchSomethingTestMLBucket", "rs_ml_aws_LR", myFile);
                        PutObjectResult response = s3Client.putObject(putRequest);
                        System.out.println("Encrypted Droplet? ->  " + response.getSSEAlgorithm());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                default:
                    return "Danger Will Robinson.  Error! ";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Needfull accomplished";
    }

    public LibSVM supportVectorMachine(JSONObject jsonString, S3Object s3object) {
        LibSVM svm = new LibSVM();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
            Instances dataset = new Instances(br);
            dataset.setClassIndex(dataset.numAttributes() - 1);
            br.close();
            int trainSize = (int) Math.round(dataset.numInstances() * (Double.parseDouble(jsonString.getString("percent")) / 100.0D));
            Instances training = new Instances(dataset, 0, trainSize);
            br.close();

            String options = "-S " + jsonString.getString("SVM") + " -K " + jsonString.getString("Kernel") + " -D " + jsonString.getString("Degree") + " -G " + jsonString.getString("Gamma") + " -M " + jsonString.getString("Size") + " -E " + jsonString.getString("Tolerance") + " -P " + jsonString.getString("Epsilon") + " -W -1";
            String[] optionsArray = options.split(" ");
            svm.setOptions(optionsArray);
            svm.buildClassifier(training);
            return svm;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return svm;
    }

    public NaiveBayes naiveBayesClassifier(JSONObject jsonString, S3Object s3object) {
        NaiveBayes nb = new NaiveBayes();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
            Instances dataset = new Instances(br);
            dataset.setClassIndex(dataset.numAttributes() - 1);
            br.close();
            int trainSize = (int) Math.round(dataset.numInstances() * (Double.parseDouble(jsonString.getString("percent")) / 100.0D));
            Instances training = new Instances(dataset, 0, trainSize);
            br.close();
            nb.buildClassifier(training);
            return nb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nb;
    }

    public Logistic logisticRegression(JSONObject jsonString, S3Object s3object) {
        Logistic lr = new Logistic();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
            Instances dataset = new Instances(br);
            dataset.setClassIndex(dataset.numAttributes() - 1);
            br.close();
            int trainSize = (int) Math.round(dataset.numInstances() * (Double.parseDouble(jsonString.getString("percent")) / 100.0D));
            Instances training = new Instances(dataset, 0, trainSize);
            br.close();
            String options = "-R " + jsonString.getString("Ridge") + " -M " + jsonString.getString("Iterations");
            String[] optionsArray = options.split(" ");
            lr.setOptions(optionsArray);
            lr.buildClassifier(training);
            return lr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lr;
    }

    public MultilayerPerceptron multiLayerPerceptron(JSONObject jsonString, S3Object s3object) {
        MultilayerPerceptron mlp = new MultilayerPerceptron();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
            Instances dataset = new Instances(br);
            dataset.setClassIndex(dataset.numAttributes() - 1);
            br.close();
            int trainSize = (int) Math.round(dataset.numInstances() * (Double.parseDouble(jsonString.getString("percent")) / 100.0D));
            Instances training = new Instances(dataset, 0, trainSize);
            br.close();
            String options = " -L " + jsonString.getString("LearningRate") + " -M " + jsonString.getString("Momentum") + " -N " + jsonString.getString("Epochs") + " -V " + jsonString.getString("ValidationSet") + " -S " + jsonString.getString("Seed") + " -E " + jsonString.getString("Threshold");
            String[] optionsArray = options.split(" ");
            mlp.setOptions(optionsArray);
            mlp.buildClassifier(training);
            return mlp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mlp;
    }
}


