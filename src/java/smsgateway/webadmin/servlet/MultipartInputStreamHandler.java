package smsgateway.webadmin.servlet;

import java.io.IOException;
import javax.servlet.ServletInputStream;

public class MultipartInputStreamHandler {

    ServletInputStream in;
    String boundary;
    int totalExpected;
    int totalRead = 0;
    byte[] buf = new byte[8192];

    public MultipartInputStreamHandler(ServletInputStream in, String boundary, int totalExpected) {
        this.in = in;
        this.boundary = boundary;
        this.totalExpected = totalExpected;
    }

    public String readLine() throws IOException {
        StringBuffer sbuf = new StringBuffer();
        int result;
        do {
            result = readLine(this.buf, 0, this.buf.length);
            if (result != -1) {
                sbuf.append(new String(this.buf, 0, result, "ISO-8859-1"));
            }
        } while (result == this.buf.length);

        if (sbuf.length() == 0) {
            return null;
        }

        sbuf.setLength(sbuf.length() - 2);
        return sbuf.toString();
    }

    public int readLine(byte[] b, int off, int len) throws IOException {
        if (this.totalRead >= this.totalExpected) {
            return -1;
        }

        int result = this.in.readLine(b, off, len);
        if (result > 0) {
            this.totalRead += result;
        }
        return result;
    }
}