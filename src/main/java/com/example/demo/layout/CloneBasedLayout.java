package com.example.demo.layout;

import com.example.demo.model.MindMap;
import com.example.demo.model.MindMapNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于克隆的布局算法基类
 * 通过克隆当前画布内容并重新构建来实现布局
 */
public abstract class CloneBasedLayout implements LayoutStrategy {

    protected static final double HORIZONTAL_GAP = 250; // 水平间距
    protected static final double VERTICAL_GAP = 80;    // 垂直间距

    /**
     * 应用布局
     * @param mindMap 思维导图
     * @param canvasWidth 画布宽度
     * @param canvasHeight 画布高度
     */
    @Override
    public void applyLayout(MindMap mindMap, double canvasWidth, double canvasHeight) {
        System.out.println(getClass().getSimpleName() + ".applyLayout called with canvas size: " + canvasWidth + "x" + canvasHeight);

        // 获取根节点
        MindMapNode rootNode = mindMap.getRootNode();
        System.out.println("Root node: " + rootNode.getText());

        // 清除所有节点的手动定位标志，以便重新布局
        // 注意：这里我们先清除标志，布局后再重新设置为手动定位
        // 这样可以确保布局后节点仍然可以拖动
        for (MindMapNode node : mindMap.getAllNodes()) {
            node.setManuallyPositioned(false);
        }

        // 克隆节点信息
        Map<String, NodeInfo> nodeInfoMap = new HashMap<>();
        for (MindMapNode node : mindMap.getAllNodes()) {
            nodeInfoMap.put(node.getId(), new NodeInfo(node));
        }

        // 调用子类实现的布局方法
        doLayout(mindMap, nodeInfoMap, canvasWidth, canvasHeight);

        // 布局完成后，将所有节点标记为手动定位
        // 这样可以确保布局后节点仍然可以拖动
        for (MindMapNode node : mindMap.getAllNodes()) {
            node.setManuallyPositioned(true);
        }
    }

    /**
     * 子类实现的布局方法
     * @param mindMap 思维导图
     * @param nodeInfoMap 节点信息映射
     * @param canvasWidth 画布宽度
     * @param canvasHeight 画布高度
     */
    protected abstract void doLayout(MindMap mindMap, Map<String, NodeInfo> nodeInfoMap, double canvasWidth, double canvasHeight);

    /**
     * 节点信息类，用于保存节点的原始信息
     */
    protected static class NodeInfo {
        private final String id;
        private final String text;
        private final double width;
        private final double height;
        private final double x;
        private final double y;
        private final boolean isCenter;
        private final List<String> childrenIds;

        /**
         * 创建节点信息
         * @param node 节点
         */
        public NodeInfo(MindMapNode node) {
            this.id = node.getId();
            this.text = node.getText();
            this.width = node.getWidth();
            this.height = node.getHeight();
            this.x = node.getX();
            this.y = node.getY();
            this.isCenter = node.isCenterNode();

            this.childrenIds = new ArrayList<>();
            for (MindMapNode child : node.getChildren()) {
                childrenIds.add(child.getId());
            }
        }

        /**
         * 获取节点ID
         * @return 节点ID
         */
        public String getId() {
            return id;
        }

        /**
         * 获取节点文本
         * @return 节点文本
         */
        public String getText() {
            return text;
        }

        /**
         * 获取节点宽度
         * @return 节点宽度
         */
        public double getWidth() {
            return width;
        }

        /**
         * 获取节点高度
         * @return 节点高度
         */
        public double getHeight() {
            return height;
        }

        /**
         * 获取节点X坐标
         * @return 节点X坐标
         */
        public double getX() {
            return x;
        }

        /**
         * 获取节点Y坐标
         * @return 节点Y坐标
         */
        public double getY() {
            return y;
        }

        /**
         * 判断是否为中心节点
         * @return 是否为中心节点
         */
        public boolean isCenter() {
            return isCenter;
        }

        /**
         * 获取子节点ID列表
         * @return 子节点ID列表
         */
        public List<String> getChildrenIds() {
            return childrenIds;
        }
    }
}
