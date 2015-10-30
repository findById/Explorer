package org.cn.explorer.utils;

import org.cn.utils.IOUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

/**
 * Created by chenning on 2015/10/12.
 */
public class FileUtil {

    public static File createFile(String path) {
        File file = createParentFolder(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static boolean createFolder(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static File createParentFolder(String path) {
        try {
            File file = new File(path);
            File parent = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator)));
            if (!parent.exists()) {
                createParentFolder(parent.getPath());
                parent.mkdirs();
            }
            return file;
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    public static String getFileSize(File file) {
        try {
            if (file.isDirectory()) {
                return "";
            }
            long length = file.length();
            DecimalFormat df = new DecimalFormat("##0.##");
            float f;
            if (length < 1024 * 1024) {
                f = (float) ((float) length / (float) 1024);
                return (df.format(new Float(f).doubleValue()) + "KB");
            } else if (length < 1024 * 1024 * 1024) {
                f = (float) ((float) length / (float) (1024 * 1024));
                return (df.format(new Float(f).doubleValue()) + "MB");
            } else {
                f = (float) ((float) length / (float) (1024 * 1024 * 1024));
                return (df.format(new Float(f).doubleValue()) + "GB");
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFileChildrenCount(File file) {
        try {
            if (file == null || file.isFile()) {
                return "0";
            }
            return String.valueOf(file.listFiles().length);
        } catch (Throwable e) {
            // ignore
        }
        return "0";
    }


    /**
     * 删除文件
     *
     * @param file
     */
    public synchronized static boolean deleteFile(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteFile(f);
                if (f.isDirectory()) {
                    // 如果不删除文件夹 可不做此处理
                    f.delete();
                }
            }
            // 删除根目录
            file.delete();
        }
        return true;
    }

    private static FileFilter filter = new FileFilter() {
        String[] FILE_TYPE = {"Thumbs.db", ".DS_Store"};

        @Override
        public boolean accept(File pathname) {
            for (int i = 0; i < FILE_TYPE.length; i++) {
                if (FILE_TYPE[i].equalsIgnoreCase(pathname.getName())) {
                    return false;
                }
            }
            return true;
        }

    };

    public static void copyFile(File src, File dest) throws Exception {
        iterates(src, dest, filter);
    }

    private static void iterates(File src, File dest, FileFilter filter) throws Exception {
        if (src.isFile()) {
            copyFileWithChannel(src, dest);
        } else if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdirs();
            } else if (!dest.isDirectory()) {
                throw new RuntimeException(dest.getAbsolutePath() + " is not a directory.");
            }
            File[] files = src.listFiles(filter);
            for (int i = 0; i < files.length; i++) {
                copyIterates(files[i], dest, filter);
            }
        }
    }

    private static void copyIterates(File copyIn, File copyOut, FileFilter filter) throws IOException {
        if (copyIn.isFile()) {
            String newFile = copyOut.getAbsolutePath() + File.separator + copyIn.getName();
            copyFileWithChannel(copyIn, new File(newFile));
        } else if (copyIn.isDirectory()) {
            File newOut = new File(copyOut.getAbsoluteFile() + File.separator + copyIn.getName());
            newOut.mkdirs();
            File[] files = copyIn.listFiles(filter);
            for (int i = 0; i < files.length; i++) {
                copyIterates(files[i], newOut, filter);
            }
        }
    }

    public static void copyFileWithChannel(File src, File dest) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;

        FileChannel in = null;
        FileChannel out = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dest);

            in = fis.getChannel();// 得到对应的文件通道
            out = fos.getChannel();// 得到对应的文件通道
            in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
        } finally {
            IOUtil.closeQuietly(fis, fos, in, out);
        }
    }

}
