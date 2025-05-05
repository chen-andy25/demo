package com.example.demo.view;

import com.example.demo.model.MindMap;
import com.example.demo.model.MindMapNode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.HashMap;
import java.util.Map;

/**
 * 思维导图树形结构视图
 */
public class MindMapTreeView extends TreeView<MindMapNode> {

    private MindMap mindMap;
    private Map<String, TreeItem<MindMapNode>> nodeItemMap;
    private NodeSelectListener nodeSelectListener;

    /**
     * 创建思维导图树形视图
     */
    public MindMapTreeView() {
        this.nodeItemMap = new HashMap<>();

        // 设置选择监听器
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && nodeSelectListener != null) {
                nodeSelectListener.onNodeSelected(newValue.getValue());
            }
        });
    }

    /**
     * 设置思维导图
     * @param mindMap 思维导图
     */
    public void setMindMap(MindMap mindMap) {
        this.mindMap = mindMap;
        this.nodeItemMap.clear();

        if (mindMap == null) {
            setRoot(null);
            return;
        }

        // 创建根节点
        TreeItem<MindMapNode> rootItem = createTreeItem(mindMap.getRootNode());

        // 创建自由节点文件夹 - 始终创建，即使没有自由节点
        TreeItem<MindMapNode> freeNodesFolder = new TreeItem<>(new MindMapNode("自由节点"));
        freeNodesFolder.setExpanded(true); // 始终展开自由节点文件夹

        // 添加自由节点
        int freeNodeCount = 0;
        System.out.println("Checking for free nodes among " + mindMap.getAllNodes().size() + " total nodes");
        for (MindMapNode node : mindMap.getAllNodes()) {
            // 如果节点没有父节点且不是中心节点，则它是自由节点
            if (node.getParent() == null && !node.isCenterNode()) {
                System.out.println("Found free node: " + node.getText() + " (ID: " + node.getId() + ")");
                TreeItem<MindMapNode> freeNodeItem = new TreeItem<>(node);
                freeNodesFolder.getChildren().add(freeNodeItem);
                nodeItemMap.put(node.getId(), freeNodeItem);
                freeNodeCount++;
            }
        }
        System.out.println("Free node count: " + freeNodeCount);

        // 始终添加自由节点文件夹，即使没有自由节点
        rootItem.getChildren().add(freeNodesFolder);

        setRoot(rootItem);
        rootItem.setExpanded(true);
    }

    /**
     * 设置节点选择监听器
     * @param listener 监听器
     */
    public void setNodeSelectListener(NodeSelectListener listener) {
        this.nodeSelectListener = listener;
    }

    /**
     * 选择指定的节点
     * @param node 要选择的节点
     */
    public void selectNode(MindMapNode node) {
        if (node == null || nodeItemMap.isEmpty()) {
            getSelectionModel().clearSelection();
            return;
        }

        TreeItem<MindMapNode> item = nodeItemMap.get(node.getId());
        if (item != null) {
            getSelectionModel().select(item);

            // 展开所有父节点
            TreeItem<MindMapNode> parent = item.getParent();
            while (parent != null) {
                parent.setExpanded(true);
                parent = parent.getParent();
            }
        }
    }

    /**
     * 更新树形结构
     */
    public void updateTree() {
        if (mindMap == null) {
            return;
        }

        // 保存当前选中的节点
        MindMapNode selectedNode = null;
        TreeItem<MindMapNode> selectedItem = getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            selectedNode = selectedItem.getValue();
        }

        // 重新创建树形结构
        setMindMap(mindMap);

        // 恢复选中状态
        if (selectedNode != null) {
            selectNode(selectedNode);
        }
    }

    /**
     * 递归创建树形项
     * @param node 思维导图节点
     * @return 树形项
     */
    private TreeItem<MindMapNode> createTreeItem(MindMapNode node) {
        TreeItem<MindMapNode> item = new TreeItem<>(node);
        nodeItemMap.put(node.getId(), item);

        // 递归创建子节点
        for (MindMapNode child : node.getChildren()) {
            item.getChildren().add(createTreeItem(child));
        }

        return item;
    }

    /**
     * 节点选择监听器接口
     */
    public interface NodeSelectListener {
        /**
         * 当节点被选择时调用
         * @param node 被选择的节点
         */
        void onNodeSelected(MindMapNode node);
    }
}
