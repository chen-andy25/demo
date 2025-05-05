package com.example.demo;

/**
 * 应用程序启动器类
 * 用于解决 JavaFX 应用程序打包成 JAR 后的启动问题
 */
public class Launcher {
    
    /**
     * 主方法，用于启动 JavaFX 应用程序
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 调用 JavaFX 应用程序的 main 方法
        HelloApplication.main(args);
    }
}
