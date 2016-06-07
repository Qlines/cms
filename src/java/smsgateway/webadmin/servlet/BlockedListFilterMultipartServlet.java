package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.SubscriberBlocked;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BlockedListFilterMultipartServlet extends HttpServlet
{
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), "ISO8859_1");

    MultipartResponse multipartResponse = new MultipartResponse(response);
    try
    {
      try
      {
        MultipartRequest multipartRequest = new MultipartRequest(request, ".", 5242880);

        Enumeration files = multipartRequest.getFileNames();
        while (files.hasMoreElements()) {
          String name = (String)files.nextElement();
          File f = multipartRequest.getFile(name);
          String line = "";
          FileReader fr = new FileReader(f);
          BufferedReader br = new BufferedReader(fr);
          String msisdn = "";
          int ntotal = 0;
          int nblock = 0;

          multipartResponse.startResponse("text/txt;charset=tis-620");
          response.setHeader("Content-disposition", "attachment; filename=blocked_result.txt");
          response.setContentType("text/txt;charset=tis-620");

          out.append("++++++++ Non-Blocked Subscribers ++++++++\r\n");
          while ((line = br.readLine()) != null) {
            msisdn = line.trim();
            System.out.print(msisdn);

            if ((msisdn.length() == 9) && (msisdn.charAt(0) == '8'))
              msisdn = "66" + msisdn;
            else if ((msisdn.length() != 11) || (!msisdn.matches("^66[689][0-9]{8}$")))
            {
              if ((msisdn.length() == 10) && (msisdn.matches("^0[689][0-9]{8}$"))) {
                msisdn = "66" + msisdn.substring(1);
              } else {
                out.append(line.trim() + "\twrong format\r\n");
                continue;
              }
            }
            ntotal++;
            try
            {
              SubscriberBlocked sub = new SubscriberBlocked(msisdn);

              System.out.println(sub.isBlocked() ? "blocked" : "unblocked");

              if (!sub.isBlocked()) {
                throw new Exception();
              }

              nblock++;
            } catch (Exception e) {
              System.out.print("unblocked:" + line);
              out.append(line.trim() + "\r\n");
            }
          }

          out.append("Total " + ntotal + " sub(s), Blocked " + nblock + " sub(s).\r\n");
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    } finally {
      multipartResponse.finish();
      out.close();
    }
  }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
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
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}