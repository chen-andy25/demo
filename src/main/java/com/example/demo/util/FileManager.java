package com.example.demo.util;

import com.example.demo.model.MindMap;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

/**
 * 文件管理工具类，用于保存和加载思维导图
 */
public class FileManager {

    private static final String FILE_EXTENSION = "*.dt";
    private static final String FILE_DESCRIPTION = "思维导图文件";

    /**
     * 保存思维导图到文件
     * @param mindMap 要保存的思维导图
     * @param stage 当前舞台
     * @return 是否成功保存
     */
    public static boolean saveMindMap(MindMap mindMap, Stage stage) {
        String filePath = mindMap.getFilePath();

        // 如果没有文件路径，则弹出保存对话框
        if (filePath == null || filePath.isEmpty()) {
            return saveAsMindMap(mindMap, stage);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(mindMap);
            mindMap.setModified(false);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 另存为思维导图
     * @param mindMap 要保存的思维导图
     * @param stage 当前舞台
     * @return 是否成功保存
     */
    public static boolean saveAsMindMap(MindMap mindMap, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存思维导图");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(FILE_DESCRIPTION, FILE_EXTENSION));

        // 设置初始文件名
        fileChooser.setInitialFileName(mindMap.getName() + ".dt");

        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return false;
        }

        // 确保文件有正确的扩展名
        String path = file.getPath();
        if (!path.toLowerCase().endsWith(".dt")) {
            path += ".dt";
            file = new File(path);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            mindMap.setFilePath(file.getPath());
            oos.writeObject(mindMap);
            mindMap.setModified(false);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 加载思维导图
     * @param stage 当前舞台
     * @return 加载的思维导图，如果取消或出错则返回null
     */
    public static MindMap loadMindMap(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开思维导图");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(FILE_DESCRIPTION, FILE_EXTENSION));

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            System.out.println("User canceled file selection");
            return null;
        }

        System.out.println("Attempting to load file: " + file.getPath());

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            System.out.println("File loaded, object class: " + obj.getClass().getName());

            if (obj instanceof MindMap) {
                MindMap mindMap = (MindMap) obj;
                mindMap.setFilePath(file.getPath());
                mindMap.setModified(false);
                System.out.println("Successfully loaded mind map: " + mindMap.getName());
                return mindMap;
            } else {
                System.err.println("Loaded object is not a MindMap: " + obj.getClass().getName());
                showErrorAlert(stage, "文件格式错误", "所选文件不是有效的思维导图文件。");
                return null;
            }
        } catch (IOException e) {
            System.err.println("IO error while loading file: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert(stage, "文件读取错误", "无法读取文件，可能文件已损坏或格式不兼容。\n\n错误信息: " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found error: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert(stage, "类型错误", "无法识别文件中的对象类型。\n\n错误信息: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert(stage, "意外错误", "加载文件时发生意外错误。\n\n错误信息: " + e.getMessage());
            return null;
        }
    }

    /**
     * 显示错误对话框
     * @param stage 当前舞台
     * @param title 标题
     * @param message 消息
     */
    private static void showErrorAlert(Stage stage, String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.showAndWait();
    }
}
