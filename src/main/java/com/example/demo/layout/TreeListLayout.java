package com.example.demo.layout;

import com.example.demo.model.MindMap;
import com.example.demo.model.MindMapNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树形列表布局策略实现
 * 根据树形列表的结构布局节点，使思维导图的布局与右侧列表视图对应
 */
public class TreeListLayout implements LayoutStrategy {

    private static final double HORIZONTAL_GAP = 250; // 水平间距 - 增加以延长连接线
    private static final double VERTICAL_GAP = 50;    // 垂直间距

    @Override
    public void applyLayout(MindMap mindMap, double canvasWidth, double canvasHeight) {
        System.out.println("TreeListLayout.applyLayout called with canvas size: " + canvasWidth + "x" + canvasHeight);

        MindMapNode rootNode = mindMap.getRootNode();

        // 计算每个节点的深度和在其层级中的索引
        Map<MindMapNode, Integer> depthMap = new HashMap<>();
        Map<MindMapNode, Integer> indexMap = new HashMap<>();

        // 计算节点总数，用于确定垂直间距
        int totalNodes = mindMap.getAllNodes().size();
        double availableHeight = canvasHeight - 100; // 留出上下边距
        double nodeSpacing = Math.min(VERTICAL_GAP, availableHeight / (totalNodes + 1));

        // 计算节点深度和索引
        calculateDepthAndIndex(rootNode, 0, 0, depthMap, indexMap);

        // 获取最大深度
        int maxDepth = 0;
        for (int depth : depthMap.values()) {
            maxDepth = Math.max(maxDepth, depth);
        }

        // 计算每个深度级别的节点数量
        Map<Integer, Integer> depthCountMap = new HashMap<>();
        for (int depth : depthMap.values()) {
            depthCountMap.put(depth, depthCountMap.getOrDefault(depth, 0) + 1);
        }

        // 布局所有节点
        for (MindMapNode node : mindMap.getAllNodes()) {
            if (!node.isManuallyPositioned()) {
                int depth = depthMap.get(node);
                int index = indexMap.get(node);
                int nodesAtDepth = depthCountMap.get(depth);

                // 计算X坐标 - 基于深度
                double x = 50 + (depth * HORIZONTAL_GAP);

                // 计算Y坐标 - 基于索引和该深度的节点总数
                double totalHeight = nodesAtDepth * node.getHeight() + (nodesAtDepth - 1) * nodeSpacing;
                double startY = (canvasHeight - totalHeight) / 2;
                double y = startY + index * (node.getHeight() + nodeSpacing);

                // 设置节点位置
                node.setX(x);
                node.setY(y);

                System.out.println("Node " + node.getText() + " (depth=" + depth + ", index=" + index +
                                  ") positioned at (" + x + ", " + y + ")");
            } else {
                System.out.println("Node " + node.getText() + " is manually positioned, not changing position.");
            }
        }
    }

    /**
     * 递归计算节点的深度和索引
     * @param node 当前节点
     * @param depth 当前深度
     * @param index 当前索引
     * @param depthMap 深度映射
     * @param indexMap 索引映射
     * @return 下一个索引
     */
    private int calculateDepthAndIndex(MindMapNode node, int depth, int index,
                                      Map<MindMapNode, Integer> depthMap,
                                      Map<MindMapNode, Integer> indexMap) {
        // 记录节点深度和索引
        depthMap.put(node, depth);
        indexMap.put(node, index);

        // 递归处理子节点
        int nextIndex = index + 1;
        for (MindMapNode child : node.getChildren()) {
            nextIndex = calculateDepthAndIndex(child, depth + 1, nextIndex, depthMap, indexMap);
        }

        return nextIndex;
    }

    @Override
    public String getName() {
        return "树形列表布局";
    }
}
