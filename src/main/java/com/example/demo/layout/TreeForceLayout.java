package com.example.demo.layout;

import com.example.demo.model.MindMap;
import com.example.demo.model.MindMapNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树状强制布局策略实现
 * 所有节点都在下方布局，形成树状结构
 */
public class TreeForceLayout implements LayoutStrategy {

    private static final double HORIZONTAL_GAP = 150; // 水平间距
    private static final double VERTICAL_GAP = 60;    // 垂直间距

    @Override
    public void applyLayout(MindMap mindMap, double canvasWidth, double canvasHeight) {
        System.out.println("TreeForceLayout.applyLayout called with canvas size: " + canvasWidth + "x" + canvasHeight);

        // 获取根节点
        MindMapNode rootNode = mindMap.getRootNode();
        System.out.println("Root node: " + rootNode.getText());

        // 不重置节点的手动定位标志，保留手动定位的节点位置

        // 如果根节点没有被手动定位，则将其放在画布上方中心
        if (!rootNode.isManuallyPositioned()) {
            rootNode.setX(canvasWidth / 2 - rootNode.getWidth() / 2);
            rootNode.setY(50);
        }
        System.out.println("Root node position set to: (" + rootNode.getX() + ", " + rootNode.getY() + ")");

        // 计算每个节点的深度和在其层级中的索引
        Map<MindMapNode, Integer> depthMap = new HashMap<>();
        Map<Integer, List<MindMapNode>> levelMap = new HashMap<>();

        // 计算节点深度和层级分组
        calculateDepthAndLevels(rootNode, 0, depthMap, levelMap);

        // 获取最大深度
        int maxDepth = 0;
        for (int depth : depthMap.values()) {
            maxDepth = Math.max(maxDepth, depth);
        }
        System.out.println("Max depth: " + maxDepth);

        // 布局所有节点（树状结构）
        for (int depth = 1; depth <= maxDepth; depth++) {
            List<MindMapNode> nodesAtDepth = levelMap.getOrDefault(depth, new ArrayList<>());
            if (nodesAtDepth.isEmpty()) continue;

            System.out.println("Laying out " + nodesAtDepth.size() + " nodes at depth " + depth);

            // 计算Y坐标（向下布局）
            double y = rootNode.getY() + rootNode.getHeight() + (depth * VERTICAL_GAP);

            // 计算节点总宽度
            double totalWidth = calculateTotalWidth(nodesAtDepth);

            // 计算起始X坐标，使节点水平居中
            double startX = (canvasWidth - totalWidth) / 2;

            // 布局节点
            for (MindMapNode node : nodesAtDepth) {
                if (!node.isManuallyPositioned()) {
                    node.setX(startX);
                    node.setY(y);
                }
                System.out.println("Node " + node.getText() + " positioned at (" + node.getX() + ", " + node.getY() + ")");

                startX += node.getWidth() + HORIZONTAL_GAP;
            }
        }
    }

    /**
     * 递归计算节点的深度并按层级分组
     * @param node 当前节点
     * @param depth 当前深度
     * @param depthMap 深度映射
     * @param levelMap 层级映射
     */
    private void calculateDepthAndLevels(MindMapNode node, int depth,
                                        Map<MindMapNode, Integer> depthMap,
                                        Map<Integer, List<MindMapNode>> levelMap) {
        // 记录节点深度
        depthMap.put(node, depth);

        // 将节点添加到对应层级
        if (!levelMap.containsKey(depth)) {
            levelMap.put(depth, new ArrayList<>());
        }
        levelMap.get(depth).add(node);

        // 递归处理子节点
        for (MindMapNode child : node.getChildren()) {
            calculateDepthAndLevels(child, depth + 1, depthMap, levelMap);
        }
    }

    /**
     * 计算节点列表的总宽度
     * @param nodes 节点列表
     * @return 总宽度
     */
    private double calculateTotalWidth(List<MindMapNode> nodes) {
        if (nodes.isEmpty()) {
            return 0;
        }

        double totalWidth = 0;
        for (MindMapNode node : nodes) {
            totalWidth += node.getWidth();
        }

        // 添加节点间的间距
        totalWidth += HORIZONTAL_GAP * (nodes.size() - 1);

        return totalWidth;
    }

    @Override
    public String getName() {
        return "树状布局";
    }
}
