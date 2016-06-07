package smsgateway.webadmin.servlet;

import java.io.File;

public class UploadedFile {

    private String dir;
    private String filename;
    private String type;

    UploadedFile(String dir, String filename, String type) {
        this.dir = dir;
        this.filename = filename;
        this.type = type;
    }

    public String getContentType() {
        return this.type;
    }

    public String getFilesystemName() {
        return this.filename;
    }

    public File getFile() {
        if ((this.dir == null) || (this.filename == null)) {
            return null;
        }

        return new File(this.dir + File.separator + this.filename);
    }
}