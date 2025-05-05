package com.example.demo.view;

import com.example.demo.layout.LayoutStrategy;
import com.example.demo.model.MindMap;
import com.example.demo.model.MindMapNode;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.Cursor;

import com.example.demo.model.MindMapNode.NodeShape;

import java.util.ArrayList;
import java.util.List;

/**
 * 思维导图绘图区视图
 */
public class MindMapView extends Pane {

    private MindMap mindMap;
    private LayoutStrategy layoutStrategy;
    private MindMapNode selectedNode;
    private NodeClickListener nodeClickListener;

    // 拖拽相关变量
    private MindMapNode draggedNode;
    private double dragStartX;
    private double dragStartY;
    private double nodeStartX;
    private double nodeStartY;

    // 右键菜单
    private ContextMenu contextMenu;

    // 控制器实例
    private Object controller;

    // 节点样式常量
    private static final double NODE_MIN_WIDTH = 120;
    private static final double NODE_HEIGHT = 40;
    private static final double NODE_PADDING = 15;
    private static final double NODE_CORNER_RADIUS = 6;
    private static final Color CENTER_NODE_COLOR = Color.web("#339af0");
    private static final Color NORMAL_NODE_COLOR = Color.web("#4dabf7");
    private static final Color SELECTED_NODE_COLOR = Color.web("#fa5252");
    private static final Color LINE_COLOR = Color.web("#adb5bd");
    private static final double LINE_WIDTH = 2.0;
    private static final String FONT_FAMILY = "Segoe UI";

    /**
     * 创建思维导图视图
     */
    public MindMapView() {
        // 设置鼠标点击事件
        setOnMouseClicked(this::handleMouseClick);
        // 添加鼠标拖拽事件
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);

        // 初始化右键菜单
        initContextMenu();
    }

    /**
     * 初始化右键菜单
     */
    private void initContextMenu() {
        contextMenu = new ContextMenu();

        // 注意：不要在这里设置鼠标点击事件处理器，因为它会覆盖之前设置的事件处理器

        // 添加子节点
        MenuItem addChildItem = new MenuItem("添加子节点");
        addChildItem.setOnAction(event -> {
            if (selectedNode != null && nodeClickListener != null) {
                // 先通知监听器节点被点击
                nodeClickListener.onNodeClicked(selectedNode);
                // 直接调用控制器的addChildNode方法
                javafx.application.Platform.runLater(() -> {
                    try {
                        // 使用反射调用控制器的addChildNode方法
                        java.lang.reflect.Method method = getController().getClass().getDeclaredMethod("addChildNode");
                        method.setAccessible(true);
                        method.invoke(getController());
                    } catch (Exception e) {
                        System.err.println("Error calling addChildNode: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        });

        // 添加兄弟节点
        MenuItem addSiblingItem = new MenuItem("添加兄弟节点");
        addSiblingItem.setOnAction(event -> {
            if (selectedNode != null && !selectedNode.isCenterNode() && nodeClickListener != null) {
                nodeClickListener.onNodeClicked(selectedNode);
                javafx.application.Platform.runLater(() -> {
                    try {
                        // 使用反射调用控制器的addSiblingNode方法
                        java.lang.reflect.Method method = getController().getClass().getDeclaredMethod("addSiblingNode");
                        method.setAccessible(true);
                        method.invoke(getController());
                    } catch (Exception e) {
                        System.err.println("Error calling addSiblingNode: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        });

        // 编辑文本
        MenuItem editTextItem = new MenuItem("编辑文本");
        editTextItem.setOnAction(event -> {
            if (selectedNode != null && nodeClickListener != null) {
                nodeClickListener.onNodeClicked(selectedNode);
                javafx.application.Platform.runLater(() -> {
                    try {
                        // 使用反射调用控制器的editNodeText方法
                        java.lang.reflect.Method method = getController().getClass().getDeclaredMethod("editNodeText");
                        method.setAccessible(true);
                        method.invoke(getController());
                    } catch (Exception e) {
                        System.err.println("Error calling editNodeText: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        });

        // 更改形状
        MenuItem changeShapeItem = new MenuItem("更改形状");
        changeShapeItem.setOnAction(event -> {
            if (selectedNode != null && nodeClickListener != null) {
                nodeClickListener.onNodeClicked(selectedNode);
                javafx.application.Platform.runLater(() -> {
                    try {
                        // 使用反射调用控制器的changeNodeShape方法
                        java.lang.reflect.Method method = getController().getClass().getDeclaredMethod("changeNodeShape");
                        method.setAccessible(true);
                        method.invoke(getController());
                    } catch (Exception e) {
                        System.err.println("Error calling changeNodeShape: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        });

        // 更改大小
        MenuItem changeSizeItem = new MenuItem("更改大小");
        changeSizeItem.setOnAction(event -> {
            if (selectedNode != null && nodeClickListener != null) {
                nodeClickListener.onNodeClicked(selectedNode);
                javafx.application.Platform.runLater(() -> {
                    try {
                        // 使用反射调用控制器的changeNodeSize方法
                        java.lang.reflect.Method method = getController().getClass().getDeclaredMethod("changeNodeSize");
                        method.setAccessible(true);
                        method.invoke(getController());
                    } catch (Exception e) {
                        System.err.println("Error calling changeNodeSize: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        });

        // 更改字体
        MenuItem changeFontItem = new MenuItem("更改字体");
        changeFontItem.setOnAction(event -> {
            if (selectedNode != null && nodeClickListener != null) {
                nodeClickListener.onNodeClicked(selectedNode);
                javafx.application.Platform.runLater(() -> {
                    try {
                        // 使用反射调用控制器的changeNodeFont方法
                        java.lang.reflect.Method method = getController().getClass().getDeclaredMethod("changeNodeFont");
                        method.setAccessible(true);
                        method.invoke(getController());
                    } catch (Exception e) {
                        System.err.println("Error calling changeNodeFont: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        });

        // 删除节点
        MenuItem deleteItem = new MenuItem("删除节点");
        deleteItem.setOnAction(event -> {
            if (selectedNode != null && !selectedNode.isCenterNode() && nodeClickListener != null) {
                nodeClickListener.onNodeClicked(selectedNode);
                javafx.application.Platform.runLater(() -> {
                    try {
                        // 使用反射调用控制器的deleteNode方法
                        java.lang.reflect.Method method = getController().getClass().getDeclaredMethod("deleteNode");
                        method.setAccessible(true);
                        method.invoke(getController());
                    } catch (Exception e) {
                        System.err.println("Error calling deleteNode: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        });

        // 添加菜单项
        contextMenu.getItems().addAll(
                addChildItem,
                addSiblingItem,
                new javafx.scene.control.SeparatorMenuItem(),
                editTextItem,
                changeShapeItem,
                changeSizeItem,
                changeFontItem,
                new javafx.scene.control.SeparatorMenuItem(),
                deleteItem
        );
    }

    /**
     * 设置思维导图
     * @param mindMap 思维导图
     */
    public void setMindMap(MindMap mindMap) {
        this.mindMap = mindMap;
        this.selectedNode = null;
        updateNodeSizes();
        applyLayout();
        draw();
    }

    /**
     * 设置布局策略
     * @param layoutStrategy 布局策略
     */
    public void setLayoutStrategy(LayoutStrategy layoutStrategy) {
        this.layoutStrategy = layoutStrategy;
        if (mindMap != null) {
            applyLayout();
            draw();
        }
    }

    /**
     * 设置节点点击监听器
     * @param listener 监听器
     */
    public void setNodeClickListener(NodeClickListener listener) {
        this.nodeClickListener = listener;
    }

    /**
     * 获取选中的节点
     * @return 选中的节点
     */
    public MindMapNode getSelectedNode() {
        return selectedNode;
    }

    /**
     * 设置选中的节点
     * @param node 要选中的节点
     */
    public void setSelectedNode(MindMapNode node) {
        this.selectedNode = node;
    }

    /**
     * 设置控制器实例
     * @param controller 控制器实例
     */
    public void setController(Object controller) {
        this.controller = controller;
    }

    /**
     * 获取控制器实例
     * @return 控制器实例
     */
    public Object getController() {
        return controller;
    }

    /**
     * 应用布局
     */
    public void applyLayout() {
        if (mindMap != null && layoutStrategy != null) {
            // 获取画布尺寸，如果画布尚未初始化，使用父容器的尺寸
            double width = getWidth() > 0 ? getWidth() : (getParent() != null ? getParent().getBoundsInLocal().getWidth() : 800);
            double height = getHeight() > 0 ? getHeight() : (getParent() != null ? getParent().getBoundsInLocal().getHeight() : 600);

            // 应用布局
            layoutStrategy.applyLayout(mindMap, width, height);

            // 输出调试信息
            System.out.println("Applying layout: " + layoutStrategy.getName());
            System.out.println("Canvas size: " + width + "x" + height);
        }
    }

    /**
     * 绘制思维导图
     */
    public void draw() {
        System.out.println("MindMapView.draw() called");
        getChildren().clear();

        if (mindMap == null) {
            System.out.println("Cannot draw: mind map is null");
            return;
        }

        // 创建画布
        double width = getWidth() > 0 ? getWidth() : (getParent() != null ? getParent().getBoundsInLocal().getWidth() : 800);
        double height = getHeight() > 0 ? getHeight() : (getParent() != null ? getParent().getBoundsInLocal().getHeight() : 600);

        // 创建两个画布，一个用于连线，一个用于节点
        // 这样可以确保节点始终在连线上方显示
        Canvas linesCanvas = new Canvas(width, height);
        GraphicsContext linesGc = linesCanvas.getGraphicsContext2D();
        getChildren().add(linesCanvas); // 先添加连线画布

        System.out.println("Canvas created with size: " + width + "x" + height);

        // 绘制所有节点的连线，包括根节点和自由节点
        for (MindMapNode node : mindMap.getAllNodes()) {
            // 如果节点有子节点，则绘制连线
            if (!node.getChildren().isEmpty()) {
                drawConnections(linesGc, node);
            }
        }

        // 绘制节点 - 直接添加到容器中，而不是画在画布上
        // 这样可以确保节点始终在连线上方显示
        List<MindMapNode> allNodes = new ArrayList<>(mindMap.getAllNodes());
        System.out.println("Drawing " + allNodes.size() + " nodes");

        for (MindMapNode node : allNodes) {
            drawNode(node);
            System.out.println("Node " + node.getText() + " drawn at position: (" + node.getX() + ", " + node.getY() + ")");
        }

        System.out.println("MindMapView.draw() completed");
    }

    /**
     * 绘制节点之间的连线
     * @param gc 图形上下文
     * @param node 当前节点
     */
    private void drawConnections(GraphicsContext gc, MindMapNode node) {
        gc.setStroke(LINE_COLOR);
        gc.setLineWidth(LINE_WIDTH);

        for (MindMapNode child : node.getChildren()) {
            // 计算节点中心点（用于计算方向）
            double nodeCenterX = node.getX() + node.getWidth() / 2;
            double nodeCenterY = node.getY() + node.getHeight() / 2;
            double childCenterX = child.getX() + child.getWidth() / 2;
            double childCenterY = child.getY() + child.getHeight() / 2;

            // 计算节点之间的方向向量
            double dirX = childCenterX - nodeCenterX;
            double dirY = childCenterY - nodeCenterY;

            // 归一化方向向量
            double length = Math.sqrt(dirX * dirX + dirY * dirY);
            if (length < 0.001) {
                // 防止除以0
                dirX = 1.0;
                dirY = 0.0;
            } else {
                dirX /= length;
                dirY /= length;
            }

            // 计算连线的起点（父节点边缘）
            double startX, startY;

            // 根据节点形状计算起点
            if (node.getShape() == MindMapNode.NodeShape.ELLIPSE) {
                // 椭圆形节点的边缘点
                double radiusX = node.getWidth() / 2;
                double radiusY = node.getHeight() / 2;
                double angle = Math.atan2(dirY, dirX);
                startX = nodeCenterX + radiusX * Math.cos(angle);
                startY = nodeCenterY + radiusY * Math.sin(angle);
            } else if (node.getShape() == MindMapNode.NodeShape.DIAMOND || node.getShape() == MindMapNode.NodeShape.HEXAGON) {
                // 菱形或六边形节点的边缘点
                // 简化处理，使用矩形边缘的计算方式
                if (Math.abs(dirX) > Math.abs(dirY)) {
                    // 水平方向为主
                    startX = nodeCenterX + (dirX > 0 ? node.getWidth() / 2 : -node.getWidth() / 2);
                    startY = nodeCenterY + dirY * (node.getWidth() / 2) / Math.abs(dirX);
                } else {
                    // 垂直方向为主
                    startX = nodeCenterX + dirX * (node.getHeight() / 2) / Math.abs(dirY);
                    startY = nodeCenterY + (dirY > 0 ? node.getHeight() / 2 : -node.getHeight() / 2);
                }
            } else {
                // 矩形或圆角矩形
                // 计算与边缘的交点
                double halfWidth = node.getWidth() / 2;
                double halfHeight = node.getHeight() / 2;

                // 计算直线方程 y = slope * x + b
                double slope = dirY / dirX; // 注意除零问题

                if (Math.abs(dirX) < 0.001) {
                    // 垂直线
                    startX = nodeCenterX;
                    startY = nodeCenterY + (dirY > 0 ? halfHeight : -halfHeight);
                } else if (Math.abs(dirY) < 0.001) {
                    // 水平线
                    startX = nodeCenterX + (dirX > 0 ? halfWidth : -halfWidth);
                    startY = nodeCenterY;
                } else {
                    // 斜线
                    // 计算与矩形边的交点
                    double xIntersectTop = (nodeCenterY - halfHeight - (nodeCenterY - slope * nodeCenterX)) / slope;
                    double xIntersectBottom = (nodeCenterY + halfHeight - (nodeCenterY - slope * nodeCenterX)) / slope;
                    double yIntersectLeft = slope * (nodeCenterX - halfWidth) + (nodeCenterY - slope * nodeCenterX);
                    double yIntersectRight = slope * (nodeCenterX + halfWidth) + (nodeCenterY - slope * nodeCenterX);

                    // 检查交点是否在矩形边上
                    if (dirX > 0 && Math.abs(xIntersectTop - nodeCenterX) <= halfWidth) {
                        // 上边交点
                        startX = xIntersectTop;
                        startY = nodeCenterY - halfHeight;
                    } else if (dirX < 0 && Math.abs(xIntersectBottom - nodeCenterX) <= halfWidth) {
                        // 下边交点
                        startX = xIntersectBottom;
                        startY = nodeCenterY + halfHeight;
                    } else if (dirY > 0 && Math.abs(yIntersectLeft - nodeCenterY) <= halfHeight) {
                        // 左边交点
                        startX = nodeCenterX - halfWidth;
                        startY = yIntersectLeft;
                    } else {
                        // 右边交点
                        startX = nodeCenterX + halfWidth;
                        startY = yIntersectRight;
                    }
                }
            }

            // 计算连线的终点（子节点边缘）
            double endX, endY;

            // 根据节点形状计算终点
            if (child.getShape() == MindMapNode.NodeShape.ELLIPSE) {
                // 椭圆形节点的边缘点
                double radiusX = child.getWidth() / 2;
                double radiusY = child.getHeight() / 2;
                double angle = Math.atan2(-dirY, -dirX); // 注意方向相反
                endX = childCenterX + radiusX * Math.cos(angle);
                endY = childCenterY + radiusY * Math.sin(angle);
            } else if (child.getShape() == MindMapNode.NodeShape.DIAMOND || child.getShape() == MindMapNode.NodeShape.HEXAGON) {
                // 菱形或六边形节点的边缘点
                // 简化处理，使用矩形边缘的计算方式
                if (Math.abs(dirX) > Math.abs(dirY)) {
                    // 水平方向为主
                    endX = childCenterX + (dirX < 0 ? child.getWidth() / 2 : -child.getWidth() / 2);
                    endY = childCenterY - dirY * (child.getWidth() / 2) / Math.abs(dirX);
                } else {
                    // 垂直方向为主
                    endX = childCenterX - dirX * (child.getHeight() / 2) / Math.abs(dirY);
                    endY = childCenterY + (dirY < 0 ? child.getHeight() / 2 : -child.getHeight() / 2);
                }
            } else {
                // 矩形或圆角矩形
                // 计算与边缘的交点
                double halfWidth = child.getWidth() / 2;
                double halfHeight = child.getHeight() / 2;

                // 计算直线方程 y = slope * x + b
                // 注意这里的方向是从子节点到父节点，所以方向相反
                double reverseX = -dirX;
                double reverseY = -dirY;
                double slope = reverseY / reverseX; // 注意除零问题

                if (Math.abs(reverseX) < 0.001) {
                    // 垂直线
                    endX = childCenterX;
                    endY = childCenterY + (reverseY > 0 ? halfHeight : -halfHeight);
                } else if (Math.abs(reverseY) < 0.001) {
                    // 水平线
                    endX = childCenterX + (reverseX > 0 ? halfWidth : -halfWidth);
                    endY = childCenterY;
                } else {
                    // 斜线
                    // 计算与矩形边的交点
                    double xIntersectTop = (childCenterY - halfHeight - (childCenterY - slope * childCenterX)) / slope;
                    double xIntersectBottom = (childCenterY + halfHeight - (childCenterY - slope * childCenterX)) / slope;
                    double yIntersectLeft = slope * (childCenterX - halfWidth) + (childCenterY - slope * childCenterX);
                    double yIntersectRight = slope * (childCenterX + halfWidth) + (childCenterY - slope * childCenterX);

                    // 检查交点是否在矩形边上
                    if (reverseX > 0 && Math.abs(xIntersectTop - childCenterX) <= halfWidth) {
                        // 上边交点
                        endX = xIntersectTop;
                        endY = childCenterY - halfHeight;
                    } else if (reverseX < 0 && Math.abs(xIntersectBottom - childCenterX) <= halfWidth) {
                        // 下边交点
                        endX = xIntersectBottom;
                        endY = childCenterY + halfHeight;
                    } else if (reverseY > 0 && Math.abs(yIntersectLeft - childCenterY) <= halfHeight) {
                        // 左边交点
                        endX = childCenterX - halfWidth;
                        endY = yIntersectLeft;
                    } else {
                        // 右边交点
                        endX = childCenterX + halfWidth;
                        endY = yIntersectRight;
                    }
                }
            }

            // 设置线条样式
            gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
            gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);

            // 根据节点的线条样式设置虚线模式
            switch (child.getLineStyle()) {
                case DASHED:
                case ARROW_DASHED:
                    // 虚线模式
                    gc.setLineDashes(5, 5);
                    break;
                default:
                    // 实线模式
                    gc.setLineDashes(null);
                    break;
            }

            // 计算控制点，创建优雅的曲线
            double dx = Math.abs(endX - startX);
            double dy = Math.abs(endY - startY);
            double distance = Math.sqrt(dx * dx + dy * dy);

            // 曲线强度与距离成正比
            double curveStrength = Math.min(distance * 0.2, 50);

            // 绘制简洁的连线
            gc.beginPath();
            gc.moveTo(startX, startY);

            // 根据节点相对位置决定曲线方向
            double controlX1, controlY1, controlX2, controlY2;
            if (Math.abs(endX - startX) > Math.abs(endY - startY)) {
                // 水平方向距离更大，使用水平控制点
                double midX = (startX + endX) / 2;
                controlX1 = midX;
                controlY1 = startY;
                controlX2 = midX;
                controlY2 = endY;
            } else {
                // 垂直方向距离更大，使用垂直控制点
                double midY = (startY + endY) / 2;
                controlX1 = startX;
                controlY1 = midY;
                controlX2 = endX;
                controlY2 = midY;
            }

            gc.bezierCurveTo(controlX1, controlY1, controlX2, controlY2, endX, endY);
            gc.stroke();

            // 如果是箭头样式，绘制箭头
            if (child.getLineStyle() == MindMapNode.LineStyle.ARROW_SOLID ||
                child.getLineStyle() == MindMapNode.LineStyle.ARROW_DASHED) {

                // 计算箭头方向 - 使用已经计算好的方向向量
                double arrowSize = 10.0;

                // 箭头方向与连线方向相反
                double arrowDirX = -dirX;
                double arrowDirY = -dirY;

                // 计算箭头的两个端点
                double arrowAngle = Math.PI / 6; // 30度角

                double ax1 = endX + arrowSize * (arrowDirX * Math.cos(arrowAngle) + arrowDirY * Math.sin(arrowAngle));
                double ay1 = endY + arrowSize * (arrowDirY * Math.cos(arrowAngle) - arrowDirX * Math.sin(arrowAngle));

                double ax2 = endX + arrowSize * (arrowDirX * Math.cos(arrowAngle) - arrowDirY * Math.sin(arrowAngle));
                double ay2 = endY + arrowSize * (arrowDirY * Math.cos(arrowAngle) + arrowDirX * Math.sin(arrowAngle));

                // 绘制箭头
                gc.beginPath();
                gc.moveTo(endX, endY);
                gc.lineTo(ax1, ay1);
                gc.lineTo(ax2, ay2);
                gc.closePath();
                gc.fill();
            }

            // 递归绘制子节点的连线
            drawConnections(gc, child);
        }
    }

    /**
     * 绘制单个节点
     * @param node 要绘制的节点
     */
    private void drawNode(MindMapNode node) {
        // 选择基础颜色
        Color baseColor;
        if (node.isSelected()) {
            // 选中状态使用高亮颜色
            baseColor = SELECTED_NODE_COLOR;
        } else {
            // 使用节点自定义颜色
            baseColor = node.getColor();
        }

        // 创建节点形状
        Shape shape = createNodeShape(node);

        // 设置节点填充颜色
        shape.setFill(baseColor);
        shape.setStroke(baseColor.darker());
        shape.setStrokeWidth(1);

        // 添加阴影效果，使节点看起来有浅浅的浮动感
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(4.0);
        dropShadow.setOffsetX(0.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.2));
        shape.setEffect(dropShadow);

        // 创建节点文本
        Text text = new Text(node.getText());
        text.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, node.getFontSize()));
        text.setFill(Color.WHITE);
        text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER); // 设置文本居中对齐

        // 计算文本位置，使其居中
        double textWidth = text.getBoundsInLocal().getWidth();
        double textHeight = text.getBoundsInLocal().getHeight();

        // 确保文本在节点中心
        text.setX(node.getX() + (node.getWidth() - textWidth) / 2);
        text.setY(node.getY() + (node.getHeight() + textHeight) / 2);

        // 将节点添加到视图
        getChildren().addAll(shape, text);
    }

    /**
     * 根据节点形状创建对应的图形
     * @param node 节点
     * @return 图形
     */
    private Shape createNodeShape(MindMapNode node) {
        double x = node.getX();
        double y = node.getY();
        double width = node.getWidth();
        double height = node.getHeight();

        switch (node.getShape()) {
            case RECTANGLE:
                // 矩形
                return new Rectangle(x, y, width, height);

            case ROUNDED_RECTANGLE:
                // 圆角矩形
                Rectangle roundedRect = new Rectangle(x, y, width, height);
                roundedRect.setArcWidth(NODE_CORNER_RADIUS);
                roundedRect.setArcHeight(NODE_CORNER_RADIUS);
                return roundedRect;

            case ELLIPSE:
                // 椭圆形
                return new Ellipse(
                        x + width / 2, // 中心点X
                        y + height / 2, // 中心点Y
                        width / 2, // X半径
                        height / 2  // Y半径
                );

            case DIAMOND:
                // 菱形
                Polygon diamond = new Polygon();
                diamond.getPoints().addAll(new Double[]{
                    x + width / 2, y, // 顶点
                    x + width, y + height / 2, // 右点
                    x + width / 2, y + height, // 底点
                    x, y + height / 2  // 左点
                });
                return diamond;

            case HEXAGON:
                // 六边形
                Polygon hexagon = new Polygon();
                double sixthWidth = width / 6;
                hexagon.getPoints().addAll(new Double[]{
                    x + sixthWidth, y, // 左上
                    x + width - sixthWidth, y, // 右上
                    x + width, y + height / 2, // 右中
                    x + width - sixthWidth, y + height, // 右下
                    x + sixthWidth, y + height, // 左下
                    x, y + height / 2  // 左中
                });
                return hexagon;

            default:
                // 默认为矩形
                return new Rectangle(x, y, width, height);
        }
    }

    /**
     * 更新所有节点的大小
     */
    private void updateNodeSizes() {
        if (mindMap == null) {
            return;
        }

        for (MindMapNode node : mindMap.getAllNodes()) {
            updateNodeSize(node);
        }
    }

    /**
     * 更新单个节点的大小
     * @param node 要更新的节点
     */
    public void updateNodeSize(MindMapNode node) {
        // 创建临时文本对象来计算文本宽度
        Text text = new Text(node.getText());
        text.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, node.getFontSize()));

        // 计算节点宽度（文本宽度加上内边距）
        double baseWidth = Math.max(NODE_MIN_WIDTH, text.getBoundsInLocal().getWidth() + 2 * NODE_PADDING);

        // 考虑缩放比例
        node.setWidth(baseWidth * node.getSizeScale());
        node.setHeight(NODE_HEIGHT * node.getSizeScale());
    }

    /**
     * 处理鼠标点击事件
     * @param event 鼠标事件
     */
    private void handleMouseClick(MouseEvent event) {
        System.out.println("Mouse clicked: " + event.getButton());

        // 如果是拖拽操作结束后的点击，不处理
        if (mindMap == null || draggedNode != null) {
            return;
        }

        // 如果右键菜单正在显示，先隐藏它
        if (contextMenu.isShowing()) {
            contextMenu.hide();
        }

        // 查找点击的节点
        MindMapNode clickedNode = findNodeAt(event.getX(), event.getY());

        // 如果在连接模式下点击了节点，则处理连接操作
        if (clickedNode != null && controller != null) {
            try {
                // 使用反射调用控制器的handleConnectionModeClick方法
                java.lang.reflect.Method method = controller.getClass().getDeclaredMethod("handleConnectionModeClick", MindMapNode.class);
                method.setAccessible(true);
                Boolean handled = (Boolean) method.invoke(controller, clickedNode);

                // 如果连接操作已处理，则返回
                if (handled != null && handled) {
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error calling handleConnectionModeClick: " + e.getMessage());
            }
        }

        // 清除所有选中状态
        mindMap.clearAllSelections();

        // 设置新的选中节点
        if (clickedNode != null) {
            clickedNode.setSelected(true);
            selectedNode = clickedNode;

            // 如果是右键，显示右键菜单
            if (event.getButton() == MouseButton.SECONDARY) {
                System.out.println("Showing context menu for node: " + clickedNode.getText());

                // 根据节点类型启用/禁用菜单项
                for (MenuItem item : contextMenu.getItems()) {
                    if (item instanceof javafx.scene.control.SeparatorMenuItem) {
                        continue;
                    }

                    String text = item.getText();
                    if (text.equals("添加兄弟节点") || text.equals("删除节点")) {
                        item.setDisable(clickedNode.isCenterNode());
                    }
                }

                // 显示右键菜单
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
            }
        } else {
            selectedNode = null;
        }

        // 通知监听器
        if (nodeClickListener != null) {
            nodeClickListener.onNodeClicked(selectedNode);
        }

        // 重绘
        draw();
    }

    /**
     * 处理鼠标按下事件
     * @param event 鼠标事件
     */
    private void handleMousePressed(MouseEvent event) {
        if (mindMap == null) {
            return;
        }

        // 查找点击的节点
        MindMapNode clickedNode = findNodeAt(event.getX(), event.getY());

        if (clickedNode != null) {
            // 开始拖拽
            draggedNode = clickedNode;
            dragStartX = event.getX();
            dragStartY = event.getY();
            nodeStartX = clickedNode.getX();
            nodeStartY = clickedNode.getY();

            // 更改鼠标样式
            setCursor(Cursor.MOVE);
        }
    }

    /**
     * 处理鼠标拖拽事件
     * @param event 鼠标事件
     */
    private void handleMouseDragged(MouseEvent event) {
        if (draggedNode == null) {
            return;
        }

        // 计算拖拽的偏移量
        double offsetX = event.getX() - dragStartX;
        double offsetY = event.getY() - dragStartY;

        // 更新节点位置，并标记为手动定位
        draggedNode.setX(nodeStartX + offsetX, true);
        draggedNode.setY(nodeStartY + offsetY, true);

        // 重绘
        draw();
    }

    /**
     * 处理鼠标释放事件
     * @param event 鼠标事件
     */
    private void handleMouseReleased(MouseEvent event) {
        if (draggedNode == null) {
            return;
        }

        // 结束拖拽
        draggedNode = null;

        // 恢复鼠标样式
        setCursor(Cursor.DEFAULT);

        // 标记思维导图为已修改
        if (mindMap != null) {
            mindMap.setModified(true);
        }
    }

    /**
     * 查找指定坐标处的节点
     * @param x X坐标
     * @param y Y坐标
     * @return 找到的节点，如果没有则返回null
     */
    private MindMapNode findNodeAt(double x, double y) {
        if (mindMap == null) {
            return null;
        }

        // 从所有节点中查找
        for (MindMapNode node : mindMap.getAllNodes()) {
            if (x >= node.getX() && x <= node.getX() + node.getWidth() &&
                    y >= node.getY() && y <= node.getY() + node.getHeight()) {
                return node;
            }
        }

        return null;
    }

    /**
     * 节点点击监听器接口
     */
    public interface NodeClickListener {
        /**
         * 当节点被点击时调用
         * @param node 被点击的节点，如果点击空白区域则为null
         */
        void onNodeClicked(MindMapNode node);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        // 当容器大小改变时，重新应用布局并绘制
        if (mindMap != null && layoutStrategy != null) {
            applyLayout();
            draw();
        }
    }


}
