package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.OperConfig;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.DBPoolManager;

public class NumberingPlanAddMultipartServlet extends HttpServlet {

    private static List<String> genRange2(int start, int end) {
        List list = new ArrayList();

        int pos = start;

        int j = 0;
        boolean stop = false;
        while ((pos <= end) && (!stop)) {
            String tmp = Integer.toString(pos);

            int n = 0;
            int nzero = 0;
            while (((n = tmp.lastIndexOf("0")) >= 0) && (n == tmp.length() - 1)) {
                tmp = tmp.substring(0, n);
                nzero++;
            }

            if (tmp.isEmpty()) {
                break;
            }
            int smallest = (int) Math.pow(10.0D, nzero);

            int nloop = 10 - Integer.valueOf(tmp.substring(tmp.length() - 1)).intValue();

            for (int i = 0; (i < nloop) && (pos <= end); i++) {
                list.add(Integer.toString(pos / smallest));
                pos += smallest;
                if (pos / smallest * smallest + smallest - 1 > end) {
                    stop = true;
                    break;
                }

            }

            if (j > 100) {
                break;
            }
        }
        if (stop) {
            stop = false;

            j = 0;
            while ((pos <= end) && (!stop)) {
                String tmp = Integer.toString(pos);

                int n = 0;
                int nzero = 0;

                while (((n = tmp.lastIndexOf("0")) >= 0) && (n == tmp.length() - 1)) {
                    tmp = tmp.substring(0, n);
                    nzero++;
                }

                int smallest = 1;
                if (nzero > 0) {
                    smallest = (int) Math.pow(10.0D, nzero - 1);
                }

                if ((pos <= end) && (nzero == 0)) {
                    list.add(Integer.toString(pos / smallest));
                } else {
                    int nloop = Integer.valueOf(Integer.toString(end).substring(Integer.toString(end).length() - nzero, Integer.toString(end).length() - nzero + 1)).intValue();

                    for (int i = 0; (i < nloop) && (pos <= end); i++) {
                        list.add(Integer.toString(pos / smallest));
                        pos += smallest;
                        if (pos > end) {
                            stop = true;
                            break;
                        }

                    }

                    if (j > 100) {
                        break;
                    }
                }
            }
        }
        return list;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), "ISO8859_1");

        MultipartResponse multipartResponse = new MultipartResponse(response);
        try {
            try {
                MultipartRequest multipartRequest = new MultipartRequest(request, ".", 5242880);

                Enumeration files = multipartRequest.getFileNames();
                while (files.hasMoreElements()) {
                    String name = (String) files.nextElement();
                    File f = multipartRequest.getFile(name);
                    String line = "";
                    FileReader fr = new FileReader(f);
                    BufferedReader br = new BufferedReader(fr);
                    String msisdn = "";
                    int ntotal = 0;
                    int nsuccess = 0;

                    multipartResponse.startResponse("text/txt;charset=tis-620");
                    response.setHeader("Content-disposition", "attachment; filename=blocked_result.txt");
                    response.setContentType("text/txt;charset=tis-620");

                    String sql = "REPLACE nmbr_plan (number, oper_id) VALUES ";
                    String content = "";
                    String pcontent = "('%s', %d),";

                    List numbers = new ArrayList();

                    while ((line = br.readLine()) != null) {
                        try {
                            ntotal++;

                            if (!line.trim().isEmpty()) {
                                String number = "";
                                String number2 = "";
                                String soper = "";
                                List values = new ArrayList();

                                Scanner s = new Scanner(line).useDelimiter("\\s*,\\s*");
                                int count = 0;
                                try {
                                    while (s.hasNext()) {
                                        values.add(s.next());
                                        count++;
                                    }
                                } catch (IllegalStateException e) {
                                } finally {
                                    s.close();
                                }

                                if (count < 2) {
                                    throw new Exception("invalid parameter");
                                }

                                soper = (String) values.get(0);
                                number = (String) values.get(1);

                                if ((number.isEmpty()) || (soper.isEmpty())) {
                                    throw new Exception("malformed parameter");
                                }

                                if (count > 2) {
                                    number2 = (String) values.get(2);
                                }

                                OperConfig.CARRIER oper = null;
                                try {
                                    oper = OperConfig.CARRIER.valueOf(soper);
                                    if (oper == null) {
                                        throw new Exception();
                                    }
                                } catch (Exception e) {
                                    throw new Exception("invalid operator");
                                }

                                if (oper == OperConfig.CARRIER.AIS) {
                                    oper = OperConfig.CARRIER.AIS_LEGACY;
                                }

                                numbers.clear();
                                if (count > 2) {
                                    numbers = genRange2(Integer.valueOf(number).intValue(), Integer.valueOf(number2).intValue());
                                } else {
                                    numbers.add(number);
                                }

                                for (int i = 0; i < numbers.size(); i++) {
                                    content = content + String.format(pcontent, new Object[]{numbers.get(i), Integer.valueOf(oper.getId())});
                                }

                                nsuccess++;
                            }
                        } catch (Exception e) {
                            out.append(line + " - " + e.getMessage() + "\r\n");
                        }


                    }

                    int rows = 0;
                    if (nsuccess > 0) {
                        content = content.substring(0, content.length() - 1);

                        DBPoolManager cp = new DBPoolManager();
                        try {
                            rows = cp.execUpdate(sql + content);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            cp.release();
                        }
                    }

                    out.append("Total " + ntotal + " number(s), pass " + nsuccess + " number(s). inserted " + rows + " row(s)\r\n");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            multipartResponse.finish();
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    public String getServletInfo() {
        return "Short description";
    }
}