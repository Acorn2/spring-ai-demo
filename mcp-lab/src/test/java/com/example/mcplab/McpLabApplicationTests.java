package com.example.mcplab;

import com.example.mcplab.service.DeviceControlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class McpLabApplicationTests {

    @Autowired
    private DeviceControlService deviceControlService;

    @Test
    void contextLoads() {
    }

    @Test
    void testToolDirectly() {
        String result = deviceControlService.controlDevice("light", "on", "100");
        assertThat(result).contains("light").contains("on");
    }
}
