package com.example.demo.theme;

import javafx.scene.paint.Color;

/**
 * 主题类，用于管理应用程序的颜色主题
 */
public class Theme {
    
    // 主题名称
    private final String name;
    
    // 主题颜色
    private final Color red;
    private final Color orange;
    private final Color yellow;
    private final Color green;
    private final Color mint;
    private final Color teal;
    private final Color cyan;
    private final Color blue;
    private final Color indigo;
    private final Color purple;
    private final Color pink;
    private final Color brown;
    private final Color gray;
    
    // 背景色和文本色
    private final Color backgroundColor;
    private final Color textColor;
    
    /**
     * 创建主题
     * @param name 主题名称
     * @param red 红色
     * @param orange 橙色
     * @param yellow 黄色
     * @param green 绿色
     * @param mint 薄荷色
     * @param teal 深青色
     * @param cyan 青色
     * @param blue 蓝色
     * @param indigo 靛蓝色
     * @param purple 紫色
     * @param pink 粉色
     * @param brown 棕色
     * @param gray 灰色
     * @param backgroundColor 背景色
     * @param textColor 文本色
     */
    public Theme(String name, Color red, Color orange, Color yellow, Color green, Color mint, Color teal, Color cyan, Color blue, Color indigo, Color purple, Color pink, Color brown, Color gray, Color backgroundColor, Color textColor) {
        this.name = name;
        this.red = red;
        this.orange = orange;
        this.yellow = yellow;
        this.green = green;
        this.mint = mint;
        this.teal = teal;
        this.cyan = cyan;
        this.blue = blue;
        this.indigo = indigo;
        this.purple = purple;
        this.pink = pink;
        this.brown = brown;
        this.gray = gray;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }
    
    /**
     * 获取主题名称
     * @return 主题名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取红色
     * @return 红色
     */
    public Color getRed() {
        return red;
    }
    
    /**
     * 获取橙色
     * @return 橙色
     */
    public Color getOrange() {
        return orange;
    }
    
    /**
     * 获取黄色
     * @return 黄色
     */
    public Color getYellow() {
        return yellow;
    }
    
    /**
     * 获取绿色
     * @return 绿色
     */
    public Color getGreen() {
        return green;
    }
    
    /**
     * 获取薄荷色
     * @return 薄荷色
     */
    public Color getMint() {
        return mint;
    }
    
    /**
     * 获取深青色
     * @return 深青色
     */
    public Color getTeal() {
        return teal;
    }
    
    /**
     * 获取青色
     * @return 青色
     */
    public Color getCyan() {
        return cyan;
    }
    
    /**
     * 获取蓝色
     * @return 蓝色
     */
    public Color getBlue() {
        return blue;
    }
    
    /**
     * 获取靛蓝色
     * @return 靛蓝色
     */
    public Color getIndigo() {
        return indigo;
    }
    
    /**
     * 获取紫色
     * @return 紫色
     */
    public Color getPurple() {
        return purple;
    }
    
    /**
     * 获取粉色
     * @return 粉色
     */
    public Color getPink() {
        return pink;
    }
    
    /**
     * 获取棕色
     * @return 棕色
     */
    public Color getBrown() {
        return brown;
    }
    
    /**
     * 获取灰色
     * @return 灰色
     */
    public Color getGray() {
        return gray;
    }
    
    /**
     * 获取背景色
     * @return 背景色
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    
    /**
     * 获取文本色
     * @return 文本色
     */
    public Color getTextColor() {
        return textColor;
    }
    
    /**
     * 根据名称获取颜色
     * @param colorName 颜色名称
     * @return 颜色
     */
    public Color getColorByName(String colorName) {
        switch (colorName.toLowerCase()) {
            case "red": return red;
            case "orange": return orange;
            case "yellow": return yellow;
            case "green": return green;
            case "mint": return mint;
            case "teal": return teal;
            case "cyan": return cyan;
            case "blue": return blue;
            case "indigo": return indigo;
            case "purple": return purple;
            case "pink": return pink;
            case "brown": return brown;
            case "gray": return gray;
            default: return blue; // 默认返回蓝色
        }
    }
    
    /**
     * 获取CSS颜色字符串
     * @param color 颜色
     * @return CSS颜色字符串
     */
    public static String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    
    @Override
    public String toString() {
        return name;
    }
}
