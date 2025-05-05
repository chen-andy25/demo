package com.example.demo.layout;

import com.example.demo.model.MindMap;

/**
 * 思维导图布局策略接口
 */
public interface LayoutStrategy {
    
    /**
     * 应用布局策略
     * @param mindMap 要布局的思维导图
     * @param canvasWidth 画布宽度
     * @param canvasHeight 画布高度
     */
    void applyLayout(MindMap mindMap, double canvasWidth, double canvasHeight);
    
    /**
     * 获取布局名称
     * @return 布局名称
     */
    String getName();
}
