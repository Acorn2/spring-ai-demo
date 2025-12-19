package com.example.mcplab.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * 模拟业务接口，通过 @Tool 注解自动暴露为 MCP 工具
 */
@Service
@Slf4j
public class DeviceControlService {

    @Tool(description = "控制家电设备，开关灯或调节空调温度")
    public String controlDevice(String deviceName, String action, String value) {
        log.info("接收到指令 - 设备: {}, 动作: {}, 值: {}", deviceName, action, value);
        
        // 模拟接口调用逻辑
        if ("light".equalsIgnoreCase(deviceName)) {
            return "已将 " + deviceName + " 设置为 " + action;
        } else if ("air-conditioner".equalsIgnoreCase(deviceName)) {
            return "空调已调节至 " + value + " 度";
        }
        
        return "执行成功: " + deviceName + " " + action;
    }

    @Tool(description = "查询当前各种设备的状态")
    public String getDeviceStatus(String deviceName) {
        log.info("查询设备状态: {}", deviceName);
        return deviceName + " 目前运行正常，处于开启状态。";
    }
}
