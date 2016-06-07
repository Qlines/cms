package smsgateway.webadmin.servlet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 *
 * @author nack_ki
 */
public class HeaderServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println(
                    "<html>"
                    + "    <head>"
                    + "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
                    + "        <link href=\"./css/cv.css\" rel=\"stylesheet\" type=\"text/css\">"
                    + "    </head>"
                    + "    <body class=\"top\">"
                    + "        <span class=\"user_detail txt11\" style=\"position:absolute;top:5px;left:10px;vertical-align:middle\">"
                    + "            <b>User:</b> " + request.getRemoteUser() + " | <b>Server:</b> " + request.getServerName()
                    + "        </span>"
                    + "        <span style=\"position:absolute;top:37px;left:5px;\">"
                    + "            <img src=\"images/title.gif\" border=\"0\">"
                    + "        </span>"
                    + "        <span style=\"position:absolute;top:60px;left:0;width:100%;padding: 0 5px 0 5px\">"
                    + "            <hr class=\"line_thin05g\">"
                    + "        </span>"
                    + "        <span style=\"position:absolute;top:70px;left:0;width:100%\">"
                    + "            <span class=\"txt10 floatr powerby\" style=\"padding: 0 10px 0 10px\">"
                    + "                powered by Sun Java Glassfish&trade; Enterprise V2.1"
                    + "            </span>"
                    + "        </span>"
                    + "    </body>"
                    + "</html>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String encoding = "UTF-8";
        if (request.getCharacterEncoding() != null) {
            encoding = request.getCharacterEncoding();
        }
        String filename = request.getParameter("file");

        if ((filename != null) && (!filename.trim().isEmpty())) {
            filename = new String(filename.getBytes("ISO8859_1"), encoding);
            try {
                File file = new File(filename);
                if (file == null) {
                    throw new Exception("file could not be open!!");
                }

                String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1).trim();
                String type = "";
                /*
                 * MIME reference referred http://www.w3schools.com/media/media_mimeref.asp
                 * image/bmp	bmp
                 * image/gif	gif
                 * image/jpeg	jpe
                 * image/jpeg	jpeg
                 * image/jpeg	jpg
                 * image/png	png
                 * image/tiff	tif
                 * image/tiff	tiff
                 * audio/basic	au
                 * audio/basic	snd
                 * audio/mid	mid
                 * audio/mid	rmi
                 * audio/mpeg	mp3
                 * audio/x-aiff	aif
                 * audio/x-aiff	aifc
                 * audio/x-aiff	aiff
                 * audio/x-mpegurl	m3u
                 * audio/x-pn-realaudio	ra
                 * audio/x-pn-realaudio	ram
                 * audio/x-wav	wav
                 */
                if (extension.matches("(?i)(TXT|CSV)")) {
                    type = "text/plain";
                } else if (extension.matches("(?i)(AU|SND)")) {
                    type = "audio/basic";
                } else if (extension.matches("(?i)(MID|RMI)")) {
                    type = "audio/mid";
                } else if (extension.matches("(?i)(MP3)")) {
                    type = "audio/mpeg";
                } else if (extension.matches("(?i)(AIF|AIFC|AIFF)")) {
                    type = "audio/x-aiff";
                } else if (extension.matches("(?i)(M3U)")) {
                    type = "audio/mpegurl";
                } else if (extension.matches("(?i)(RA|RAM)")) {
                    type = "audio/x-pn-realaudio";
                } else if (extension.matches("(?i)(WAV)")) {
                    type = "audio/x-wav";
                } else if (extension.matches("(?i)(BMP)")) {
                    type = "image/bmp";
                } else if (extension.matches("(?i)(GIF)")) {
                    type = "image/gif";
                } else if (extension.matches("(?i)(JPE|JPEG|JPG)")) {
                    type = "image/jpeg";
                } else if (extension.matches("(?i)(PNG)")) {
                    type = "image/png";
                } else if (extension.matches("(?i)(TIF|TIFF)")) {
                    type = "image/tiff";
                } else {
                    throw new Exception("file extension isn't supported!!");
                }

                response.setContentType(type);
                response.setHeader("Content-type", type);
                response.setHeader("Content-disposition", "inline;filename=" + file.getName().substring(file.getName().lastIndexOf('/') + 1).trim());

                if (extension.matches("(?i)(BMP|GIF|JPE|JPEG|JPG|PNG|TIF|TIFF)")) {
                    BufferedImage image = ImageIO.read(file);

                    if (image != null) {
                        OutputStream outputStream = response.getOutputStream();
                        ImageIO.write(image, extension, outputStream);
                        outputStream.close();
                    }

                } else if (extension.matches("(?i)(AIF|AIFC|AIFF|AU|WAV|SND)")) {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                    AudioFileFormat format = AudioSystem.getAudioFileFormat(file);

                    if (audioInputStream != null) {
                        MultipartResponse multi = new MultipartResponse(response);
                        try {
                            multi.startResponse(type);
                            OutputStream outputStream = response.getOutputStream();
                            AudioSystem.write(audioInputStream, format.getType(), outputStream);
                            outputStream.close();
                            multi.endResponse();
                        } finally {
                            multi.finish();
                        }
                    }
                } else if (extension.matches("(?i)(MID|MIDI|RAW|MP3|MPG|MPEG|TXT|CSV)")) {
                    MultipartResponse multi = new MultipartResponse(response);
                    try {
                        FileInputStream fistream = new FileInputStream(file);
                        multi.startResponse(type);
                        OutputStream outputStream = response.getOutputStream();
                        int b = -1;
                        while ((b = fistream.read()) != -1) {
                            outputStream.write(b);
                        }
                        outputStream.close();
                        fistream.close();
                        multi.endResponse();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        multi.finish();
                    }
                }
            } catch (Exception e) {
                System.err.println("error load resource: " + filename);
                e.printStackTrace();
            } finally {
            }
        } else {
            processRequest(request, response);
        }
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}