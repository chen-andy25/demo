package com.example.demo.layout;

import com.example.demo.model.MindMap;
import com.example.demo.model.MindMapNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 左侧布局策略实现
 * 中心节点的所有子节点排列在其左侧
 * 根据节点的层级和关系进行更清晰的排列
 */
public class LeftLayout implements LayoutStrategy {

    private static final double HORIZONTAL_GAP = 100; // 水平间距 - 减小以缩短连接线
    private static final double VERTICAL_GAP = 40;    // 垂直间距
    private static final double LEVEL_SCALE = 0.9;    // 每层级缩放比例

    @Override
    public void applyLayout(MindMap mindMap, double canvasWidth, double canvasHeight) {
        MindMapNode rootNode = mindMap.getRootNode();

        // 如果根节点没有被手动定位，则设置其位置在画布中心偏右
        if (!rootNode.isManuallyPositioned()) {
            rootNode.setX(canvasWidth * 0.7 - rootNode.getWidth() / 2);
            rootNode.setY(canvasHeight / 2 - rootNode.getHeight() / 2);
        }

        List<MindMapNode> children = rootNode.getChildren();
        if (children.isEmpty()) {
            return;
        }

        // 按子节点数量分组，使布局更平衡
        List<List<MindMapNode>> levels = groupNodesByLevel(rootNode);

        // 布局每一层级的节点
        for (int i = 0; i < levels.size(); i++) {
            List<MindMapNode> levelNodes = levels.get(i);
            int depth = i + 1; // 层级深度，从1开始

            // 计算当前层级的总高度
            double totalHeight = calculateTotalHeight(levelNodes);

            // 计算当前层级的X坐标
            double levelX = rootNode.getX() - (HORIZONTAL_GAP * depth);

            // 计算起始Y坐标，使节点垂直居中
            double startY = rootNode.getY() - (totalHeight / 2);

            // 布局当前层级的所有节点
            for (MindMapNode node : levelNodes) {
                // 如果节点已经被手动定位，则不改变其位置
                if (!node.isManuallyPositioned()) {
                    // 计算节点缩放比例，越深层级越小
                    double scale = Math.pow(LEVEL_SCALE, depth - 1);

                    // 设置节点位置
                    node.setX(levelX);
                    node.setY(startY + node.getHeight() / 2);
                }

                // 更新起始Y坐标为下一个节点
                startY += node.getHeight() + VERTICAL_GAP * (1 + 0.2 * depth); // 深层级间距稍大
            }
        }
    }

    /**
     * 按层级对节点进行分组
     * @param rootNode 根节点
     * @return 分组后的节点列表，每个列表代表一个层级
     */
    private List<List<MindMapNode>> groupNodesByLevel(MindMapNode rootNode) {
        List<List<MindMapNode>> levels = new ArrayList<>();
        groupNodesByLevelRecursive(rootNode, 0, levels);
        return levels;
    }

    /**
     * 递归对节点进行分组
     * @param node 当前节点
     * @param level 当前层级
     * @param levels 分组结果
     */
    private void groupNodesByLevelRecursive(MindMapNode node, int level, List<List<MindMapNode>> levels) {
        List<MindMapNode> children = node.getChildren();
        if (children.isEmpty()) {
            return;
        }

        // 确保层级列表大小足够
        while (levels.size() <= level) {
            levels.add(new ArrayList<>());
        }

        // 将子节点添加到当前层级
        levels.get(level).addAll(children);

        // 递归处理每个子节点
        for (MindMapNode child : children) {
            groupNodesByLevelRecursive(child, level + 1, levels);
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
        return "左侧布局";
    }
}
