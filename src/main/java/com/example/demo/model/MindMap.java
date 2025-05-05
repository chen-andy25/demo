package com.example.demo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表示一个完整的思维导图
 */
public class MindMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private MindMapNode rootNode;
    private String filePath;
    private boolean modified;
    private String name;
    private Map<String, MindMapNode> nodeMap;

    /**
     * 创建一个新的思维导图
     * @param centerText 中心节点文本
     */
    public MindMap(String centerText) {
        this.rootNode = new MindMapNode(centerText);
        this.rootNode.setCenterNode(true); // 标记为中心节点
        this.modified = false;
        this.name = "未命名";
        this.nodeMap = new HashMap<>();
        this.nodeMap.put(rootNode.getId(), rootNode);
    }

    /**
     * 获取根节点
     * @return 根节点
     */
    public MindMapNode getRootNode() {
        return rootNode;
    }

    /**
     * 获取文件路径
     * @return 文件路径
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 设置文件路径
     * @param filePath 文件路径
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
        if (filePath != null) {
            int lastSeparatorIndex = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
            if (lastSeparatorIndex >= 0 && lastSeparatorIndex < filePath.length() - 1) {
                String fileName = filePath.substring(lastSeparatorIndex + 1);
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    this.name = fileName.substring(0, dotIndex);
                } else {
                    this.name = fileName;
                }
            }
        }
    }

    /**
     * 判断思维导图是否被修改
     * @return 是否被修改
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * 设置思维导图修改状态
     * @param modified 修改状态
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /**
     * 获取思维导图名称
     * @return 思维导图名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置思维导图名称
     * @param name 思维导图名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 根据ID获取节点
     * @param id 节点ID
     * @return 节点对象，如果不存在则返回null
     */
    public MindMapNode getNodeById(String id) {
        return nodeMap.get(id);
    }

    /**
     * 添加节点到映射表
     * @param node 要添加的节点
     */
    public void addNodeToMap(MindMapNode node) {
        nodeMap.put(node.getId(), node);
    }

    /**
     * 从映射表中移除节点
     * @param id 要移除的节点ID
     */
    public void removeNodeFromMap(String id) {
        nodeMap.remove(id);
    }

    /**
     * 获取所有节点
     * @return 所有节点的列表
     */
    public List<MindMapNode> getAllNodes() {
        return new ArrayList<>(nodeMap.values());
    }

    /**
     * 清除所有选中状态
     */
    public void clearAllSelections() {
        for (MindMapNode node : nodeMap.values()) {
            node.setSelected(false);
        }
    }

    /**
     * 添加子节点
     * @param parent 父节点
     * @param text 子节点文本
     * @return 新创建的子节点
     */
    public MindMapNode addChildNode(MindMapNode parent, String text) {
        // 创建新节点
        MindMapNode child = new MindMapNode(text);

        // 设置子节点的初始位置，使其在父节点附近
        // 这样即使是自由节点的子节点也能正确显示
        child.setX(parent.getX() + parent.getWidth() + 50);
        child.setY(parent.getY() + 30);

        // 继承父节点的颜色，但稍微深一点
        if (parent.getColor() != null) {
            child.setColor(parent.getColor().darker());
        }

        // 添加到父节点
        parent.addChild(child);

        // 添加到节点映射
        nodeMap.put(child.getId(), child);

        // 标记为已修改
        setModified(true);

        return child;
    }

    /**
     * 添加兄弟节点
     * @param sibling 参考节点
     * @param text 新节点文本
     * @return 新创建的兄弟节点
     */
    public MindMapNode addSiblingNode(MindMapNode sibling, String text) {
        if (sibling.getParent() == null) {
            // 中心节点不能有兄弟节点
            return null;
        }

        MindMapNode parent = sibling.getParent();
        MindMapNode newNode = new MindMapNode(text);
        parent.addChild(newNode);
        nodeMap.put(newNode.getId(), newNode);
        setModified(true);
        return newNode;
    }

    /**
     * 添加自由节点（不与其他节点相连）
     * @param node 要添加的节点
     */
    public void addFreeNode(MindMapNode node) {
        // 将节点添加到映射表中
        nodeMap.put(node.getId(), node);
        setModified(true);
    }

    /**
     * 删除节点及其所有子节点
     * @param node 要删除的节点
     * @return 是否成功删除
     */
    public boolean deleteNode(MindMapNode node) {
        if (node == rootNode) {
            // 不能删除根节点
            return false;
        }

        // 递归删除所有子节点
        deleteNodeRecursively(node);

        // 从父节点中移除
        MindMapNode parent = node.getParent();
        boolean result = parent.removeChild(node);

        if (result) {
            setModified(true);
        }

        return result;
    }

    /**
     * 递归删除节点及其所有子节点
     * @param node 要删除的节点
     */
    private void deleteNodeRecursively(MindMapNode node) {
        // 复制子节点列表，避免并发修改异常
        List<MindMapNode> childrenCopy = new ArrayList<>(node.getChildren());

        // 递归删除所有子节点
        for (MindMapNode child : childrenCopy) {
            deleteNodeRecursively(child);
        }

        // 从映射表中移除当前节点
        nodeMap.remove(node.getId());
    }
}
