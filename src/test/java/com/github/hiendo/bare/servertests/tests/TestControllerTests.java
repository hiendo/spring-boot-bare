package com.github.hiendo.bare.servertests.tests;

import com.github.hiendo.bare.entities.TestEntity;
import com.github.hiendo.bare.servertests.AbstractServerTests;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


public class TestControllerTests extends AbstractServerTests {

    @Test
    public void canGetTestEntity() throws Exception {
        TestEntity testEntity = testControllerOperations.getTestEntity();

        assertThat(testEntity, notNullValue());
        assertThat(testEntity.getTestValue(), notNullValue());
    }
}
