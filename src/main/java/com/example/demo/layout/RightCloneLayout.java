package com.example.demo.layout;

import com.example.demo.model.MindMap;
import com.example.demo.model.MindMapNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 右侧克隆布局
 * 根节点在左侧，所有子节点在右侧
 */
public class RightCloneLayout extends CloneBasedLayout {

    @Override
    protected void doLayout(MindMap mindMap, Map<String, NodeInfo> nodeInfoMap, double canvasWidth, double canvasHeight) {
        // 获取根节点
        MindMapNode rootNode = mindMap.getRootNode();

        // 将根节点放在画布左侧，除非它已经被手动定位
        if (!rootNode.isManuallyPositioned()) {
            rootNode.setX(canvasWidth * 0.2 - rootNode.getWidth());
            rootNode.setY(canvasHeight / 2);
        }
        System.out.println("Root node position set to: (" + rootNode.getX() + ", " + rootNode.getY() + ")");

        // 获取所有子节点
        List<MindMapNode> children = new ArrayList<>(rootNode.getChildren());
        if (children.isEmpty()) {
            return; // 没有子节点，不需要布局
        }

        // 计算所有子节点的总高度
        double totalHeight = 0;
        for (MindMapNode child : children) {
            totalHeight += child.getHeight();
        }
        // 添加节点间的间距
        totalHeight += VERTICAL_GAP * (children.size() - 1);

        // 计算起始Y坐标，使节点垂直居中
        double startY = rootNode.getY() - (totalHeight / 2);

        // 计算右侧X坐标
        double x = rootNode.getX() + rootNode.getWidth() + HORIZONTAL_GAP;

        // 布局所有子节点
        for (MindMapNode child : children) {
            // 只有当节点没有被手动定位时才设置其位置
            if (!child.isManuallyPositioned()) {
                child.setX(x);
                child.setY(startY);

                // 递归布局子节点的子节点
                layoutChildrenRight(child, x + child.getWidth() + HORIZONTAL_GAP);
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
        return "右侧布局";
    }
}
