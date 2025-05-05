package com.example.demo.layout;

import com.example.demo.model.MindMap;
import com.example.demo.model.MindMapNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树状克隆布局
 * 根节点在上方，所有子节点在下方
 */
public class TreeCloneLayout extends CloneBasedLayout {

    @Override
    protected void doLayout(MindMap mindMap, Map<String, NodeInfo> nodeInfoMap, double canvasWidth, double canvasHeight) {
        // 获取根节点
        MindMapNode rootNode = mindMap.getRootNode();

        // 将根节点放在画布中心，除非它已经被手动定位
        if (!rootNode.isManuallyPositioned()) {
            rootNode.setX(canvasWidth / 2 - rootNode.getWidth() / 2);
            rootNode.setY(canvasHeight / 2 - rootNode.getHeight() / 2);
        }
        System.out.println("Root node position set to: (" + rootNode.getX() + ", " + rootNode.getY() + ")");

        // 获取所有子节点
        List<MindMapNode> children = new ArrayList<>(rootNode.getChildren());
        if (children.isEmpty()) {
            return; // 没有子节点，不需要布局
        }

        // 将子节点分为左右两组
        List<MindMapNode> leftNodes = new ArrayList<>();
        List<MindMapNode> rightNodes = new ArrayList<>();

        // 计算每个节点及其子节点的高度
        Map<MindMapNode, Double> nodeHeightMap = new HashMap<>();
        for (MindMapNode child : children) {
            nodeHeightMap.put(child, calculateNodeHeight(child));
        }

        // 平均分配节点到左右两侧，使两侧高度尽量平衡
        double leftHeight = 0;
        double rightHeight = 0;

        // 按节点高度降序排序
        children.sort((a, b) -> Double.compare(nodeHeightMap.get(b), nodeHeightMap.get(a)));

        // 贪心算法分配节点
        for (MindMapNode child : children) {
            double height = nodeHeightMap.get(child);
            if (leftHeight <= rightHeight) {
                leftNodes.add(child);
                leftHeight += height;
            } else {
                rightNodes.add(child);
                rightHeight += height;
            }
        }

        // 布局左侧节点
        if (!leftNodes.isEmpty()) {
            double totalHeight = leftNodes.stream().mapToDouble(MindMapNode::getHeight).sum() +
                                VERTICAL_GAP * (leftNodes.size() - 1);
            double startY = rootNode.getY() - (totalHeight / 2);
            double x = rootNode.getX() - HORIZONTAL_GAP;

            for (MindMapNode node : leftNodes) {
                if (!node.isManuallyPositioned()) {
                    node.setX(x - node.getWidth());
                    node.setY(startY);

                    // 递归布局子节点
                    layoutChildrenLeft(node, x - node.getWidth() - HORIZONTAL_GAP);
                }

                startY += node.getHeight() + VERTICAL_GAP;
            }
        }

        // 布局右侧节点
        if (!rightNodes.isEmpty()) {
            double totalHeight = rightNodes.stream().mapToDouble(MindMapNode::getHeight).sum() +
                                VERTICAL_GAP * (rightNodes.size() - 1);
            double startY = rootNode.getY() - (totalHeight / 2);
            double x = rootNode.getX() + rootNode.getWidth() + HORIZONTAL_GAP;

            for (MindMapNode node : rightNodes) {
                if (!node.isManuallyPositioned()) {
                    node.setX(x);
                    node.setY(startY);

                    // 递归布局子节点
                    layoutChildrenRight(node, x + node.getWidth() + HORIZONTAL_GAP);
                }

                startY += node.getHeight() + VERTICAL_GAP;
            }
        }
    }

    /**
     * 递归计算节点及其子节点的总高度
     * @param node 节点
     * @return 总高度
     */
    private double calculateNodeHeight(MindMapNode node) {
        List<MindMapNode> children = node.getChildren();
        if (children.isEmpty()) {
            return node.getHeight();
        }

        double childrenHeight = 0;
        for (MindMapNode child : children) {
            childrenHeight += calculateNodeHeight(child);
        }

        // 添加子节点间的间距
        childrenHeight += VERTICAL_GAP * (children.size() - 1);

        // 返回节点自身高度和子节点总高度的最大值
        return Math.max(node.getHeight(), childrenHeight);
    }

    /**
     * 递归布局子节点的子节点（左侧）
     * @param parent 父节点
     * @param rightX 右侧X坐标
     */
    private void layoutChildrenLeft(MindMapNode parent, double rightX) {
        List<MindMapNode> children = parent.getChildren();
        if (children.isEmpty()) {
            return;
        }

        // 计算所有子节点的总高度
        double totalHeight = 0;
        for (MindMapNode child : children) {
            totalHeight += child.getHeight();
        }
        // 添加节点间的间距
        totalHeight += VERTICAL_GAP * (children.size() - 1);

        // 计算起始Y坐标，使节点垂直居中
        double startY = parent.getY() + (parent.getHeight() / 2) - (totalHeight / 2);

        // 布局所有子节点
        for (MindMapNode child : children) {
            // 只有当节点没有被手动定位时才设置其位置
            if (!child.isManuallyPositioned()) {
                child.setX(rightX - child.getWidth());
                child.setY(startY);

                // 递归布局子节点的子节点
                layoutChildrenLeft(child, rightX - child.getWidth() - HORIZONTAL_GAP);
            }

            // 更新起始Y坐标
            startY += child.getHeight() + VERTICAL_GAP;
        }
    }

    /**
     * 递归布局子节点的子节点（右侧）
     * @param parent 父节点
     * @param leftX 左侧X坐标
     */
    private void layoutChildrenRight(MindMapNode parent, double leftX) {
        List<MindMapNode> children = parent.getChildren();
        if (children.isEmpty()) {
            return;
        }

        // 计算所有子节点的总高度
        double totalHeight = 0;
        for (MindMapNode child : children) {
            totalHeight += child.getHeight();
        }
        // 添加节点间的间距
        totalHeight += VERTICAL_GAP * (children.size() - 1);

        // 计算起始Y坐标，使节点垂直居中
        double startY = parent.getY() + (parent.getHeight() / 2) - (totalHeight / 2);

        // 布局所有子节点
        for (MindMapNode child : children) {
            // 只有当节点没有被手动定位时才设置其位置
            if (!child.isManuallyPositioned()) {
                child.setX(leftX);
                child.setY(startY);

                // 递归布局子节点的子节点
                layoutChildrenRight(child, leftX + child.getWidth() + HORIZONTAL_GAP);
            }

            // 更新起始Y坐标
            startY += child.getHeight() + VERTICAL_GAP;
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
        return "自动布局";
    }
}
