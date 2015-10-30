package org.cn.explorer.vo;

import java.io.File;

/**
 * Created by chenning on 2015/10/10.
 */
public class ExpItem {

    private File file;
    private String title;
    private String size;
    private String lastModified;
    private String contentType;
    private String permission;
    private String suffix;
    private boolean isChecked = false;
    private String childrenCount;
    private int position;

    public ExpItem() {
    }

    public ExpItem(File file, String title, String size, String lastModified, String contentType, String permission, String suffix, boolean isChecked, String childrenCount) {
        this.file = file;
        this.title = title;
        this.size = size;
        this.lastModified = lastModified;
        this.contentType = contentType;
        this.permission = permission;
        this.suffix = suffix;
        this.isChecked = isChecked;
        this.childrenCount = childrenCount;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size == null ? "" : size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLastModified() {
        return lastModified == null ? "" : lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getSuffix() {
        return suffix == null ? "" : suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(String childrenCount) {
        this.childrenCount = childrenCount;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "ExpItem{" +
                "file=" + file +
                ", title='" + title + '\'' +
                ", size='" + size + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", contentType='" + contentType + '\'' +
                ", suffix='" + suffix + '\'' +
                ", isChecked=" + isChecked +
                ", childrenCount='" + childrenCount + '\'' +
                '}';
    }
}
