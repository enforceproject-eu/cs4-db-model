package org.n52.project.enforce.cs4.api.impl;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.n52.project.enforce.cs4.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class UtilsTest extends RepositoryTest {

    @Autowired
    Utils utils;
    
    @Test
    void testCreateCdm() {
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            File tmpFile = File.createTempFile("xyz", ".tmp");
            JsonNode jsonNode = mapper.readTree(getClass().getResourceAsStream("4fb46055-fd68-4503-8960-41a61db44b5f"));
            JsonNode resultNode = jsonNode.get("results");
            if(resultNode instanceof ArrayNode) {
                utils.convertToCdm(((ArrayNode)resultNode).get(0), tmpFile.getParentFile());
                
            }else {
                utils.convertToCdm(resultNode, tmpFile.getParentFile());                
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
    
}
