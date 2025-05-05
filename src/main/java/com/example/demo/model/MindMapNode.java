package com.example.demo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 表示思维导图中的一个节点
 */
public class MindMapNode implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String text;
    private MindMapNode parent;
    private List<MindMapNode> children;
    private double x;
    private double y;
    private double width;
    private double height;
    private boolean selected;
    private boolean manuallyPositioned; // 标记节点是否被手动移动过
    private NodeShape shape = NodeShape.ROUNDED_RECTANGLE; // 节点形状，默认为圆角矩形
    private double sizeScale = 1.0; // 节点大小缩放比例
    private double fontSize = 14.0; // 节点字体大小
    // 使用transient标记不可序列化的Color对象
    private transient javafx.scene.paint.Color color = javafx.scene.paint.Color.DODGERBLUE; // 节点颜色

    // 存储颜色的RGB值用于序列化
    private double colorRed = color.getRed();
    private double colorGreen = color.getGreen();
    private double colorBlue = color.getBlue();
    private double colorOpacity = color.getOpacity();
    private LineStyle lineStyle = LineStyle.SOLID; // 连接线样式，默认为实线

    /**
     * 连接线样式枚举
     */
    public enum LineStyle {
        SOLID,          // 实线
        DASHED,         // 虚线
        ARROW_SOLID,    // 箭头实线
        ARROW_DASHED    // 箭头虚线
    }

    /**
     * 节点形状枚举
     */
    public enum NodeShape {
        RECTANGLE, // 矩形
        ROUNDED_RECTANGLE, // 圆角矩形
        ELLIPSE, // 椭圆形
        DIAMOND, // 菱形
        HEXAGON // 六边形
    }

    /**
     * 创建一个新节点
     * @param text 节点文本
     */
    public MindMapNode(String text) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.children = new ArrayList<>();
        this.selected = false;
    }

    /**
     * 添加子节点
     * @param child 子节点
     */
    public void addChild(MindMapNode child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * 删除子节点
     * @param child 要删除的子节点
     * @return 是否成功删除
     */
    public boolean removeChild(MindMapNode child) {
        return children.remove(child);
    }

    /**
     * 删除所有子节点
     */
    public void removeAllChildren() {
        children.clear();
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
     * 设置节点文本
     * @param text 节点文本
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 获取父节点
     * @return 父节点
     */
    public MindMapNode getParent() {
        return parent;
    }

    /**
     * 设置父节点
     * @param parent 父节点
     */
    public void setParent(MindMapNode parent) {
        this.parent = parent;
    }

    /**
     * 获取所有子节点
     * @return 子节点列表
     */
    public List<MindMapNode> getChildren() {
        return children;
    }

    /**
     * 获取X坐标
     * @return X坐标
     */
    public double getX() {
        return x;
    }

    /**
     * 设置X坐标
     * @param x X坐标
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * 设置X坐标，并标记为手动定位
     * @param x X坐标
     * @param manually 是否手动定位
     */
    public void setX(double x, boolean manually) {
        this.x = x;
        if (manually) {
            this.manuallyPositioned = true;
        }
    }

    /**
     * 获取Y坐标
     * @return Y坐标
     */
    public double getY() {
        return y;
    }

    /**
     * 设置Y坐标
     * @param y Y坐标
     */
    public void setY(double y) {
        this.y = y;
        // 注意：这里不标记为手动定位，因为这个方法主要由布局算法调用
    }

    /**
     * 设置Y坐标，并标记为手动定位
     * @param y Y坐标
     * @param manually 是否手动定位
     */
    public void setY(double y, boolean manually) {
        this.y = y;
        if (manually) {
            this.manuallyPositioned = true;
        }
    }

    /**
     * 获取宽度
     * @return 宽度
     */
    public double getWidth() {
        return width;
    }

    /**
     * 设置宽度
     * @param width 宽度
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * 获取高度
     * @return 高度
     */
    public double getHeight() {
        return height;
    }

    /**
     * 设置高度
     * @param height 高度
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * 判断节点是否被选中
     * @return 是否被选中
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 设置节点选中状态
     * @param selected 选中状态
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // 标记该节点是否为中心节点
    private boolean centerNode = false;

    /**
     * 判断是否为中心节点
     * @return 是否为中心节点
     */
    public boolean isCenterNode() {
        return centerNode;
    }

    /**
     * 设置节点是否为中心节点
     * @param centerNode 是否为中心节点
     */
    public void setCenterNode(boolean centerNode) {
        this.centerNode = centerNode;
    }

    /**
     * 判断是否为叶子节点
     * @return 是否为叶子节点
     */
    public boolean isLeafNode() {
        return children.isEmpty();
    }

    /**
     * 判断节点是否被手动定位
     * @return 是否被手动定位
     */
    public boolean isManuallyPositioned() {
        return manuallyPositioned;
    }

    /**
     * 设置节点是否被手动定位
     * @param manuallyPositioned 是否被手动定位
     */
    public void setManuallyPositioned(boolean manuallyPositioned) {
        this.manuallyPositioned = manuallyPositioned;
    }

    /**
     * 获取节点形状
     * @return 节点形状
     */
    public NodeShape getShape() {
        return shape;
    }

    /**
     * 设置节点形状
     * @param shape 节点形状
     */
    public void setShape(NodeShape shape) {
        this.shape = shape;
    }

    /**
     * 获取节点大小缩放比例
     * @return 大小缩放比例
     */
    public double getSizeScale() {
        return sizeScale;
    }

    /**
     * 设置节点大小缩放比例
     * @param sizeScale 大小缩放比例
     */
    public void setSizeScale(double sizeScale) {
        this.sizeScale = sizeScale;
        // 调整宽高
        this.width = this.width * sizeScale;
        this.height = this.height * sizeScale;
    }

    /**
     * 获取节点字体大小
     * @return 字体大小
     */
    public double getFontSize() {
        return fontSize;
    }

    /**
     * 设置节点字体大小
     * @param fontSize 字体大小
     */
    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * 获取节点颜色
     * @return 节点颜色
     */
    public javafx.scene.paint.Color getColor() {
        return color;
    }

    /**
     * 设置节点颜色
     * @param color 节点颜色
     */
    public void setColor(javafx.scene.paint.Color color) {
        this.color = color;
    }

    /**
     * 获取连接线样式
     * @return 连接线样式
     */
    public LineStyle getLineStyle() {
        return lineStyle;
    }

    /**
     * 设置连接线样式
     * @param lineStyle 连接线样式
     */
    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }

    /**
     * 获取节点深度（从中心节点到当前节点的距离）
     * @return 节点深度
     */
    public int getDepth() {
        if (parent == null) {
            return 0;
        }
        return parent.getDepth() + 1;
    }

    /**
     * 获取节点及其所有子节点的总数
     * @return 节点总数
     */
    public int getTotalNodeCount() {
        int count = 1; // 当前节点
        for (MindMapNode child : children) {
            count += child.getTotalNodeCount();
        }
        return count;
    }

    /**
     * 获取节点及其所有子节点的总高度
     * @return 总高度
     */
    public double getTotalHeight() {
        if (isLeafNode()) {
            return height;
        }

        double totalHeight = 0;
        for (MindMapNode child : children) {
            totalHeight += child.getTotalHeight();
        }
        return Math.max(height, totalHeight);
    }

    @Override
    public String toString() {
        return text;
    }
}
