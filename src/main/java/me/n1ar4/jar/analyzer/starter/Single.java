package me.n1ar4.jar.analyzer.starter;

import javax.swing.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class Single {
    private static final String LOCK_FILE = "jar-analyzer-lockfile";

    public static boolean canRun() {
        if (!isInstanceRunning()) {
            return true;
        } else {
            JOptionPane.showMessageDialog(null,
                    "Jar Analyzer is running");
            return false;
        }
    }

    private static boolean isInstanceRunning() {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(LOCK_FILE, "rw");
            FileLock lock = randomAccessFile.getChannel().tryLock();
            if (lock == null) {
                randomAccessFile.close();
                return true;
            }
            return false;
        } catch (OverlappingFileLockException | IOException e) {
            return true;
        }
    }
}