package com.example.demo.event;

import com.example.demo.model.MindMapNode;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * 思维导图事件类
 */
public class MindMapEvent extends Event {
    
    // 事件类型
    public static final EventType<MindMapEvent> ANY = new EventType<>(Event.ANY, "MINDMAP_EVENT");
    public static final EventType<MindMapEvent> ADD_CHILD_NODE = new EventType<>(ANY, "ADD_CHILD_NODE");
    public static final EventType<MindMapEvent> ADD_SIBLING_NODE = new EventType<>(ANY, "ADD_SIBLING_NODE");
    public static final EventType<MindMapEvent> EDIT_NODE_TEXT = new EventType<>(ANY, "EDIT_NODE_TEXT");
    public static final EventType<MindMapEvent> CHANGE_NODE_SHAPE = new EventType<>(ANY, "CHANGE_NODE_SHAPE");
    public static final EventType<MindMapEvent> CHANGE_NODE_SIZE = new EventType<>(ANY, "CHANGE_NODE_SIZE");
    public static final EventType<MindMapEvent> CHANGE_NODE_FONT = new EventType<>(ANY, "CHANGE_NODE_FONT");
    public static final EventType<MindMapEvent> DELETE_NODE = new EventType<>(ANY, "DELETE_NODE");
    public static final EventType<MindMapEvent> CONNECT_NODE = new EventType<>(ANY, "CONNECT_NODE");
    public static final EventType<MindMapEvent> DISCONNECT_NODE = new EventType<>(ANY, "DISCONNECT_NODE");
    
    private final MindMapNode node;
    
    /**
     * 创建思维导图事件
     * @param eventType 事件类型
     * @param node 相关节点
     */
    public MindMapEvent(EventType<MindMapEvent> eventType, MindMapNode node) {
        super(eventType);
        this.node = node;
    }
    
    /**
     * 获取相关节点
     * @return 节点
     */
    public MindMapNode getNode() {
        return node;
    }
}
