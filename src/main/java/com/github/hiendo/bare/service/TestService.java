package com.github.hiendo.bare.service;

import com.github.hiendo.bare.entities.TestEntity;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    public TestEntity getTestEntity() {
        return new TestEntity("testing");
    }
}
