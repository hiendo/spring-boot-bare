package com.github.hiendo.bare.entities;

import org.codehaus.jackson.annotate.JsonCreator;

public class TestEntity {

    private String testValue;

    // Json serialization
    private TestEntity(){}

    public TestEntity(String testValue) {
        super();
        this.testValue = testValue;
    }

    public String getTestValue() {
        return testValue;
    }
}
