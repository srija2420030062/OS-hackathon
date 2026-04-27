import java.io.*;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class SharedFile {
    private String fileName;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public SharedFile(String fileName) {
        this.fileName = fileName;
    }

    // WITH LOCK
    public void readFileWithLock(String readerName) {
        lock.readLock().lock();
        try {
            System.out.println(readerName + " started reading (WITH LOCK)...");
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(readerName + " read: " + line);
            }
            br.close();
            Thread.sleep(1000);
            System.out.println(readerName + " finished reading.\n");
        } catch (Exception e) {
            System.out.println(readerName + " error: " + e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
    }

    public void writeFileWithLock(String writerName, String content) {
        lock.writeLock().lock();
        try {
            System.out.println(writerName + " started writing (WITH LOCK)...");
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true));
            bw.write(content);
            bw.newLine();
            bw.close();
            Thread.sleep(1000);
            System.out.println(writerName + " wrote: " + content);
            System.out.println(writerName + " finished writing.\n");
        } catch (Exception e) {
            System.out.println(writerName + " error: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    // WITHOUT LOCK
    public void readFileWithoutLock(String readerName) {
        try {
            System.out.println(readerName + " started reading (WITHOUT LOCK)...");
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(readerName + " read: " + line);
            }
            br.close();
            Thread.sleep(1000);
            System.out.println(readerName + " finished reading.\n");
        } catch (Exception e) {
            System.out.println(readerName + " error: " + e.getMessage());
        }
    }

    public void writeFileWithoutLock(String writerName, String content) {
        try {
            System.out.println(writerName + " started writing (WITHOUT LOCK)...");
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true));
            bw.write(content);
            bw.newLine();
            bw.close();
            Thread.sleep(1000);
            System.out.println(writerName + " wrote: " + content);
            System.out.println(writerName + " finished writing.\n");
        } catch (Exception e) {
            System.out.println(writerName + " error: " + e.getMessage());
        }
    }
}

class ReaderThread extends Thread {
    private SharedFile sharedFile;
    private String readerName;
    private int choice;

    public ReaderThread(SharedFile sharedFile, String readerName, int choice) {
        this.sharedFile = sharedFile;
        this.readerName = readerName;
        this.choice = choice;
    }

    public void run() {
        if (choice == 1) {
            sharedFile.readFileWithLock(readerName);
        } else {
            sharedFile.readFileWithoutLock(readerName);
        }
    }
}

class WriterThread extends Thread {
    private SharedFile sharedFile;
    private String writerName;
    private String content;
    private int choice;

    public WriterThread(SharedFile sharedFile, String writerName, String content, int choice) {
        this.sharedFile = sharedFile;
        this.writerName = writerName;
        this.content = content;
        this.choice = choice;
    }

    public void run() {
        if (choice == 1) {
            sharedFile.writeFileWithLock(writerName, content);
        } else {
            sharedFile.writeFileWithoutLock(writerName, content);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SharedFile sharedFile = new SharedFile("shared.txt");

        System.out.println("Enter your choice:");
        System.out.println("1. Run WITH LOCK");
        System.out.println("2. Run WITHOUT LOCK");
        int choice = sc.nextInt();

        ReaderThread r1 = new ReaderThread(sharedFile, "Reader-1", choice);
        ReaderThread r2 = new ReaderThread(sharedFile, "Reader-2", choice);
        WriterThread w1 = new WriterThread(sharedFile, "Writer-1", "Data added by Writer-1", choice);
        WriterThread w2 = new WriterThread(sharedFile, "Writer-2", "Data added by Writer-2", choice);

        r1.start();
        r2.start();
        w1.start();
        w2.start();

        try {
            r1.join();
            r2.join();
            w1.join();
            w2.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted.");
        }

        System.out.println("Simulation completed.");
        sc.close();
    }
}