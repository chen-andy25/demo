package com.example.demo.layout;

import com.example.demo.model.MindMap;
import com.example.demo.model.MindMapNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 右向逻辑布局策略实现
 * 特点：
 * 1. 从左往右布局各个层次的节点
 * 2. 每个节点的位置只相对于父节点，和其他父节点不同的同层次节点位置不相关
 * 3. 是所谓的"非分层紧凑树布局"
 */
public class LogicalLayout implements LayoutStrategy {
    
    private static final double HORIZONTAL_GAP = 120; // 水平间距
    private static final double VERTICAL_GAP = 40;    // 垂直间距
    
    @Override
    public void applyLayout(MindMap mindMap, double canvasWidth, double canvasHeight) {
        MindMapNode rootNode = mindMap.getRootNode();
        
        // 如果根节点没有被手动定位，则设置其位置在画布左侧中心
        if (!rootNode.isManuallyPositioned()) {
            rootNode.setX(50);
            rootNode.setY(canvasHeight / 2 - rootNode.getHeight() / 2);
        }
        
        // 计算每个节点的深度和子树高度
        Map<MindMapNode, Integer> depthMap = new HashMap<>();
        Map<MindMapNode, Double> subtreeHeightMap = new HashMap<>();
        calculateDepthAndHeight(rootNode, 0, depthMap, subtreeHeightMap);
        
        // 布局整棵树
        layoutTree(rootNode, depthMap, subtreeHeightMap);
    }
    
    /**
     * 计算节点的深度和子树高度
     * @param node 当前节点
     * @param depth 当前深度
     * @param depthMap 深度映射
     * @param subtreeHeightMap 子树高度映射
     * @return 子树高度
     */
    private double calculateDepthAndHeight(MindMapNode node, int depth, 
                                          Map<MindMapNode, Integer> depthMap, 
                                          Map<MindMapNode, Double> subtreeHeightMap) {
        depthMap.put(node, depth);
        
        List<MindMapNode> children = node.getChildren();
        if (children.isEmpty()) {
            subtreeHeightMap.put(node, node.getHeight());
            return node.getHeight();
        }
        
        double totalHeight = 0;
        for (MindMapNode child : children) {
            totalHeight += calculateDepthAndHeight(child, depth + 1, depthMap, subtreeHeightMap);
        }
        
        // 添加子节点间的间距
        totalHeight += VERTICAL_GAP * (children.size() - 1);
        
        // 如果子树高度小于节点自身高度，则使用节点自身高度
        totalHeight = Math.max(totalHeight, node.getHeight());
        
        subtreeHeightMap.put(node, totalHeight);
        return totalHeight;
    }
    
    /**
     * 布局树
     * @param node 当前节点
     * @param depthMap 深度映射
     * @param subtreeHeightMap 子树高度映射
     */
    private void layoutTree(MindMapNode node, 
                           Map<MindMapNode, Integer> depthMap, 
                           Map<MindMapNode, Double> subtreeHeightMap) {
        List<MindMapNode> children = node.getChildren();
        if (children.isEmpty()) {
            return;
        }
        
        int depth = depthMap.get(node);
        double x = node.getX() + node.getWidth() + HORIZONTAL_GAP;
        
        // 计算子节点的起始Y坐标，使子树垂直居中于父节点
        double totalHeight = 0;
        for (MindMapNode child : children) {
            totalHeight += subtreeHeightMap.get(child);
        }
        totalHeight += VERTICAL_GAP * (children.size() - 1);
        
        double startY = node.getY() + (node.getHeight() / 2) - (totalHeight / 2);
        
        // 布局所有子节点
        for (MindMapNode child : children) {
            if (!child.isManuallyPositioned()) {
                double childHeight = subtreeHeightMap.get(child);
                
                // 设置节点位置
                child.setX(x);
                child.setY(startY + (childHeight / 2) - (child.getHeight() / 2));
            }
            
            // 递归布局子节点
            layoutTree(child, depthMap, subtreeHeightMap);
            
            startY += subtreeHeightMap.get(child) + VERTICAL_GAP;
        }
    }
    
    @Override
    public String getName() {
        return "逻辑布局";
    }
}
