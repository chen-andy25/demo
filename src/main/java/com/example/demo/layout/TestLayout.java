package com.example.demo.layout;

import com.example.demo.model.MindMap;
import com.example.demo.model.MindMapNode;

import java.util.List;

/**
 * 测试布局策略实现
 * 一个简单的布局算法，将所有节点放置在明显不同的位置
 */
public class TestLayout implements LayoutStrategy {
    
    @Override
    public void applyLayout(MindMap mindMap, double canvasWidth, double canvasHeight) {
        System.out.println("TestLayout.applyLayout called with canvas size: " + canvasWidth + "x" + canvasHeight);
        
        MindMapNode rootNode = mindMap.getRootNode();
        
        // 将根节点放在画布中心
        rootNode.setX(canvasWidth / 2);
        rootNode.setY(canvasHeight / 2);
        System.out.println("Root node position set to: (" + rootNode.getX() + ", " + rootNode.getY() + ")");
        
        // 获取所有节点
        List<MindMapNode> allNodes = mindMap.getAllNodes();
        
        // 将所有非根节点放置在一个圆形上
        int nodeCount = allNodes.size() - 1; // 减去根节点
        if (nodeCount > 0) {
            double radius = Math.min(canvasWidth, canvasHeight) * 0.4; // 圆的半径
            
            int index = 0;
            for (MindMapNode node : allNodes) {
                if (node != rootNode) {
                    // 计算节点在圆上的位置
                    double angle = 2 * Math.PI * index / nodeCount;
                    double x = rootNode.getX() + radius * Math.cos(angle);
                    double y = rootNode.getY() + radius * Math.sin(angle);
                    
                    // 设置节点位置
                    node.setX(x);
                    node.setY(y);
                    System.out.println("Node " + node.getText() + " position set to: (" + x + ", " + y + ")");
                    
                    index++;
                }
            }
        }
    }
    
    @Override
    public String getName() {
        return "测试布局";
    }
}
