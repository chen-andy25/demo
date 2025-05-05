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
        // 创建温暖粉色主题
        Theme warmPink = new Theme(
            "温暖粉色",
            Color.web("#FCF5F5"),    // 浅粉色
            Color.web("#EDD6CC"),    // 浅橙色
            Color.web("#AB9D9A"),    // 浅棕色
            Color.web("#7A6C69"),    // 深棕色
            Color.web("#7A6C69"),    // 深棕色
            Color.web("#AB9D9A"),    // 浅棕色
            Color.web("#AB9D9A"),    // 浅棕色
            Color.web("#7A6C69"),    // 深棕色
            Color.web("#2E2827"),    // 深灰色
            Color.web("#EDD6CC"),    // 浅橙色
            Color.web("#FCF5F5"),    // 浅粉色
            Color.web("#AB9D9A"),    // 浅棕色
            Color.web("#7A6C69"),    // 深棕色
            Color.web("#FFFFFF"),    // 背景色（白色）
            Color.web("#2E2827")     // 文本色（深灰色）
        );

        // 创建棕紫主题
        Theme purpleTheme = new Theme(
            "棕紫主题",
            Color.web("#D9A9CC"),    // 浅紫色
            Color.web("#F2D0ED"),    // 浅粉色
            Color.web("#8D7BA6"),    // 深紫色
            Color.web("#6960A6"),    // 深蓝紫色
            Color.web("#6960A6"),    // 深蓝紫色
            Color.web("#8D7BA6"),    // 深紫色
            Color.web("#8D7BA6"),    // 深紫色
            Color.web("#6960A6"),    // 深蓝紫色
            Color.web("#010A26"),    // 深蓝色
            Color.web("#D9A9CC"),    // 浅紫色
            Color.web("#F2D0ED"),    // 浅粉色
            Color.web("#8D7BA6"),    // 深紫色
            Color.web("#6960A6"),    // 深蓝紫色
            Color.web("#FFFFFF"),    // 背景色（白色）
            Color.web("#010A26")     // 文本色（深蓝色）
        );

        // 创建蓝绿主题
        Theme blueGreenTheme = new Theme(
            "蓝绿主题",
            Color.web("#1d3752"),    // 深蓝色
            Color.web("#214d72"),    // 中蓝色
            Color.web("#2c7695"),    // 浅蓝色
            Color.web("#50bfc3"),    // 浅绿色
            Color.web("#50bfc3"),    // 浅绿色
            Color.web("#2c7695"),    // 浅蓝色
            Color.web("#2c7695"),    // 浅蓝色
            Color.web("#214d72"),    // 中蓝色
            Color.web("#1d3752"),    // 深蓝色
            Color.web("#2c7695"),    // 浅蓝色
            Color.web("#50bfc3"),    // 浅绿色
            Color.web("#214d72"),    // 中蓝色
            Color.web("#1d3752"),    // 深蓝色
            Color.web("#FFFFFF"),    // 背景色（白色）
            Color.web("#1d3752")     // 文本色（深蓝色）
        );

        // 创建灰色主题
        Theme grayTheme = new Theme(
            "灰色主题",
            Color.web("#474847"),    // 深灰色
            Color.web("#7E7D83"),    // 中灰色
            Color.web("#C0C0C0"),    // 浅灰色
            Color.web("#F1F1F4"),    // 浅灰白色
            Color.web("#F1F1F4"),    // 浅灰白色
            Color.web("#C0C0C0"),    // 浅灰色
            Color.web("#C0C0C0"),    // 浅灰色
            Color.web("#7E7D83"),    // 中灰色
            Color.web("#474847"),    // 深灰色
            Color.web("#C0C0C0"),    // 浅灰色
            Color.web("#F1F1F4"),    // 浅灰白色
            Color.web("#7E7D83"),    // 中灰色
            Color.web("#FAFBFC"),    // 接近白色
            Color.web("#FFFFFF"),    // 背景色（白色）
            Color.web("#474847")     // 文本色（深灰色）
        );

        // 添加主题到列表
        themes.add(warmPink);
        themes.add(purpleTheme);
        themes.add(blueGreenTheme);
        themes.add(grayTheme);

        // 设置默认主题
        currentTheme = warmPink;
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
