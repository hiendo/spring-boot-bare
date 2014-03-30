package com.github.hiendo.springboot.servertests.tests;

import com.github.hiendo.springboot.entities.TestEntity;
import com.github.hiendo.springboot.servertests.AbstractServerTests;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


public class RestTests extends AbstractServerTests {

    @Test
    public void canGetTestEntity() throws Exception {
        TestEntity testEntity = restTestOperations.getEntityFromController();

        assertThat(testEntity, notNullValue());
        assertThat(testEntity.getTestValue(), notNullValue());
    }

    @Test
    public void canGetTestEntityUsingJerseyResource() throws Exception {
        TestEntity testEntity = restTestOperations.getEntityFromResource();

        assertThat(testEntity, notNullValue());
        assertThat(testEntity.getTestValue(), notNullValue());
    }
}
