package com.example.demo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.application.Platform;

import com.example.demo.theme.Theme;
import com.example.demo.theme.ThemeManager;

/**
 * 思维导图控制器
 * 处理思维导图界面的所有功能
 */
public class MindMapController {

    @FXML private BorderPane rootPane;
    @FXML private Label titleLabel;
    @FXML private Label statusLabel;

    // 菜单按钮
    @FXML private MenuButton layoutMenuButton;
    @FXML private MenuButton themeMenuButton;
    @FXML private Button toggleSidebarButton;

    // 节点操作按钮
    @FXML private MenuButton addNodeMenuButton;
    @FXML private MenuItem addChildMenuItem;
    @FXML private MenuItem addSiblingMenuItem;
    @FXML private MenuItem addFreeNodeMenuItem;

    @FXML private MenuButton connectionMenuButton;
    @FXML private MenuItem connectNodesMenuItem;
    @FXML private MenuItem disconnectNodesMenuItem;

    @FXML private MenuButton editNodeMenuButton;
    @FXML private MenuItem changeShapeMenuItem;
    @FXML private MenuItem changeSizeMenuItem;
    @FXML private MenuItem changeFontMenuItem;
    @FXML private MenuItem changeLineStyleMenuItem;
    @FXML private MenuItem editTextMenuItem;

    @FXML private Button deleteNodeButton;

    // 连接操作按钮
    @FXML private Button connectionModeButton;
    @FXML private Button exitConnectionModeButton;

    // 布局组件
    @FXML private SplitPane mainSplitPane;
    @FXML private Pane mindMapViewContainer;
    @FXML private TreeView<String> treeViewControl;

    /**
     * 初始化控制器
     */
    @FXML
    public void initialize() {
        // 初始化思维导图布局
        setupMindMapLayout();

        // 初始化主题选项
        setupThemeOptions();

        // 初始化树形视图
        setupTreeView();

        // 设置状态信息
        statusLabel.setText("思维导图已就绪");
    }

    /**
     * 设置思维导图布局
     */
    private void setupMindMapLayout() {
        // 添加布局选项
        MenuItem[] layoutOptions = {
            createMenuItem("树形布局", e -> applyTreeLayout()),
            createMenuItem("放射性布局", e -> applyRadialLayout()),
            createMenuItem("水平布局", e -> applyHorizontalLayout()),
            createMenuItem("垂直布局", e -> applyVerticalLayout())
        };

        layoutMenuButton.getItems().addAll(layoutOptions);
    }

    /**
     * 设置主题选项
     */
    private void setupThemeOptions() {
        // 获取主题管理器
        ThemeManager themeManager = ThemeManager.getInstance();

        // 清空现有菜单项
        themeMenuButton.getItems().clear();

        // 为每个主题创建菜单项
        for (Theme theme : themeManager.getThemes()) {
            MenuItem item = createMenuItem(theme.getName(), e -> applyTheme(theme));
            themeMenuButton.getItems().add(item);
        }

        // 应用默认主题
        applyTheme(themeManager.getCurrentTheme());

        System.out.println("主题选项已设置，共" + themeMenuButton.getItems().size() + "个主题");
    }

    /**
     * 设置树形视图
     */
    private void setupTreeView() {
        // 创建根节点
        TreeItem<String> rootItem = new TreeItem<>("根节点");
        rootItem.setExpanded(true);

        // 创建示例子节点
        rootItem.getChildren().add(new TreeItem<>("子节点 1"));
        rootItem.getChildren().add(new TreeItem<>("子节点 2"));

        treeViewControl.setRoot(rootItem);
        treeViewControl.setShowRoot(true);
    }

    /**
     * 辅助方法：创建菜单项
     */
    private MenuItem createMenuItem(String text, javafx.event.EventHandler<ActionEvent> handler) {
        MenuItem item = new MenuItem(text);
        item.setOnAction(handler);
        return item;
    }

    /**
     * 应用主题
     * @param theme 主题对象
     */
    private void applyTheme(Theme theme) {
        if (theme == null) {
            System.out.println("错误：主题对象为空");
            return;
        }

        System.out.println("应用主题: " + theme.getName());

        // 移除所有主题类
        rootPane.getStyleClass().removeAll(
            "warm-pink-theme", "purple-theme", "blue-green-theme", "gray-theme"
        );

        // 根据主题名称选择相应的CSS类
        String themeClass = "";
        String themeName = theme.getName();

        if (themeName.equals("温暖粉色")) {
            themeClass = "warm-pink-theme";
        } else if (themeName.equals("棕紫主题")) {
            themeClass = "purple-theme";
        } else if (themeName.equals("蓝绿主题")) {
            themeClass = "blue-green-theme";
        } else if (themeName.equals("灰色主题")) {
            themeClass = "gray-theme";
        }

        // 应用新主题
        if (!themeClass.isEmpty()) {
            rootPane.getStyleClass().add(themeClass);
            System.out.println("已添加主题类: " + themeClass);
        }

        // 更新背景色
        String backgroundColor = Theme.toRGBCode(theme.getBackgroundColor());
        rootPane.setStyle("-fx-background-color: " + backgroundColor + ";");

        // 更新状态栏
        statusLabel.setText("已应用主题：" + theme.getName());

        // 设置为当前主题
        ThemeManager.getInstance().setCurrentTheme(theme);
    }

    /**
     * 应用树形布局
     */
    private void applyTreeLayout() {
        statusLabel.setText("已应用树形布局");
    }

    /**
     * 应用放射性布局
     */
    private void applyRadialLayout() {
        statusLabel.setText("已应用放射性布局");
    }

    /**
     * 应用水平布局
     */
    private void applyHorizontalLayout() {
        statusLabel.setText("已应用水平布局");
    }

    /**
     * 应用垂直布局
     */
    private void applyVerticalLayout() {
        statusLabel.setText("已应用垂直布局");
    }

    // FXML事件处理方法

    @FXML
    public void createNewMindMap() {
        statusLabel.setText("创建新思维导图");
    }

    @FXML
    public void openMindMap() {
        statusLabel.setText("打开思维导图");
    }

    @FXML
    public void saveMindMap() {
        statusLabel.setText("保存思维导图");
    }

    @FXML
    public void saveAsMindMap() {
        statusLabel.setText("另存为思维导图");
    }

    @FXML
    public void exportAsJPG() {
        statusLabel.setText("导出为JPG格式");
    }

    @FXML
    public void exportAsPNG() {
        statusLabel.setText("导出为PNG格式");
    }

    @FXML
    public void toggleSidebar() {
        boolean isSidebarVisible = mainSplitPane.getItems().size() > 1;

        if (isSidebarVisible) {
            // 保存当前分割位置
            double[] positions = mainSplitPane.getDividerPositions();
            mainSplitPane.getItems().remove(1);
            toggleSidebarButton.setText("展开菜单");
        } else {
            // 恢复树形视图面板
            VBox sidePanel = (VBox) mainSplitPane.getItems().get(0).getParent().lookup("VBox");
            mainSplitPane.getItems().add(sidePanel);
            mainSplitPane.setDividerPositions(0.75);
            toggleSidebarButton.setText("收起菜单");
        }
    }

    @FXML
    public void addChildNode() {
        statusLabel.setText("添加子节点");
    }

    @FXML
    public void addSiblingNode() {
        statusLabel.setText("添加兄弟节点");
    }

    @FXML
    public void addFreeNode() {
        statusLabel.setText("添加自由节点");
    }

    @FXML
    public void connectNodes() {
        statusLabel.setText("连接节点模式");
        connectionModeButton.setVisible(false);
        exitConnectionModeButton.setVisible(true);
    }

    @FXML
    public void disconnectNodes() {
        statusLabel.setText("断开节点连接");
    }

    @FXML
    public void changeNodeShape() {
        statusLabel.setText("更改节点形状");
    }

    @FXML
    public void changeNodeSize() {
        statusLabel.setText("更改节点大小");
    }

    @FXML
    public void changeNodeFont() {
        statusLabel.setText("更改节点字体");
    }

    @FXML
    public void changeLineStyle() {
        statusLabel.setText("更改连接线样式");
    }

    @FXML
    public void editNodeText() {
        statusLabel.setText("编辑节点文本");
    }

    @FXML
    public void deleteNode() {
        statusLabel.setText("删除节点");
    }

    @FXML
    public void exitConnectionMode() {
        statusLabel.setText("退出连接模式");
        connectionModeButton.setVisible(true);
        exitConnectionModeButton.setVisible(false);
    }
}
