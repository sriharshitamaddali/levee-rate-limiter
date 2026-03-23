package com.levee.model;

public enum AlgorithmType {
    FIXED_WINDOW("FIXED_WINDOW"),
    TOKEN_BUCKET("TOKEN_BUCKET");

    private final String algorithm;

    AlgorithmType(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }
}
