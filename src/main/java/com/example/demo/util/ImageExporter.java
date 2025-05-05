package com.example.demo.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 图像导出工具类，用于将思维导图导出为图像文件
 */
public class ImageExporter {
    
    /**
     * 导出节点为JPG图像
     * @param node 要导出的节点
     * @param stage 当前舞台
     * @return 是否成功导出
     */
    public static boolean exportAsJPG(Node node, Stage stage) {
        return exportImage(node, stage, "jpg", "JPG图像文件");
    }
    
    /**
     * 导出节点为PNG图像
     * @param node 要导出的节点
     * @param stage 当前舞台
     * @return 是否成功导出
     */
    public static boolean exportAsPNG(Node node, Stage stage) {
        return exportImage(node, stage, "png", "PNG图像文件");
    }
    
    /**
     * 导出节点为图像
     * @param node 要导出的节点
     * @param stage 当前舞台
     * @param format 图像格式（jpg或png）
     * @param description 文件描述
     * @return 是否成功导出
     */
    private static boolean exportImage(Node node, Stage stage, String format, String description) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("导出为" + format.toUpperCase() + "图像");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(description, "*." + format));
        
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return false;
        }
        
        // 确保文件有正确的扩展名
        String path = file.getPath();
        if (!path.toLowerCase().endsWith("." + format)) {
            path += "." + format;
            file = new File(path);
        }
        
        try {
            // 创建快照
            WritableImage image = node.snapshot(new SnapshotParameters(), null);
            
            // 保存图像
            return ImageIO.write(SwingFXUtils.fromFXImage(image, null), format, file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
