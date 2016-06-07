package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.ServiceElement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.DBPoolManager;

public class MigrateSubServlet extends HttpServlet
{
  public int insertSub(String sql)
    throws Exception
  {
    int row = 0;

    DBPoolManager cp = new DBPoolManager();
    try
    {
      row = cp.execUpdate(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      cp.release();
    }
    return row;
  }

  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = response.getWriter();
    try
    {
      try
      {
        MultipartRequest multi = new MultipartRequest(request, ".", 5242880);

        out.println("<HTML>");
        out.println("<HEAD><TITLE>Subscriber Migration</TITLE></HEAD>");
        out.println("<BODY>");
        out.println("<H1>Information</H1>");

        out.println("<H3>Params:</H3>");
        out.println("<PRE>");
        Enumeration params = multi.getParameterNames();
        String srvc_main_id = "";
        OperConfig.CARRIER oper = null;
        while (params.hasMoreElements()) {
          String name = (String)params.nextElement();
          String value = multi.getParameter(name);
          out.println(name + " = " + value);
          if (name.equals("srvc_main_id"))
            srvc_main_id = value;
          else if (name.equals("oper_id")) {
            oper = OperConfig.CARRIER.fromId(Integer.parseInt(value));
          }
        }
        out.println("</PRE>");

        out.println("<H3>Files:</H3>");
        out.println("<PRE>");
        Enumeration files = multi.getFileNames();
        while (files.hasMoreElements()) {
          String name = (String)files.nextElement();
          String filename = multi.getFilesystemName(name);
          String type = multi.getContentType(name);
          File f = multi.getFile(name);
          out.println("name: " + name);
          out.println("filename: " + filename);
          out.println("type: " + type);
          if (f != null) {
            out.println("length: " + f.length());
            out.println();
          }
          out.println("content:<BR>");
          String line = "";
          FileReader fr = new FileReader(f);
          BufferedReader br = new BufferedReader(fr);

          String[] token = { "", "", "", "", "", "", "", "", "", "", "", "" };
          String sql = "INSERT IGNORE INTO mmbr_" + oper.toString().toLowerCase() + " (srvc_main_id, msisdn, ctnt_ctr, free_trial, rmdr_ctr, extd_ctr, register_date, unregister_date, expired_date, balanced_date, state, srvc_chrg_type_id, srvc_chrg_amnt) VALUES ";

          while ((line = br.readLine()) != null) {
            int i = 0;
            int pos = 0;
            while ((pos = line.indexOf('|')) != -1) {
              token[i] = line.substring(0, pos);
              line = line.substring(pos + 1);
              i++;
            }
            token[i] = line;

            ServiceElement se = new ServiceElement(Integer.parseInt(token[0]), oper.getId(), ServiceElement.SERVICE_TYPE.ALL.getId(), ServiceElement.SERVICE_STATUS.ALL.getId());

            sql = sql + "(" + token[0] + "," + "'" + token[1] + "'" + "," + token[2] + "," + token[3] + "," + token[4] + "," + token[5] + "," + "'" + token[6] + "'" + "," + (token[7].equals("NULL") ? "NULL" : new StringBuilder().append("'").append(token[7]).append("'").toString()) + "," + "'" + token[8] + "'" + "," + (token[9].equals("NULL") ? "NULL" : new StringBuilder().append("'").append(token[9]).append("'").toString()) + "," + token[10] + "," + se.srvc_chrg_type_id + "," + se.srvc_chrg_amnt + "),";
          }

          sql = sql.substring(0, sql.length() - 1);
          sql = sql + ";";
          out.println(sql);
          out.println("</PRE>");
          out.print("Insert new " + insertSub(sql) + " sub(s).");
        }
      } catch (Exception e) {
        out.println("<PRE>");
        e.printStackTrace(out);
        out.println("</PRE>");
      }
      out.println("</BODY></HTML>");
    } finally {
      out.close();
    }
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    processRequest(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    processRequest(request, response);
  }

  public String getServletInfo()
  {
    return "Short description";
  }
}