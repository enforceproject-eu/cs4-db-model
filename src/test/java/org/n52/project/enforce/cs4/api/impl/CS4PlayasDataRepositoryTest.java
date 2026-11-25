package org.n52.project.enforce.cs4.api.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.n52.project.enforce.cs4.api.impl.manorba.CS4PlayasData;
import org.n52.project.enforce.cs4.api.impl.manorba.CS4PlayasDataRepository;
import org.n52.project.enforce.cs4.api.impl.manorba.CS4PlayasObservedProperty;
import org.n52.project.enforce.cs4.api.impl.manorba.CS4PlayasObservedPropertyRepository;
import org.n52.project.enforce.cs4.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;

public class CS4PlayasDataRepositoryTest extends RepositoryTest {

    @Autowired
    CS4PlayasDataRepository cs4PlayasDataRepository;

    @Autowired
    CS4PlayasObservedPropertyRepository observedPropertyRepository;
    
    @Autowired
    Utils utils;

    @Test
    void testAddData() {
        Integer id = 235;
        CS4PlayasData data = new CS4PlayasData();
        data.setId(id);
        cs4PlayasDataRepository.save(data);
        CS4PlayasObservedProperty dataReference = new CS4PlayasObservedProperty(66, "ddsgdagag");
        observedPropertyRepository.save(dataReference);
        data.putObservedPropertyCount(dataReference, 7);
        cs4PlayasDataRepository.saveAndFlush(data);
    }

}
