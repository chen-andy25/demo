package com.example.demo.controller;

import com.example.demo.layout.*;
import com.example.demo.model.MindMap;
import com.example.demo.model.MindMapNode;
import com.example.demo.theme.Theme;
import com.example.demo.theme.ThemeManager;
import com.example.demo.util.FileManager;
import com.example.demo.util.ImageExporter;
import com.example.demo.view.MindMapTreeView;
import com.example.demo.view.MindMapView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.SplitPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 思维导图控制器
 */
public class MindMapController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label titleLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button connectionModeButton;

    @FXML
    private Button exitConnectionModeButton;

    @FXML
    private javafx.scene.layout.Pane mindMapViewContainer;

    @FXML
    private javafx.scene.control.TreeView<MindMapNode> treeViewControl;

    private MindMapView mindMapView;
    private MindMapTreeView treeView;

    @FXML
    private MenuButton layoutMenuButton;

    @FXML
    private MenuButton themeMenuButton; // 主题菜单按钮

    // 布局菜单项已在代码中动态创建

    @FXML
    private MenuButton addNodeMenuButton; // 添加节点菜单按钮

    @FXML
    private MenuButton connectionMenuButton; // 连接操作菜单按钮

    @FXML
    private MenuButton editNodeMenuButton; // 编辑节点菜单按钮

    @FXML
    private Button deleteNodeButton; // 删除节点按钮

    private MindMap mindMap;
    private LayoutStrategy currentLayout;

    /**
     * 初始化控制器
     */
    @FXML
    public void initialize() {
        // 创建自定义视图组件
        mindMapView = new MindMapView();
        mindMapViewContainer.getChildren().add(mindMapView);

        // 绑定大小
        mindMapView.prefWidthProperty().bind(mindMapViewContainer.widthProperty());
        mindMapView.prefHeightProperty().bind(mindMapViewContainer.heightProperty());

        // 创建树形视图
        treeView = new MindMapTreeView();
        treeView.setRoot(new TreeItem<>());

        // 使用Platform.runLater确保组件已经完全加载
        Platform.runLater(() -> {
            try {
                // 替换FXML中的TreeView
                // 查找SplitPane父容器
                javafx.scene.Parent parent = mindMapViewContainer.getParent();
                while (parent != null && !(parent instanceof SplitPane)) {
                    parent = parent.getParent();
                }

                if (parent instanceof SplitPane) {
                    SplitPane splitPane = (SplitPane) parent;
                    // 找到treeViewControl在SplitPane中的索引
                    int treeViewIndex = -1;
                    for (int i = 0; i < splitPane.getItems().size(); i++) {
                        if (splitPane.getItems().get(i) == treeViewControl) {
                            treeViewIndex = i;
                            break;
                        }
                    }

                    if (treeViewIndex >= 0) {
                        splitPane.getItems().set(treeViewIndex, treeView);
                    } else {
                        // 如果找不到treeViewControl，直接添加到SplitPane
                        splitPane.getItems().add(treeView);
                    }
                } else {
                    throw new RuntimeException("无法找到SplitPane父容器");
                }

                // 初始化布局策略
                currentLayout = new LeftForceLayout();

                // 设置节点点击监听器
                mindMapView.setNodeClickListener(this::handleNodeClicked);
                treeView.setNodeSelectListener(this::handleNodeSelected);

                // 设置控制器实例
                mindMapView.setController(this);

                // 注释掉右键菜单事件处理，因为我们还没有实现MindMapEvent类
                // mindMapView.addEventHandler(MindMapEvent.ADD_CHILD_NODE, this::handleAddChildNode);
                // mindMapView.addEventHandler(MindMapEvent.ADD_SIBLING_NODE, this::handleAddSiblingNode);
                // mindMapView.addEventHandler(MindMapEvent.CONNECT_NODE, this::handleConnectNode);
                // mindMapView.addEventHandler(MindMapEvent.DISCONNECT_NODE, this::handleDisconnectNode);
                // mindMapView.addEventHandler(MindMapEvent.EDIT_NODE_TEXT, this::handleEditNodeText);
                // mindMapView.addEventHandler(MindMapEvent.CHANGE_NODE_SHAPE, this::handleChangeNodeShape);
                // mindMapView.addEventHandler(MindMapEvent.CHANGE_NODE_SIZE, this::handleChangeNodeSize);
                // mindMapView.addEventHandler(MindMapEvent.CHANGE_NODE_FONT, this::handleChangeNodeFont);
                // mindMapView.addEventHandler(MindMapEvent.DELETE_NODE, this::handleDeleteNode);

                // 清空布局菜单项
                layoutMenuButton.getItems().clear();

                // 添加布局菜单项
                MenuItem autoLayoutItem = new MenuItem("自动布局");
                autoLayoutItem.setOnAction(event -> {
                    System.out.println("Auto layout menu item clicked");
                    setLayout(new TreeCloneLayout());
                });
                layoutMenuButton.getItems().add(autoLayoutItem);

                MenuItem leftLayoutItem = new MenuItem("左侧布局");
                leftLayoutItem.setOnAction(event -> {
                    System.out.println("Left layout menu item clicked");
                    setLayout(new LeftCloneLayout());
                });
                layoutMenuButton.getItems().add(leftLayoutItem);

                MenuItem rightLayoutItem = new MenuItem("右侧布局");
                rightLayoutItem.setOnAction(event -> {
                    System.out.println("Right layout menu item clicked");
                    setLayout(new RightCloneLayout());
                });
                layoutMenuButton.getItems().add(rightLayoutItem);

                // 初始化主题菜单
                initThemeMenu();

                // 创建新的思维导图
                createNewMindMap();

                // 更新按钮状态
                updateButtonStates(null);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("初始化失败: " + e.getMessage());
            }
        });
    }

    /**
     * 创建新的思维导图
     */
    @FXML
    public void createNewMindMap() {
        // 检查是否需要保存当前思维导图
        if (!checkSaveBeforeAction()) {
            return;
        }

        // 显示输入对话框
        TextInputDialog dialog = new TextInputDialog("中心主题");
        dialog.setTitle("新建思维导图");
        dialog.setHeaderText("请输入中心主题");
        dialog.setContentText("中心主题:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // 创建新的思维导图
            mindMap = new MindMap(result.get());

            // 更新视图
            updateViews();

            // 更新标题
            updateTitle();
        }
    }

    /**
     * 打开思维导图
     */
    @FXML
    public void openMindMap() {
        // 检查是否需要保存当前思维导图
        if (!checkSaveBeforeAction()) {
            return;
        }

        // 加载思维导图
        MindMap loadedMap = FileManager.loadMindMap(getStage());
        if (loadedMap != null) {
            mindMap = loadedMap;

            // 更新视图
            updateViews();

            // 更新标题
            updateTitle();
        }
    }

    /**
     * 保存思维导图
     */
    @FXML
    public void saveMindMap() {
        if (mindMap != null) {
            FileManager.saveMindMap(mindMap, getStage());
            updateTitle();
        }
    }

    /**
     * 另存为思维导图
     */
    @FXML
    public void saveAsMindMap() {
        if (mindMap != null) {
            FileManager.saveAsMindMap(mindMap, getStage());
            updateTitle();
        }
    }

    /**
     * 导出为JPG图像
     */
    @FXML
    public void exportAsJPG() {
        if (mindMap != null) {
            ImageExporter.exportAsJPG(mindMapView, getStage());
        }
    }

    /**
     * 导出为PNG图像
     */
    @FXML
    public void exportAsPNG() {
        if (mindMap != null) {
            ImageExporter.exportAsPNG(mindMapView, getStage());
        }
    }

    /**
     * 添加子节点
     */
    @FXML
    public void addChildNode() {
        MindMapNode selectedNode = mindMapView.getSelectedNode();
        if (selectedNode == null) {
            showAlert("请先选择一个节点");
            return;
        }

        // 显示输入对话框
        TextInputDialog dialog = new TextInputDialog("新节点");
        dialog.setTitle("添加子节点");
        dialog.setHeaderText("请输入节点内容");
        dialog.setContentText("节点内容:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // 添加子节点
            mindMap.addChildNode(selectedNode, result.get());

            // 更新视图
            updateViews();

            // 标记为已修改
            mindMap.setModified(true);
            updateTitle();
        }
    }

    /**
     * 添加兄弟节点
     */
    @FXML
    public void addSiblingNode() {
        MindMapNode selectedNode = mindMapView.getSelectedNode();
        if (selectedNode == null) {
            showAlert("请先选择一个节点");
            return;
        }

        if (selectedNode.isCenterNode()) {
            showAlert("中心节点不能添加兄弟节点");
            return;
        }

        // 显示输入对话框
        TextInputDialog dialog = new TextInputDialog("新节点");
        dialog.setTitle("添加兄弟节点");
        dialog.setHeaderText("请输入节点内容");
        dialog.setContentText("节点内容:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // 添加兄弟节点
            mindMap.addSiblingNode(selectedNode, result.get());

            // 更新视图
            updateViews();

            // 标记为已修改
            mindMap.setModified(true);
            updateTitle();
        }
    }

    /**
     * 删除节点
     */
    @FXML
    public void deleteNode() {
        MindMapNode selectedNode = mindMapView.getSelectedNode();
        if (selectedNode == null) {
            showAlert("请先选择一个节点");
            return;
        }

        if (selectedNode.isCenterNode()) {
            showAlert("不能删除中心节点");
            return;
        }

        // 显示确认对话框
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("删除节点");
        alert.setHeaderText("确认删除");
        alert.setContentText("确定要删除选中的节点及其所有子节点吗？");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 删除节点
            mindMap.deleteNode(selectedNode);

            // 更新视图
            updateViews();

            // 标记为已修改
            mindMap.setModified(true);
            updateTitle();
        }
    }

    /**
     * 添加自由节点（作为独立的“根节点”）
     */
    @FXML
    public void addFreeNode() {
        if (mindMap == null) {
            return;
        }

        // 显示输入对话框
        TextInputDialog dialog = new TextInputDialog("新主题");
        dialog.setTitle("添加新主题");
        dialog.setHeaderText("请输入新主题内容");
        dialog.setContentText("主题内容:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // 创建新节点
            MindMapNode newNode = new MindMapNode(result.get());

            // 设置节点位置（在画布中心附近）
            double canvasWidth = mindMapViewContainer.getWidth();
            double canvasHeight = mindMapViewContainer.getHeight();

            // 随机偏移，使节点不重叠
            double offsetX = Math.random() * 200 - 100;
            double offsetY = Math.random() * 200 - 100;

            newNode.setX(canvasWidth / 2 + offsetX, true);
            newNode.setY(canvasHeight / 2 + offsetY, true);

            // 使用不同的形状和颜色来区分自由节点
            newNode.setShape(MindMapNode.NodeShape.ROUNDED_RECTANGLE);

            // 更新节点大小
            mindMapView.updateNodeSize(newNode);

            // 将节点添加到思维导图
            mindMap.addFreeNode(newNode);
            System.out.println("Added free node as independent root: " + newNode.getText() + " (ID: " + newNode.getId() + ")");
            System.out.println("Total nodes after adding free node: " + mindMap.getAllNodes().size());

            // 更新视图
            updateViews();

            // 选中新创建的节点
            mindMap.clearAllSelections();
            newNode.setSelected(true);
            mindMapView.setSelectedNode(newNode);
            mindMapView.draw();

            // 标记为已修改
            mindMap.setModified(true);
            updateTitle();

            // 显示成功消息
            showInformation("新主题已创建", "新主题 '"+newNode.getText()+"' 已成功创建。您可以向其添加子节点。");
        }
    }

    // 记录连接操作的第一个节点
    private MindMapNode connectSourceNode = null;

    // 记录取消连接操作的节点
    private MindMapNode disconnectNode = null;

    // 连接模式标志
    private boolean connectionMode = false;

    /**
     * 连接节点 - 更人性化的实现
     */
    @FXML
    public void connectNodes() {
        if (mindMap == null) {
            showAlert("请先创建或打开思维导图");
            return;
        }

        // 进入连接模式
        connectionMode = true;
        connectSourceNode = null;
        statusLabel.setText("连接模式: 请先点击源节点，然后点击目标节点");

        // 更改鼠标样式以提示连接模式
        mindMapView.setCursor(javafx.scene.Cursor.CROSSHAIR);

        // 隐藏连接模式按钮，显示退出连接按钮
        connectionModeButton.setVisible(false);
        exitConnectionModeButton.setVisible(true);
    }

    /**
     * 退出连接模式
     */
    @FXML
    public void exitConnectionMode() {
        // 退出连接模式
        connectionMode = false;
        connectSourceNode = null;
        statusLabel.setText("已退出连接模式");

        // 恢复鼠标样式
        mindMapView.setCursor(javafx.scene.Cursor.DEFAULT);

        // 显示连接模式按钮，隐藏退出连接按钮
        connectionModeButton.setVisible(true);
        exitConnectionModeButton.setVisible(false);

        // 创建一个定时器，3秒后恢复状态栏
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        pause.setOnFinished(event -> statusLabel.setText("已就绪"));
        pause.play();
    }

    /**
     * 处理连接模式下的节点点击
     * @param node 被点击的节点
     * @return 是否已处理连接操作
     */
    public boolean handleConnectionModeClick(MindMapNode node) {
        if (!connectionMode || node == null) {
            return false;
        }

        if (connectSourceNode == null) {
            // 第一步：选择源节点
            connectSourceNode = node;
            statusLabel.setText("连接模式: 已选择源节点 '" + node.getText() + "'，请点击目标节点");

            // 高亮显示源节点
            mindMap.clearAllSelections();
            connectSourceNode.setSelected(true);
            mindMapView.draw();

            // 更新连接模式指示器显示当前源节点
            HBox statusBar = (HBox) rootPane.getBottom();
            if (statusBar != null) {
                // 查找连接模式指示器
                for (javafx.scene.Node n : statusBar.getChildren()) {
                    if (n instanceof HBox && n.getId() != null && n.getId().equals("connectionModeIndicator")) {
                        HBox indicatorBox = (HBox) n;

                        // 更新指示器文本
                        for (javafx.scene.Node child : indicatorBox.getChildren()) {
                            if (child instanceof Label) {
                                ((Label) child).setText("连接模式: 源节点 '" + node.getText() + "'");
                                break;
                            }
                        }

                        // 更新指示器颜色
                        indicatorBox.setStyle("-fx-background-color: #2ecc71; -fx-padding: 5; -fx-background-radius: 5;");
                        break;
                    }
                }
            }

            return true;
        } else {
            // 第二步：选择目标节点并连接
            MindMapNode targetNode = node;

            if (targetNode == connectSourceNode) {
                statusLabel.setText("连接错误: 不能连接节点到自身");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;"); // 错误提示使用红色

                // 创建一个定时器，3秒后恢复状态栏
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
                pause.setOnFinished(event -> {
                    statusLabel.setText("连接模式: 请重新选择目标节点");
                    statusLabel.setStyle("-fx-text-fill: #3498db;"); // 恢复蓝色
                });
                pause.play();

                return true;
            }

            // 检查是否会形成循环
            if (isNodeDescendant(targetNode, connectSourceNode)) {
                statusLabel.setText("连接错误: 不能连接到子节点，这会形成循环");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;"); // 错误提示使用红色

                // 创建一个定时器，3秒后恢复状态栏
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
                pause.setOnFinished(event -> {
                    statusLabel.setText("连接模式: 请重新选择目标节点");
                    statusLabel.setStyle("-fx-text-fill: #3498db;"); // 恢复蓝色
                });
                pause.play();

                return true;
            }

            // 如果目标节点已经有父节点，则需要先断开原来的连接
            if (targetNode.getParent() != null) {
                // 显示确认对话框
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("断开原有连接");
                alert.setHeaderText("目标节点已经有连接");
                alert.setContentText("是否断开原有连接并创建新连接？");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // 断开原有连接
                    MindMapNode oldParent = targetNode.getParent();
                    oldParent.removeChild(targetNode);
                } else {
                    // 用户取消了操作，但保持连接模式
                    statusLabel.setText("连接模式: 操作取消，请重新选择目标节点");
                    return true;
                }
            }

            // 创建新连接
            connectSourceNode.addChild(targetNode);

            // 更新视图
            updateViews();

            // 标记为已修改
            mindMap.setModified(true);
            updateTitle();

            // 更新连接模式指示器
            HBox statusBar = (HBox) rootPane.getBottom();
            if (statusBar != null) {
                // 查找连接模式指示器
                for (javafx.scene.Node n : statusBar.getChildren()) {
                    if (n instanceof HBox && n.getId() != null && n.getId().equals("connectionModeIndicator")) {
                        HBox indicatorBox = (HBox) n;

                        // 更新指示器文本
                        for (javafx.scene.Node child : indicatorBox.getChildren()) {
                            if (child instanceof Label) {
                                ((Label) child).setText("连接模式活跃");
                                break;
                            }
                        }

                        // 更新指示器颜色
                        indicatorBox.setStyle("-fx-background-color: #3498db; -fx-padding: 5; -fx-background-radius: 5;");
                        break;
                    }
                }
            }

            // 显示成功消息
            statusLabel.setText("连接成功: '" + connectSourceNode.getText() + "' 已连接到 '" + targetNode.getText() + "'");
            statusLabel.setStyle("-fx-text-fill: #2ecc71;"); // 成功提示使用绿色

            // 重置连接操作，但保持连接模式
            connectSourceNode = null;

            // 创建一个定时器，3秒后恢复状态栏
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
            pause.setOnFinished(event -> {
                statusLabel.setText("连接模式: 请选择新的源节点");
                statusLabel.setStyle("-fx-text-fill: #3498db;"); // 恢复蓝色
            });
            pause.play();

            return true;
        }
    }

    /**
     * 取消节点连接
     */
    @FXML
    public void disconnectNodes() {
        if (mindMap == null) {
            return;
        }

        // 获取当前选中节点
        MindMapNode selectedNode = mindMapView.getSelectedNode();
        if (selectedNode == null) {
            showAlert("请先选择要取消连接的节点");
            return;
        }

        // 如果是根节点，则取消所有连接
        if (selectedNode.isCenterNode()) {
            // 显示确认对话框
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("取消所有连接");
            alert.setHeaderText("确认取消所有连接");
            alert.setContentText("是否取消根节点的所有连接？所有子节点将成为独立节点。");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // 获取所有子节点
                List<MindMapNode> children = new ArrayList<>(selectedNode.getChildren());

                // 断开所有连接
                for (MindMapNode child : children) {
                    selectedNode.removeChild(child);

                    // 将节点设置为手动定位，保持其当前位置
                    child.setManuallyPositioned(true);
                }

                // 更新视图
                updateViews();

                // 标记为已修改
                mindMap.setModified(true);
                updateTitle();

                showInformation("取消连接成功", "所有节点连接已取消，子节点现在是独立节点。");
            }
            return;
        }

        // 获取父节点
        MindMapNode parentNode = selectedNode.getParent();
        if (parentNode == null) {
            showAlert("该节点没有连接到其他节点");
            return;
        }

        // 显示确认对话框
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("取消连接");
        alert.setHeaderText("确认取消连接");
        alert.setContentText("是否取消该节点与其父节点的连接？该节点将成为独立节点。");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 断开连接
            parentNode.removeChild(selectedNode);

            // 将节点设置为手动定位，保持其当前位置
            selectedNode.setManuallyPositioned(true);

            // 更新视图
            updateViews();

            // 标记为已修改
            mindMap.setModified(true);
            updateTitle();

            showInformation("取消连接成功", "节点连接已取消，该节点现在是一个独立节点。");
        }
    }

    /**
     * 更改节点形状
     */
    @FXML
    public void changeNodeShape() {
        if (mindMap == null) {
            return;
        }

        // 获取当前选中节点
        MindMapNode selectedNode = mindMapView.getSelectedNode();
        if (selectedNode == null) {
            showAlert("请先选择要更改形状的节点");
            return;
        }

        // 创建形状选择对话框
        ChoiceDialog<String> dialog = new ChoiceDialog<>("矩形",
                "矩形", "圆角矩形", "椭圆形", "菱形", "六边形");
        dialog.setTitle("更改节点形状");
        dialog.setHeaderText("选择节点形状");
        dialog.setContentText("请选择形状:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // 根据选择设置节点形状
            String shapeStr = result.get();
            MindMapNode.NodeShape shape;

            switch (shapeStr) {
                case "圆角矩形":
                    shape = MindMapNode.NodeShape.ROUNDED_RECTANGLE;
                    break;
                case "椭圆形":
                    shape = MindMapNode.NodeShape.ELLIPSE;
                    break;
                case "菱形":
                    shape = MindMapNode.NodeShape.DIAMOND;
                    break;
                case "六边形":
                    shape = MindMapNode.NodeShape.HEXAGON;
                    break;
                default:
                    shape = MindMapNode.NodeShape.RECTANGLE;
                    break;
            }

            // 设置节点形状
            selectedNode.setShape(shape);

            // 更新视图
            mindMapView.draw();

            // 标记为已修改
            mindMap.setModified(true);
            updateTitle();

            showInformation("形状已更改", "节点形状已更改为" + shapeStr);
        }
    }

    /**
     * 更改节点大小
     */
    @FXML
    public void changeNodeSize() {
        if (mindMap == null) {
            return;
        }

        // 获取当前选中节点
        MindMapNode selectedNode = mindMapView.getSelectedNode();
        if (selectedNode == null) {
            showAlert("请先选择要调整大小的节点");
            return;
        }

        // 创建大小选择对话框
        ChoiceDialog<String> dialog = new ChoiceDialog<>("正常",
                "很小", "小", "正常", "大", "很大");
        dialog.setTitle("调整节点大小");
        dialog.setHeaderText("选择节点大小");
        dialog.setContentText("请选择大小:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // 根据选择设置节点大小
            String sizeStr = result.get();
            double scale;

            switch (sizeStr) {
                case "很小":
                    scale = 0.6;
                    break;
                case "小":
                    scale = 0.8;
                    break;
                case "大":
                    scale = 1.2;
                    break;
                case "很大":
                    scale = 1.5;
                    break;
                default:
                    scale = 1.0;
                    break;
            }

            // 设置节点大小
            selectedNode.setSizeScale(scale);

            // 更新节点大小
            mindMapView.updateNodeSize(selectedNode);

            // 更新视图
            mindMapView.draw();

            // 标记为已修改
            mindMap.setModified(true);
            updateTitle();

            showInformation("大小已调整", "节点大小已调整为" + sizeStr);
        }
    }

    /**
     * 更改节点字体大小
     */
    @FXML
    public void changeNodeFont() {
        if (mindMap == null) {
            return;
        }

        // 获取当前选中节点
        MindMapNode selectedNode = mindMapView.getSelectedNode();
        if (selectedNode == null) {
            showAlert("请先选择要调整字体的节点");
            return;
        }

        // 创建字体大小选择对话框
        ChoiceDialog<String> dialog = new ChoiceDialog<>("正常",
                "10", "12", "14", "16", "18", "20", "24", "28", "32");
        dialog.setTitle("调整字体大小");
        dialog.setHeaderText("选择字体大小");
        dialog.setContentText("请选择字体大小:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                // 解析字体大小
                double fontSize = Double.parseDouble(result.get());

                // 设置节点字体大小
                selectedNode.setFontSize(fontSize);

                // 更新节点大小
                mindMapView.updateNodeSize(selectedNode);

                // 更新视图
                mindMapView.draw();

                // 标记为已修改
                mindMap.setModified(true);
                updateTitle();

                showInformation("字体大小已调整", "节点字体大小已调整为" + fontSize);
            } catch (NumberFormatException e) {
                showAlert("无效的字体大小");
            }
        }
    }

    /**
     * 更改线条样式
     */
    @FXML
    public void changeLineStyle() {
        if (mindMap == null) {
            showAlert("请先创建或打开思维导图");
            return;
        }

        MindMapNode selectedNode = mindMapView.getSelectedNode();
        if (selectedNode == null) {
            showAlert("请先选择一个节点");
            return;
        }

        // 创建线条样式选择对话框
        ChoiceDialog<String> dialog = new ChoiceDialog<>(
            getLineStyleName(selectedNode.getLineStyle()),
            Arrays.asList("实线", "虚线", "箭头实线", "箭头虚线")
        );
        dialog.setTitle("更改线条样式");
        dialog.setHeaderText("请选择线条样式");
        dialog.setContentText("线条样式:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // 更新节点线条样式
            MindMapNode.LineStyle newStyle = getLineStyleFromName(result.get());
            selectedNode.setLineStyle(newStyle);

            // 重绘思维导图
            mindMapView.draw();

            // 标记为已修改
            mindMap.setModified(true);
            updateTitle();

            // 显示成功消息
            statusLabel.setText("线条样式已更改为: " + result.get());
        }
    }

    /**
     * 获取线条样式名称
     * @param style 线条样式
     * @return 线条样式名称
     */
    private String getLineStyleName(MindMapNode.LineStyle style) {
        switch (style) {
            case SOLID: return "实线";
            case DASHED: return "虚线";
            case ARROW_SOLID: return "箭头实线";
            case ARROW_DASHED: return "箭头虚线";
            default: return "实线";
        }
    }

    /**
     * 根据名称获取线条样式
     * @param name 线条样式名称
     * @return 线条样式
     */
    private MindMapNode.LineStyle getLineStyleFromName(String name) {
        switch (name) {
            case "实线": return MindMapNode.LineStyle.SOLID;
            case "虚线": return MindMapNode.LineStyle.DASHED;
            case "箭头实线": return MindMapNode.LineStyle.ARROW_SOLID;
            case "箭头虚线": return MindMapNode.LineStyle.ARROW_DASHED;
            default: return MindMapNode.LineStyle.SOLID;
        }
    }

    /**
     * 编辑节点文本
     */
    @FXML
    public void editNodeText() {
        if (mindMap == null) {
            return;
        }

        // 获取当前选中节点
        MindMapNode selectedNode = mindMapView.getSelectedNode();
        if (selectedNode == null) {
            showAlert("请先选择要编辑的节点");
            return;
        }

        // 创建文本输入对话框
        TextInputDialog dialog = new TextInputDialog(selectedNode.getText());
        dialog.setTitle("编辑节点文本");
        dialog.setHeaderText("编辑节点文本");
        dialog.setContentText("请输入新的文本:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            // 设置节点文本
            selectedNode.setText(result.get().trim());

            // 更新节点大小
            mindMapView.updateNodeSize(selectedNode);

            // 更新视图
            updateViews();

            // 标记为已修改
            mindMap.setModified(true);
            updateTitle();

            showInformation("文本已更新", "节点文本已更新");
        }
    }

    /**
     * 检查一个节点是否是另一个节点的后代
     * @param node 要检查的节点
     * @param potentialAncestor 可能的祖先节点
     * @return 如果是后代则返回true
     */
    private boolean isNodeDescendant(MindMapNode node, MindMapNode potentialAncestor) {
        if (node == null) {
            return false;
        }

        // 递归检查所有子节点
        for (MindMapNode child : potentialAncestor.getChildren()) {
            if (child == node || isNodeDescendant(node, child)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 设置布局
     * @param layout 布局策略
     */
    private void setLayout(LayoutStrategy layout) {
        System.out.println("Setting layout to: " + layout.getName());

        // 检查是否选中了中心节点
        MindMapNode selectedNode = mindMapView.getSelectedNode();
        if (selectedNode == null || !selectedNode.isCenterNode()) {
            // 如果没有选中节点或选中的不是中心节点，则自动选中中心节点
            selectedNode = mindMap.getRootNode();
            mindMap.clearAllSelections();
            selectedNode.setSelected(true);
            mindMapView.setSelectedNode(selectedNode);

            // 通知用户
            statusLabel.setText("已自动选中中心节点并应用" + layout.getName());
        }

        currentLayout = layout;
        mindMapView.setLayoutStrategy(layout);

        // 强制重新布局和绘制
        System.out.println("Forcing layout application...");
        mindMapView.applyLayout();
        mindMapView.draw();
        System.out.println("Layout applied and redrawn.");

        // 更新布局菜单按钮文本
        layoutMenuButton.setText(layout.getName());
        System.out.println("Layout menu button text updated to: " + layout.getName());

        // 标记思维导图为已修改
        if (mindMap != null) {
            mindMap.setModified(true);
            updateTitle();
            System.out.println("Mind map marked as modified.");
        } else {
            System.out.println("WARNING: Mind map is null!");
        }
    }

    /**
     * 初始化主题菜单
     */
    private void initThemeMenu() {
        // 清空主题菜单项
        themeMenuButton.getItems().clear();

        // 获取主题管理器
        ThemeManager themeManager = ThemeManager.getInstance();

        // 添加主题菜单项
        for (Theme theme : themeManager.getThemes()) {
            MenuItem themeItem = new MenuItem(theme.getName());

            // 设置菜单项图标，显示主题的颜色预览
            javafx.scene.layout.HBox colorPreview = new javafx.scene.layout.HBox();
            colorPreview.setSpacing(2);

            // 添加颜色预览
            addColorBox(colorPreview, theme.getRed());
            addColorBox(colorPreview, theme.getOrange());
            addColorBox(colorPreview, theme.getYellow());
            addColorBox(colorPreview, theme.getGreen());
            addColorBox(colorPreview, theme.getBlue());
            addColorBox(colorPreview, theme.getPurple());

            themeItem.setGraphic(colorPreview);

            // 设置点击事件
            themeItem.setOnAction(event -> {
                // 设置当前主题
                themeManager.setCurrentTheme(theme);

                // 更新主题菜单按钮文本
                themeMenuButton.setText("主题: " + theme.getName());

                // 应用主题
                applyTheme(theme);

                // 显示成功消息
                statusLabel.setText("已应用主题: " + theme.getName());
            });

            themeMenuButton.getItems().add(themeItem);
        }

        // 设置默认主题
        Theme defaultTheme = themeManager.getCurrentTheme();
        themeMenuButton.setText("主题: " + defaultTheme.getName());
        applyTheme(defaultTheme);
    }

    /**
     * 添加颜色预览框
     * @param container 容器
     * @param color 颜色
     */
    private void addColorBox(javafx.scene.layout.HBox container, javafx.scene.paint.Color color) {
        javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(10, 10);
        rect.setFill(color);
        rect.setStroke(javafx.scene.paint.Color.GRAY);
        rect.setStrokeWidth(0.5);
        container.getChildren().add(rect);
    }

    /**
     * 应用主题
     * @param theme 主题
     */
    private void applyTheme(Theme theme) {
        if (mindMap == null) {
            return;
        }

        // 更新画布背景色
        mindMapView.setStyle("-fx-background-color: " + Theme.toRGBCode(theme.getBackgroundColor()) + ";");

        // 更新节点颜色
        for (MindMapNode node : mindMap.getAllNodes()) {
            // 根据节点类型设置不同的颜色
            if (node.isCenterNode()) {
                // 中心节点使用蓝色
                node.setColor(theme.getBlue());
            } else if (node.getParent() == null) {
                // 自由节点使用绿色
                node.setColor(theme.getGreen());
            } else {
                // 其他节点根据深度使用不同的颜色
                int depth = getNodeDepth(node);
                switch (depth % 6) {
                    case 0: node.setColor(theme.getPurple()); break;
                    case 1: node.setColor(theme.getOrange()); break;
                    case 2: node.setColor(theme.getTeal()); break;
                    case 3: node.setColor(theme.getRed()); break;
                    case 4: node.setColor(theme.getIndigo()); break;
                    case 5: node.setColor(theme.getYellow()); break;
                }
            }
        }

        // 重绘思维导图
        mindMapView.draw();
    }

    /**
     * 获取节点深度
     * @param node 节点
     * @return 深度
     */
    private int getNodeDepth(MindMapNode node) {
        int depth = 0;
        MindMapNode current = node;
        while (current.getParent() != null) {
            depth++;
            current = current.getParent();
        }
        return depth;
    }



    /**
     * 处理节点点击事件
     * @param node 被点击的节点
     */
    private void handleNodeClicked(MindMapNode node) {
        // 更新树形视图中的选择
        treeView.selectNode(node);

        // 更新按钮状态
        updateButtonStates(node);
    }



    /**
     * 处理节点选择事件
     * @param node 被选择的节点
     */
    private void handleNodeSelected(MindMapNode node) {
        // 清除所有选中状态
        mindMap.clearAllSelections();

        // 设置新的选中节点
        if (node != null) {
            node.setSelected(true);
        }

        // 重绘思维导图视图
        mindMapView.draw();

        // 更新按钮状态
        updateButtonStates(node);
    }

    /**
     * 更新按钮状态 - 所有按钮始终可用，但在状态栏显示提示
     * @param selectedNode 当前选中的节点
     */
    private void updateButtonStates(MindMapNode selectedNode) {
        // 所有按钮始终可用
        addNodeMenuButton.setDisable(false);
        connectionMenuButton.setDisable(false);
        editNodeMenuButton.setDisable(false);
        deleteNodeButton.setDisable(false);
        layoutMenuButton.setDisable(false);

        // 更新当前选中节点状态
        if (selectedNode != null) {
            statusLabel.setText("当前选中: " + selectedNode.getText());
        } else {
            statusLabel.setText("已就绪");
        }
    }

    /**
     * 更新所有视图
     */
    private void updateViews() {
        // 更新思维导图视图
        mindMapView.setMindMap(mindMap);
        mindMapView.setLayoutStrategy(currentLayout);

        // 更新树形视图
        treeView.setMindMap(mindMap);
    }

    /**
     * 更新标题
     */
    private void updateTitle() {
        if (mindMap != null) {
            String title = mindMap.getName();
            if (mindMap.isModified()) {
                title += " *";
            }
            titleLabel.setText(title);
        } else {
            titleLabel.setText("");
        }
    }

    /**
     * 检查是否需要保存当前思维导图
     * @return 是否可以继续操作
     */
    private boolean checkSaveBeforeAction() {
        if (mindMap != null && mindMap.isModified()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("保存更改");
            alert.setHeaderText("思维导图已修改");
            alert.setContentText("是否保存更改？");

            ButtonType saveButton = new ButtonType("保存");
            ButtonType noSaveButton = new ButtonType("不保存");
            ButtonType cancelButton = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(saveButton, noSaveButton, cancelButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == saveButton) {
                    return FileManager.saveMindMap(mindMap, getStage());
                } else if (result.get() == cancelButton) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 显示警告
     * @param message 警告消息
     */
    private void showAlert(String message) {
        // 在状态栏显示警告
        statusLabel.setText("警告: " + message);

        // 设置状态栏文本颜色为警告色
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");

        // 创建一个定时器，5秒后恢复状态栏
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5));
        pause.setOnFinished(event -> {
            statusLabel.setText("已就绪");
            statusLabel.setStyle(""); // 恢复默认样式
        });
        pause.play();
    }

    /**
     * 显示信息
     * @param title 标题
     * @param message 信息消息
     */
    private void showInformation(String title, String message) {
        // 在状态栏显示信息
        statusLabel.setText(title + ": " + message);

        // 创建一个定时器，5秒后恢复状态栏
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5));
        pause.setOnFinished(event -> statusLabel.setText("已就绪"));
        pause.play();
    }

    /**
     * 获取当前舞台
     * @return 舞台
     */
    private Stage getStage() {
        return (Stage) rootPane.getScene().getWindow();
    }
}
