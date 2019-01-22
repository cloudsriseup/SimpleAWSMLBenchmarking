package fidelis;

import org.json.simple.JSONObject;

public class Body extends RequestClass {
    public String percent;
    public String algorithm;

    public String getPercent() {
        return this.percent;
    }

    public String kernelType;

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String svmType;

    public String getAlgorithm() {
        return this.algorithm;
    }

    public String degree;

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String tolerance;

    public String getSvnType() {
        return this.svmType;
    }

    public String size;

    public void setSvmType(String svmType) {
        this.svmType = svmType;
    }

    public String epsilon;

    public String getKernelType() {
        return this.kernelType;
    }

    public String gamma;
    public String weight;

    public void setKernelType(String kernelType) {
        this.kernelType = kernelType;
    }


    public String getDegree() {
        return this.degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getTolerance() {
        return this.tolerance;
    }

    public void setTolerance(String tolerance) {
        this.tolerance = tolerance;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getEpsilon() {
        return this.epsilon;
    }

    public void setEpsilon(String epsilon) {
        this.epsilon = epsilon;
    }

    public String getGamma() {
        return this.gamma;
    }

    public void setGamma(String gamma) {
        this.gamma = gamma;
    }

    public String getWeight() {
        return this.weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Body(JSONObject json) {
    }
}
