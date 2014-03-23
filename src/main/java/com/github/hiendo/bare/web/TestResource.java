package com.github.hiendo.bare.web;

import com.github.hiendo.bare.entities.TestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@Path("testResource")
public class TestResource {
    final static Logger logger = LoggerFactory.getLogger(TestResource.class);

    @GET
    @Produces("application/json")
    public TestEntity getTestEntity() throws Exception {
        logger.debug("Sending test entity");
        return new TestEntity("testing");
	}
}
