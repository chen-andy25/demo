package com.example.demo.layout;

import com.example.demo.model.MindMap;
import com.example.demo.model.MindMapNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 标准布局策略实现
 * 特点：
 * 1. Root节点的子节点先左后右布局
 * 2. 左边子节点后续节点往左，右边子节点后续节点往右
 * 3. Root节点的子节点围绕Root节点带向内的弧度紧凑布局
 * 4. Root节点后两层以后的子节点和所在层子节点垂直对齐
 */
public class StandardLayout implements LayoutStrategy {

    private static final double HORIZONTAL_GAP = 200; // 水平间距 - 增加以延长连接线
    private static final double VERTICAL_GAP = 40;    // 垂直间距
    private static final double LEVEL_SCALE = 0.9;    // 每层级缩放比例

    @Override
    public void applyLayout(MindMap mindMap, double canvasWidth, double canvasHeight) {
        System.out.println("StandardLayout.applyLayout called with canvas size: " + canvasWidth + "x" + canvasHeight);

        MindMapNode rootNode = mindMap.getRootNode();
        System.out.println("Root node: " + rootNode.getText() + ", position before: (" + rootNode.getX() + ", " + rootNode.getY() + ")");

        // 如果根节点没有被手动定位，则设置其位置在画布中心
        if (!rootNode.isManuallyPositioned()) {
            rootNode.setX(canvasWidth / 2 - rootNode.getWidth() / 2);
            rootNode.setY(canvasHeight / 2 - rootNode.getHeight() / 2);
            System.out.println("Root node position set to: (" + rootNode.getX() + ", " + rootNode.getY() + ")");
        } else {
            System.out.println("Root node is manually positioned, not changing position.");
        }

        List<MindMapNode> children = rootNode.getChildren();
        if (children.isEmpty()) {
            return;
        }

        // 将子节点分为左右两组
        List<MindMapNode> leftNodes = new ArrayList<>();
        List<MindMapNode> rightNodes = new ArrayList<>();

        // 计算所有子节点的总高度
        double totalHeight = calculateTotalHeight(children);

        // 平均分配节点到左右两侧，使两侧高度尽量接近
        double currentHeight = 0;
        for (int i = 0; i < children.size(); i++) {
            MindMapNode child = children.get(i);
            double nodeHeight = child.getHeight();

            // 前半部分节点放左边，后半部分节点放右边
            if (i < children.size() / 2) {
                leftNodes.add(child);
            } else {
                rightNodes.add(child);
            }

            currentHeight += nodeHeight + VERTICAL_GAP;
        }

        // 布局左侧节点
        if (!leftNodes.isEmpty()) {
            double leftTotalHeight = calculateTotalHeight(leftNodes);
            double startY = rootNode.getY() - (leftTotalHeight / 2);

            for (MindMapNode node : leftNodes) {
                if (!node.isManuallyPositioned()) {
                    // 设置节点位置
                    node.setX(rootNode.getX() - HORIZONTAL_GAP - node.getWidth());
                    node.setY(startY + node.getHeight() / 2);

                    // 递归布局子节点
                    layoutLeftSubtree(node, 2);
                }

                startY += node.getHeight() + VERTICAL_GAP;
            }
        }

        // 布局右侧节点
        if (!rightNodes.isEmpty()) {
            double rightTotalHeight = calculateTotalHeight(rightNodes);
            double startY = rootNode.getY() - (rightTotalHeight / 2);

            for (MindMapNode node : rightNodes) {
                if (!node.isManuallyPositioned()) {
                    // 设置节点位置
                    node.setX(rootNode.getX() + rootNode.getWidth() + HORIZONTAL_GAP);
                    node.setY(startY + node.getHeight() / 2);

                    // 递归布局子节点
                    layoutRightSubtree(node, 2);
                }

                startY += node.getHeight() + VERTICAL_GAP;
            }
        }
    }

    /**
     * 布局左侧子树
     * @param parent 父节点
     * @param level 当前层级
     */
    private void layoutLeftSubtree(MindMapNode parent, int level) {
        List<MindMapNode> children = parent.getChildren();
        if (children.isEmpty()) {
            return;
        }

        // 计算子节点总高度
        double totalHeight = calculateTotalHeight(children);

        // 计算起始Y坐标，使子节点垂直居中于父节点
        double startY = parent.getY() - (totalHeight / 2);

        // 布局所有子节点
        for (MindMapNode child : children) {
            if (!child.isManuallyPositioned()) {
                // 设置节点位置，向左布局
                child.setX(parent.getX() - HORIZONTAL_GAP - child.getWidth());
                child.setY(startY + child.getHeight() / 2);

                // 递归布局子节点
                layoutLeftSubtree(child, level + 1);
            }

            startY += child.getHeight() + VERTICAL_GAP;
        }
    }

    /**
     * 布局右侧子树
     * @param parent 父节点
     * @param level 当前层级
     */
    private void layoutRightSubtree(MindMapNode parent, int level) {
        List<MindMapNode> children = parent.getChildren();
        if (children.isEmpty()) {
            return;
        }

        // 计算子节点总高度
        double totalHeight = calculateTotalHeight(children);

        // 计算起始Y坐标，使子节点垂直居中于父节点
        double startY = parent.getY() - (totalHeight / 2);

        // 布局所有子节点
        for (MindMapNode child : children) {
            if (!child.isManuallyPositioned()) {
                // 设置节点位置，向右布局
                child.setX(parent.getX() + parent.getWidth() + HORIZONTAL_GAP);
                child.setY(startY + child.getHeight() / 2);

                // 递归布局子节点
                layoutRightSubtree(child, level + 1);
            }

            startY += child.getHeight() + VERTICAL_GAP;
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
        return "标准布局";
    }
}
