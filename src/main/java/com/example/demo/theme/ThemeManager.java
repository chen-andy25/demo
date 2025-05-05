package com.example.demo.theme;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * 主题管理器，用于管理应用程序的主题
 */
public class ThemeManager {
    
    // 单例实例
    private static ThemeManager instance;
    
    // 主题列表
    private final List<Theme> themes = new ArrayList<>();
    
    // 当前主题
    private Theme currentTheme;
    
    /**
     * 私有构造函数，初始化主题
     */
    private ThemeManager() {
        // 创建默认主题（浅色）
        Theme defaultLight = new Theme(
            "默认（浅色）",
            Color.rgb(255, 59, 48),    // 红色
            Color.rgb(255, 149, 0),    // 橙色
            Color.rgb(255, 204, 0),    // 黄色
            Color.rgb(40, 205, 65),    // 绿色
            Color.rgb(0, 199, 190),    // 薄荷色
            Color.rgb(89, 173, 196),   // 深青色
            Color.rgb(85, 190, 240),   // 青色
            Color.rgb(0, 122, 255),    // 蓝色
            Color.rgb(88, 86, 214),    // 靛蓝色
            Color.rgb(175, 82, 222),   // 紫色
            Color.rgb(255, 45, 85),    // 粉色
            Color.rgb(162, 132, 94),   // 棕色
            Color.rgb(142, 142, 147),  // 灰色
            Color.rgb(255, 255, 255),  // 背景色（白色）
            Color.rgb(0, 0, 0)         // 文本色（黑色）
        );
        
        // 创建默认主题（深色）
        Theme defaultDark = new Theme(
            "默认（深色）",
            Color.rgb(255, 69, 58),    // 红色
            Color.rgb(255, 159, 10),   // 橙色
            Color.rgb(255, 214, 10),   // 黄色
            Color.rgb(50, 215, 75),    // 绿色
            Color.rgb(102, 212, 207),  // 薄荷色
            Color.rgb(106, 196, 220),  // 深青色
            Color.rgb(90, 200, 245),   // 青色
            Color.rgb(10, 132, 255),   // 蓝色
            Color.rgb(94, 92, 230),    // 靛蓝色
            Color.rgb(191, 90, 242),   // 紫色
            Color.rgb(255, 55, 95),    // 粉色
            Color.rgb(172, 142, 104),  // 棕色
            Color.rgb(152, 152, 157),  // 灰色
            Color.rgb(30, 30, 30),     // 背景色（深灰色）
            Color.rgb(255, 255, 255)   // 文本色（白色）
        );
        
        // 创建可访问主题（浅色）
        Theme accessibleLight = new Theme(
            "可访问（浅色）",
            Color.rgb(215, 0, 21),     // 红色
            Color.rgb(201, 52, 0),     // 橙色
            Color.rgb(160, 90, 0),     // 黄色
            Color.rgb(0, 125, 27),     // 绿色
            Color.rgb(12, 129, 123),   // 薄荷色
            Color.rgb(0, 130, 153),    // 深青色
            Color.rgb(0, 113, 164),    // 青色
            Color.rgb(0, 64, 221),     // 蓝色
            Color.rgb(54, 52, 163),    // 靛蓝色
            Color.rgb(173, 68, 171),   // 紫色
            Color.rgb(211, 15, 69),    // 粉色
            Color.rgb(127, 101, 69),   // 棕色
            Color.rgb(105, 105, 110),  // 灰色
            Color.rgb(255, 255, 255),  // 背景色（白色）
            Color.rgb(0, 0, 0)         // 文本色（黑色）
        );
        
        // 创建可访问主题（深色）
        Theme accessibleDark = new Theme(
            "可访问（深色）",
            Color.rgb(255, 105, 97),   // 红色
            Color.rgb(255, 179, 64),   // 橙色
            Color.rgb(255, 212, 38),   // 黄色
            Color.rgb(49, 222, 75),    // 绿色
            Color.rgb(102, 212, 207),  // 薄荷色
            Color.rgb(93, 230, 255),   // 深青色
            Color.rgb(112, 215, 255),  // 青色
            Color.rgb(64, 156, 255),   // 蓝色
            Color.rgb(125, 122, 255),  // 靛蓝色
            Color.rgb(218, 143, 255),  // 紫色
            Color.rgb(255, 100, 130),  // 粉色
            Color.rgb(181, 148, 105),  // 棕色
            Color.rgb(152, 152, 157),  // 灰色
            Color.rgb(30, 30, 30),     // 背景色（深灰色）
            Color.rgb(255, 255, 255)   // 文本色（白色）
        );
        
        // 添加主题到列表
        themes.add(defaultLight);
        themes.add(defaultDark);
        themes.add(accessibleLight);
        themes.add(accessibleDark);
        
        // 设置默认主题
        currentTheme = defaultLight;
    }
    
    /**
     * 获取单例实例
     * @return 主题管理器实例
     */
    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    /**
     * 获取所有主题
     * @return 主题列表
     */
    public List<Theme> getThemes() {
        return themes;
    }
    
    /**
     * 获取当前主题
     * @return 当前主题
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * 设置当前主题
     * @param theme 主题
     */
    public void setCurrentTheme(Theme theme) {
        this.currentTheme = theme;
    }
    
    /**
     * 根据名称获取主题
     * @param name 主题名称
     * @return 主题
     */
    public Theme getThemeByName(String name) {
        for (Theme theme : themes) {
            if (theme.getName().equals(name)) {
                return theme;
            }
        }
        return currentTheme;
    }
}
