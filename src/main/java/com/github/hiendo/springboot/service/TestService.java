package com.github.hiendo.springboot.service;

import com.github.hiendo.springboot.entities.TestEntity;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    public TestEntity getTestEntity() {
        return new TestEntity("testing");
    }
}
