package com.example.demo.layout;

import com.example.demo.model.MindMap;
import com.example.demo.model.MindMapNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动布局策略实现
 * 中心节点的所有子节点平均排列在其左、右两侧
 * 根据节点的层级和关系进行更清晰的排列
 */
public class AutoLayout implements LayoutStrategy {

    private static final double HORIZONTAL_GAP = 100; // 水平间距 - 减小以缩短连接线
    private static final double VERTICAL_GAP = 40;    // 垂直间距
    private static final double LEVEL_SCALE = 0.9;    // 每层级缩放比例

    @Override
    public void applyLayout(MindMap mindMap, double canvasWidth, double canvasHeight) {
        MindMapNode rootNode = mindMap.getRootNode();

        // 如果根节点没有被手动定位，则设置其位置在画布中心
        if (!rootNode.isManuallyPositioned()) {
            rootNode.setX(canvasWidth / 2 - rootNode.getWidth() / 2);
            rootNode.setY(canvasHeight / 2 - rootNode.getHeight() / 2);
        }

        List<MindMapNode> children = rootNode.getChildren();
        if (children.isEmpty()) {
            return;
        }

        // 按层级对节点进行分组
        Map<Integer, List<MindMapNode>> levelMap = new HashMap<>();
        Map<MindMapNode, Integer> nodeDepthMap = new HashMap<>();

        // 递归计算每个节点的层级和子节点数量
        calculateNodeDepth(rootNode, 0, levelMap, nodeDepthMap);

        // 将所有节点分为左右两部分
        Map<Integer, List<MindMapNode>> leftLevelMap = new HashMap<>();
        Map<Integer, List<MindMapNode>> rightLevelMap = new HashMap<>();

        // 平均分配每一层的节点到左右两侧
        for (Map.Entry<Integer, List<MindMapNode>> entry : levelMap.entrySet()) {
            int level = entry.getKey();
            List<MindMapNode> nodesAtLevel = entry.getValue();

            // 初始化左右层级列表
            leftLevelMap.put(level, new ArrayList<>());
            rightLevelMap.put(level, new ArrayList<>());

            // 平均分配节点
            int totalNodes = nodesAtLevel.size();
            int leftCount = totalNodes / 2 + (totalNodes % 2); // 如果是奇数，左侧多一个

            for (int i = 0; i < totalNodes; i++) {
                MindMapNode node = nodesAtLevel.get(i);
                if (i < leftCount) {
                    leftLevelMap.get(level).add(node);
                } else {
                    rightLevelMap.get(level).add(node);
                }
            }
        }

        // 布局左侧节点
        for (int level = 1; level <= leftLevelMap.size(); level++) {
            List<MindMapNode> nodesAtLevel = leftLevelMap.get(level - 1);
            if (nodesAtLevel == null || nodesAtLevel.isEmpty()) {
                continue;
            }

            // 计算当前层级的总高度
            double totalHeight = calculateTotalHeight(nodesAtLevel);

            // 计算当前层级的X坐标
            double levelX = rootNode.getX() - (HORIZONTAL_GAP * level);

            // 计算起始Y坐标，使节点垂直居中
            double startY = rootNode.getY() - (totalHeight / 2);

            // 布局当前层级的所有节点
            for (MindMapNode node : nodesAtLevel) {
                // 如果节点已经被手动定位，则不改变其位置
                if (!node.isManuallyPositioned()) {
                    // 设置节点位置
                    node.setX(levelX);
                    node.setY(startY + node.getHeight() / 2);
                }

                // 更新起始Y坐标为下一个节点
                startY += node.getHeight() + VERTICAL_GAP * (1 + 0.2 * level); // 深层级间距稍大
            }
        }

        // 布局右侧节点
        for (int level = 1; level <= rightLevelMap.size(); level++) {
            List<MindMapNode> nodesAtLevel = rightLevelMap.get(level - 1);
            if (nodesAtLevel == null || nodesAtLevel.isEmpty()) {
                continue;
            }

            // 计算当前层级的总高度
            double totalHeight = calculateTotalHeight(nodesAtLevel);

            // 计算当前层级的X坐标
            double levelX = rootNode.getX() + rootNode.getWidth() + (HORIZONTAL_GAP * level);

            // 计算起始Y坐标，使节点垂直居中
            double startY = rootNode.getY() - (totalHeight / 2);

            // 布局当前层级的所有节点
            for (MindMapNode node : nodesAtLevel) {
                // 如果节点已经被手动定位，则不改变其位置
                if (!node.isManuallyPositioned()) {
                    // 设置节点位置
                    node.setX(levelX);
                    node.setY(startY + node.getHeight() / 2);
                }

                // 更新起始Y坐标为下一个节点
                startY += node.getHeight() + VERTICAL_GAP * (1 + 0.2 * level); // 深层级间距稍大
            }
        }
    }

    /**
     * 递归计算节点的层级和子节点数量
     * @param node 当前节点
     * @param depth 当前深度
     * @param levelMap 层级映射
     * @param nodeDepthMap 节点深度映射
     */
    private void calculateNodeDepth(MindMapNode node, int depth, Map<Integer, List<MindMapNode>> levelMap, Map<MindMapNode, Integer> nodeDepthMap) {
        // 记录节点深度
        nodeDepthMap.put(node, depth);

        // 将节点添加到对应层级
        if (!levelMap.containsKey(depth)) {
            levelMap.put(depth, new ArrayList<>());
        }

        // 如果不是根节点，则添加到层级列表
        if (depth > 0) {
            levelMap.get(depth - 1).add(node);
        }

        // 递归处理子节点
        for (MindMapNode child : node.getChildren()) {
            calculateNodeDepth(child, depth + 1, levelMap, nodeDepthMap);
        }
    }

    /**
     * 计算节点列表的总高度
     * @param nodes 节点列表
     * @return 总高度
     */
    private double calculateTotalHeight(List<MindMapNode> nodes) {
        if (nodes.isEmpty()) {
            return 0;
        }

        double totalHeight = 0;
        for (MindMapNode node : nodes) {
            totalHeight += node.getHeight();
        }

        // 添加节点间的间距
        totalHeight += VERTICAL_GAP * (nodes.size() - 1);

        return totalHeight;
    }

    @Override
    public String getName() {
        return "自动布局";
    }
}
