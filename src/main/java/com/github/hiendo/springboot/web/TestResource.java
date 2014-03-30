package com.github.hiendo.springboot.web;

import com.github.hiendo.springboot.entities.TestEntity;
import com.github.hiendo.springboot.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Component
@Path("testResource")
public class TestResource {
    final static Logger logger = LoggerFactory.getLogger(TestResource.class);

    @Autowired
    private TestService testService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TestEntity getTestEntity() throws Exception {
        logger.debug("Sending test entity");
        return testService.getTestEntity();
	}
}
