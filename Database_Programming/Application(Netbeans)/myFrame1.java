
package mydbproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.CallableStatement;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;


/**
 *
 * @author 우민
 */
public class myFrame1 extends javax.swing.JFrame {
    Connection  conn = null;
    String gs = null;
    /**
     * Creates new form myFrame1
     */
    public myFrame1() {
        initComponents();
        conn = myConnection.makeConn();
        addNodestoTree();
    }
    
    // show all rows corresponding to selected table name
    private void showData(String tn) {
        String sql1 = "select * from " + tn + "";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql1);
            ResultSet rs = pstmt.executeQuery();
            
            
            jTable1.setModel(DbUtils.resultSetToTableModel(rs));
            jTable1.setRowSelectionInterval(0, 0);
            
        }
        catch(Exception e) {
            
        }
    }
    
    // some functions for Certificate
    private void showCertificate() {
        int selectedRow = jTable1.getSelectedRow();
        btnSearch.setVisible(true);
        btnAdd.setVisible(true);
        btnDelete.setVisible(true);
        btnNew.setVisible(true);
        btnPrint.setVisible(true);
        btnUpdate.setVisible(true);
        jTextField1.setVisible(true);
        jTextField2.setVisible(true);
        jTextField3.setVisible(true);
        jTextField4.setVisible(true);
        jTextField5.setVisible(true);
        jTextField6.setVisible(true);
        jTextField7.setVisible(true);
        jTextField8.setVisible(true);
        jTextField10.setVisible(true);
        jTextField11.setVisible(true);
        jTextField12.setVisible(true);
        jTextField1.setText(jTable1.getValueAt(selectedRow, 0).toString());
        jTextField2.setText(jTable1.getValueAt(selectedRow, 1).toString());
        jTextField3.setText(jTable1.getValueAt(selectedRow, 2).toString());
        jTextField4.setVisible(false);
        jTextField5.setVisible(false);
        jTextField6.setVisible(false);
        jTextField7.setVisible(false);
        jTextField8.setVisible(false);
        jTextField10.setVisible(false);
        jTextField11.setVisible(false);
        jTextField12.setVisible(false);
        stext2.setVisible(false);
        
        jLabel1.setVisible(true);
        jLabel2.setVisible(true);
        jLabel3.setVisible(true);
        jLabel4.setVisible(true);
        jLabel5.setVisible(true);
        jLabel6.setVisible(true);
        jLabel7.setVisible(true);
        jLabel8.setVisible(true);
        jLabel9.setVisible(true);
        jLabel10.setVisible(true);
        jLabel11.setVisible(true);
        jLabel1.setText(jTable1.getModel().getColumnName(0));
        jLabel2.setText(jTable1.getModel().getColumnName(1));
        jLabel3.setText(jTable1.getModel().getColumnName(2));
        jLabel4.setVisible(false);
        jLabel5.setVisible(false);
        jLabel6.setVisible(false);
        jLabel7.setVisible(false);
        jLabel8.setVisible(false);
        jLabel9.setVisible(false);
        jLabel10.setVisible(false);
        jLabel11.setVisible(false);
        slabel2.setVisible(false);
        
        slabel.setText("Medicine name");
        stext.setText("");
    }
    private void searchCertificate() {
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      String sql = null;

        try {
            if(stext.getText().equals("")) {
                sql = "select * from " + gs + "";
                pstmt = conn.prepareStatement(sql);
            }
            else {
                sql  = "select * from " + gs + " where medicine_name = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, stext.getText());
            }
            
            //pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            jTable1.setModel(rstoTable(rs));
            jTable1.setRowSelectionInterval(0, 0);
            
            showCertificate();

        }
        catch(Exception e) {
            System.err.printf(e.getMessage());
        }
        finally {
            try {
                pstmt.close();
                rs.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }
    } 
    private void clearCertificate() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
    }
    private void addCertificate() {
      CallableStatement cst = null;
      String sql = null;
        try {
            sql  = "{call addCertificateRecord(?, ?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, jTextField1.getText());
            cst.setString(2, jTextField2.getText());
            cst.setString(3, jTextField3.getText());
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record added");
            showData(gs);
            showCertificate();
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    private void deleteCertificate() {
      CallableStatement cst = null;
      String sql = null;
        
        try {
            int rownum = jTable1.getSelectedRow();
            String cer_name = jTable1.getModel().getValueAt(rownum, 0).toString();
            String med_name = jTable1.getModel().getValueAt(rownum, 1).toString();
            sql  = "{call deleteCertificateRecord(?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, cer_name);
            cst.setString(2, med_name);
            // cst.setInt(1, Integer.parseInt(id)); // for integer
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record is deleted");
            showData(gs);
            showCertificate();
            
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    private void updateCertificate() {
      CallableStatement cst = null;
      String sql = null;
        try {
            sql  = "{call updateCertificateRecord(?, ?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, jTextField1.getText());
            cst.setString(2, jTextField2.getText());
            cst.setString(3, jTextField3.getText());
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record is updated");
            showData(gs);
            showCertificate();
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    
    // some functions for Certification
    private void showCertification() {
        int selectedRow = jTable1.getSelectedRow();
        btnSearch.setVisible(true);
        btnAdd.setVisible(true);
        btnDelete.setVisible(true);
        btnNew.setVisible(true);
        btnPrint.setVisible(true);
        btnUpdate.setVisible(true);
        jTextField1.setVisible(true);
        jTextField2.setVisible(true);
        jTextField3.setVisible(true);
        jTextField4.setVisible(true);
        jTextField5.setVisible(true);
        jTextField6.setVisible(true);
        jTextField7.setVisible(true);
        jTextField8.setVisible(true);
        jTextField10.setVisible(true);
        jTextField11.setVisible(true);
        jTextField12.setVisible(true);
        jTextField1.setText(jTable1.getValueAt(selectedRow, 0).toString());
        jTextField2.setText(jTable1.getValueAt(selectedRow, 1).toString());
        jTextField3.setText(jTable1.getValueAt(selectedRow, 2).toString());
        jTextField4.setText(jTable1.getValueAt(selectedRow, 3).toString());
        jTextField5.setVisible(false);
        jTextField6.setVisible(false);
        jTextField7.setVisible(false);
        jTextField8.setVisible(false);
        jTextField10.setVisible(false);
        jTextField11.setVisible(false);
        jTextField12.setVisible(false);
        stext2.setVisible(false);
        
        jLabel1.setVisible(true);
        jLabel2.setVisible(true);
        jLabel3.setVisible(true);
        jLabel4.setVisible(true);
        jLabel5.setVisible(true);
        jLabel6.setVisible(true);
        jLabel7.setVisible(true);
        jLabel8.setVisible(true);
        jLabel9.setVisible(true);
        jLabel10.setVisible(true);
        jLabel11.setVisible(true);
        jLabel1.setText(jTable1.getModel().getColumnName(0));
        jLabel2.setText(jTable1.getModel().getColumnName(1));
        jLabel3.setText(jTable1.getModel().getColumnName(2));
        jLabel4.setText(jTable1.getModel().getColumnName(3));
        jLabel5.setVisible(false);
        jLabel6.setVisible(false);
        jLabel7.setVisible(false);
        jLabel8.setVisible(false);
        jLabel9.setVisible(false);
        jLabel10.setVisible(false);
        jLabel11.setVisible(false);
        slabel2.setVisible(false);
        
        slabel.setText("Certification name");
        stext.setText("");
    }
    private void searchCertification() {
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      String sql = null;

        try {
            if(stext.getText().equals("")) {
                sql = "select * from " + gs + "";
                pstmt = conn.prepareStatement(sql);
            }
            else {
                sql  = "select * from " + gs + " where certification_name = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, stext.getText());
            }
            
            //pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            jTable1.setModel(rstoTable(rs));
            jTable1.setRowSelectionInterval(0, 0);
            showCertification();

        }
        catch(Exception e) {
            System.err.printf(e.getMessage());
        }
        finally {
            try {
                pstmt.close();
                rs.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }
    }
    private void clearCertification() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setVisible(false);
    }
    private void addCertification() {
      CallableStatement cst = null;
      String sql = null;
        try {
            sql  = "{call addCertificationRecord(?, ?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, jTextField1.getText());
            cst.setString(2, jTextField2.getText());
            cst.setString(3, jTextField3.getText());
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record added");
            showData(gs);
            showCertification();
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    private void deleteCertification() {
      CallableStatement cst = null;
      String sql = null;
        
        try {
            int rownum = jTable1.getSelectedRow();
            String cer_name = jTable1.getModel().getValueAt(rownum, 0).toString();
            sql  = "{call deleteCertificationRecord(?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, cer_name);
            // cst.setInt(1, Integer.parseInt(id)); // for integer
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record is deleted");
            showData(gs);
            showCertification();
            
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    private void updateCertification() {
      CallableStatement cst = null;
      String sql = null;
        try {
            sql  = "{call updateCertificationRecord(?, ?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, jTextField1.getText());
            cst.setString(2, jTextField2.getText());
            cst.setString(3, jTextField3.getText());
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record is updated");
            showData(gs);
            showCertification();
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
        
    // some functions for Company
    private void showCompany() {
        int selectedRow = jTable1.getSelectedRow();
        btnSearch.setVisible(true);
        btnAdd.setVisible(true);
        btnDelete.setVisible(true);
        btnNew.setVisible(true);
        btnPrint.setVisible(true);
        btnUpdate.setVisible(true);
        jTextField1.setVisible(true);
        jTextField2.setVisible(true);
        jTextField3.setVisible(true);
        jTextField4.setVisible(true);
        jTextField5.setVisible(true);
        jTextField6.setVisible(true);
        jTextField7.setVisible(true);
        jTextField8.setVisible(true);
        jTextField10.setVisible(true);
        jTextField11.setVisible(true);
        jTextField12.setVisible(true);
        jTextField1.setText(jTable1.getValueAt(selectedRow, 0).toString());
        jTextField2.setText(jTable1.getValueAt(selectedRow, 1).toString());
        jTextField3.setText(jTable1.getValueAt(selectedRow, 2).toString());
        jTextField4.setText(jTable1.getValueAt(selectedRow, 3).toString());
        jTextField5.setText(jTable1.getValueAt(selectedRow, 4).toString());
        jTextField6.setText(jTable1.getValueAt(selectedRow, 5).toString());
        jTextField7.setText(jTable1.getValueAt(selectedRow, 6).toString());
        jTextField8.setVisible(false);
        jTextField10.setVisible(false);
        jTextField11.setVisible(false);
        jTextField12.setVisible(false);
        stext2.setVisible(false);
        
        jLabel1.setVisible(true);
        jLabel2.setVisible(true);
        jLabel3.setVisible(true);
        jLabel4.setVisible(true);
        jLabel5.setVisible(true);
        jLabel6.setVisible(true);
        jLabel7.setVisible(true);
        jLabel8.setVisible(true);
        jLabel9.setVisible(true);
        jLabel10.setVisible(true);
        jLabel11.setVisible(true);
        jLabel1.setText(jTable1.getModel().getColumnName(0));
        jLabel2.setText(jTable1.getModel().getColumnName(1));
        jLabel3.setText(jTable1.getModel().getColumnName(2));
        jLabel4.setText(jTable1.getModel().getColumnName(3));
        jLabel5.setText(jTable1.getModel().getColumnName(4));
        jLabel6.setText(jTable1.getModel().getColumnName(5));
        jLabel7.setText(jTable1.getModel().getColumnName(6));
        jLabel8.setVisible(false);
        jLabel9.setVisible(false);
        jLabel10.setVisible(false);
        jLabel11.setVisible(false);
        slabel2.setVisible(false);
        
        slabel.setText("Company name");
        stext.setText("");
    }
    private void searchCompany() {
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      String sql = null;

        try {
            if(stext.getText().equals("")) {
                sql = "select * from " + gs + "";
                pstmt = conn.prepareStatement(sql);
            }
            else {
                sql  = "select * from " + gs + " where company_name = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, stext.getText());
            }
            
            //pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            jTable1.setModel(rstoTable(rs));
            jTable1.setRowSelectionInterval(0, 0);
            showCompany();

        }
        catch(Exception e) {
            System.err.printf(e.getMessage());
        }
        finally {
            try {
                pstmt.close();
                rs.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }
    }
    private void clearCompany() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jTextField7.setVisible(false);
    }
    private void addCompany() {
      CallableStatement cst = null;
      String sql = null;
        try {
            sql  = "{call addCompanyRecord(?, ?, ?, ?, ?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, jTextField1.getText());
            cst.setString(2, jTextField2.getText());
            cst.setString(3, jTextField3.getText());
            cst.setString(4, jTextField4.getText());
            cst.setString(5, jTextField5.getText());
            cst.setString(6, jTextField6.getText());
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record added");
            showData(gs);
            showCompany();
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    private void deleteCompany() {
      CallableStatement cst = null;
      String sql = null;
        
        try {
            int rownum = jTable1.getSelectedRow();
            String com_name = jTable1.getModel().getValueAt(rownum, 0).toString();
            sql  = "{call deleteCompanyRecord(?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, com_name);
            // cst.setInt(1, Integer.parseInt(id)); // for integer
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record is deleted");
            showData(gs);
            showCompany();
            
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    private void updateCompany() {
      CallableStatement cst = null;
      String sql = null;
        try {
            sql  = "{call updateCompanyRecord(?, ?, ?, ?, ?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, jTextField1.getText());
            cst.setString(2, jTextField2.getText());
            cst.setString(3, jTextField3.getText());
            cst.setString(4, jTextField4.getText());
            cst.setString(5, jTextField5.getText());
            cst.setString(6, jTextField6.getText());
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record is updated");
            showData(gs);
            showCompany();
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
            
    // some function for Log
    private void showLog() {
        int selectedRow = jTable1.getSelectedRow();
        btnAdd.setVisible(false);
        btnDelete.setVisible(false);
        btnNew.setVisible(false);
        btnPrint.setVisible(false);
        btnUpdate.setVisible(false);
        jTextField1.setVisible(true);
        jTextField2.setVisible(true);
        jTextField3.setVisible(true);
        jTextField4.setVisible(true);
        jTextField5.setVisible(true);
        jTextField6.setVisible(true);
        jTextField7.setVisible(true);
        jTextField8.setVisible(true);
        jTextField10.setVisible(true);
        jTextField11.setVisible(true);
        jTextField12.setVisible(true);
        jTextField1.setText(jTable1.getValueAt(selectedRow, 0).toString());
        jTextField2.setText(jTable1.getValueAt(selectedRow, 1).toString());
        jTextField3.setText(jTable1.getValueAt(selectedRow, 2).toString());
        jTextField4.setText(jTable1.getValueAt(selectedRow, 3).toString());
        jTextField5.setText(jTable1.getValueAt(selectedRow, 4).toString());
        jTextField6.setText(jTable1.getValueAt(selectedRow, 5).toString());
        jTextField7.setVisible(false);
        jTextField8.setVisible(false);
        jTextField10.setVisible(false);
        jTextField11.setVisible(false);
        jTextField12.setVisible(false);
        stext2.setVisible(true);
        
        jLabel1.setVisible(true);
        jLabel2.setVisible(true);
        jLabel3.setVisible(true);
        jLabel4.setVisible(true);
        jLabel5.setVisible(true);
        jLabel6.setVisible(true);
        jLabel7.setVisible(true);
        jLabel8.setVisible(true);
        jLabel9.setVisible(true);
        jLabel10.setVisible(true);
        jLabel11.setVisible(true);
        jLabel1.setText(jTable1.getModel().getColumnName(0));
        jLabel2.setText(jTable1.getModel().getColumnName(1));
        jLabel3.setText(jTable1.getModel().getColumnName(2));
        jLabel4.setText(jTable1.getModel().getColumnName(3));
        jLabel5.setText(jTable1.getModel().getColumnName(4));
        jLabel6.setText(jTable1.getModel().getColumnName(5));
        jLabel7.setVisible(false);
        jLabel8.setVisible(false);
        jLabel9.setVisible(false);
        jLabel10.setVisible(false);
        jLabel11.setVisible(false);
        slabel2.setVisible(true);
        
        slabel.setText("Table name");
        stext.setText("");
        slabel2.setText("Operation");
        stext2.setText("");
    }
    private void searchLog() {
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      String sql = null;

        try {
            if(stext.getText().equals("") && stext2.getText().equals("")) {
                sql = "select * from " + gs + "";
                pstmt = conn.prepareStatement(sql);
            }
            else if(!stext.getText().equals("") && stext2.getText().equals("")){
                sql  = "select * from " + gs + " where log_table = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, stext.getText());
            }
            else if(stext.getText().equals("") && !stext2.getText().equals("")){
                sql  = "select * from " + gs + " where log_operation = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, stext2.getText());
            }
            else {
                sql  = "select * from " + gs + " where log_table = ? and log_operation = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, stext.getText());
                pstmt.setString(2, stext2.getText());
            }
            
            //pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            jTable1.setModel(rstoTable(rs));
            jTable1.setRowSelectionInterval(0, 0);
            showLog();

        }
        catch(Exception e) {
            System.err.printf(e.getMessage());
        }
        finally {
            try {
                pstmt.close();
                rs.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }
    }
    
    // some functions for Medicine
    private void showMedicine() {
        int selectedRow = jTable1.getSelectedRow();
        btnSearch.setVisible(true);
        btnAdd.setVisible(true);
        btnDelete.setVisible(true);
        btnNew.setVisible(true);
        btnPrint.setVisible(true);
        btnUpdate.setVisible(true);
        jTextField1.setVisible(true);
        jTextField2.setVisible(true);
        jTextField3.setVisible(true);
        jTextField4.setVisible(true);
        jTextField5.setVisible(true);
        jTextField6.setVisible(true);
        jTextField7.setVisible(true);
        jTextField8.setVisible(true);
        jTextField10.setVisible(true);
        jTextField11.setVisible(true);
        jTextField12.setVisible(true);
        jTextField1.setText(jTable1.getValueAt(selectedRow, 0).toString());
        jTextField2.setText(jTable1.getValueAt(selectedRow, 1).toString());
        jTextField3.setText(jTable1.getValueAt(selectedRow, 2).toString());
        jTextField4.setText(jTable1.getValueAt(selectedRow, 3).toString());
        jTextField5.setText(jTable1.getValueAt(selectedRow, 4).toString());
        jTextField6.setText(jTable1.getValueAt(selectedRow, 5).toString());
        jTextField7.setText(jTable1.getValueAt(selectedRow, 6).toString());
        jTextField8.setText(jTable1.getValueAt(selectedRow, 7).toString());
        jTextField10.setText(jTable1.getValueAt(selectedRow, 8).toString());
        jTextField11.setText(jTable1.getValueAt(selectedRow, 9).toString());
        jTextField12.setText(jTable1.getValueAt(selectedRow, 10).toString());
        stext2.setVisible(false);
        
        jLabel1.setVisible(true);
        jLabel2.setVisible(true);
        jLabel3.setVisible(true);
        jLabel4.setVisible(true);
        jLabel5.setVisible(true);
        jLabel6.setVisible(true);
        jLabel7.setVisible(true);
        jLabel8.setVisible(true);
        jLabel9.setVisible(true);
        jLabel10.setVisible(true);
        jLabel11.setVisible(true);
        jLabel1.setText(jTable1.getModel().getColumnName(0));
        jLabel2.setText(jTable1.getModel().getColumnName(1));
        jLabel3.setText(jTable1.getModel().getColumnName(2));
        jLabel4.setText(jTable1.getModel().getColumnName(3));
        jLabel5.setText(jTable1.getModel().getColumnName(4));
        jLabel6.setText(jTable1.getModel().getColumnName(5));
        jLabel7.setText(jTable1.getModel().getColumnName(6));
        jLabel8.setText(jTable1.getModel().getColumnName(7));
        jLabel9.setText(jTable1.getModel().getColumnName(8));
        jLabel10.setText(jTable1.getModel().getColumnName(9));
        jLabel11.setText(jTable1.getModel().getColumnName(10));
        slabel2.setVisible(false);
        
        slabel.setText("Medicine name");
        stext.setText("");
    }
    private void searchMedicine() {
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      String sql = null;

        try {
            if(stext.getText().equals("")) {
                sql = "select * from " + gs + "";
                pstmt = conn.prepareStatement(sql);
            }
            else {
                sql  = "select * from " + gs + " where medicine_name = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, stext.getText());
            }
            
            //pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            jTable1.setModel(rstoTable(rs));
            jTable1.setRowSelectionInterval(0, 0);
            showMedicine();

        }
        catch(Exception e) {
            System.err.printf(e.getMessage());
        }
        finally {
            try {
                pstmt.close();
                rs.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }
    }
    private void clearMedicine() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jTextField7.setText("");
        jTextField8.setText("");
        jTextField10.setText("");
        jTextField11.setVisible(false);
        jTextField12.setText("");
    }
    private void addMedicine() {
      CallableStatement cst = null;
      String sql = null;
        try {
            sql  = "{call addMedicineRecord(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, jTextField1.getText());
            cst.setString(2, jTextField2.getText());
            cst.setString(3, jTextField3.getText());
            cst.setString(4, jTextField4.getText());
            cst.setString(5, jTextField5.getText());
            cst.setString(6, jTextField6.getText());
            cst.setString(7, jTextField7.getText());
            cst.setString(8, jTextField8.getText());
            cst.setString(9, jTextField10.getText());
            cst.setString(12, jTextField12.getText());
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record added");
            showData(gs);
            showMedicine();
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    private void deleteMedicine() {
      CallableStatement cst = null;
      String sql = null;
        
        try {
            int rownum = jTable1.getSelectedRow();
            String med_name = jTable1.getModel().getValueAt(rownum, 0).toString();
            sql  = "{call deleteMedicineRecord(?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, med_name);
            // cst.setInt(1, Integer.parseInt(id)); // for integer
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record is deleted");
            showData(gs);
            showMedicine();
            
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    private void updateMedicine() {
      CallableStatement cst = null;
      String sql = null;
        try {
            sql  = "{call updateMedicineRecord(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, jTextField1.getText());
            cst.setString(2, jTextField2.getText());
            cst.setString(3, jTextField3.getText());
            cst.setString(4, jTextField4.getText());
            cst.setString(5, jTextField5.getText());
            cst.setString(6, jTextField6.getText());
            cst.setString(7, jTextField7.getText());
            cst.setString(8, jTextField8.getText());
            cst.setString(9, jTextField10.getText());
            cst.setString(10, jTextField12.getText());
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record is updated");
            showData(gs);
            showMedicine();
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    
    // some functions for Pharmacy
    private void showPharmacy() {
        int selectedRow = jTable1.getSelectedRow();
        btnSearch.setVisible(true);
        btnAdd.setVisible(true);
        btnDelete.setVisible(true);
        btnNew.setVisible(true);
        btnPrint.setVisible(true);
        btnUpdate.setVisible(true);
        jTextField1.setVisible(true);
        jTextField2.setVisible(true);
        jTextField3.setVisible(true);
        jTextField4.setVisible(true);
        jTextField5.setVisible(true);
        jTextField6.setVisible(true);
        jTextField7.setVisible(true);
        jTextField8.setVisible(true);
        jTextField10.setVisible(true);
        jTextField11.setVisible(true);
        jTextField12.setVisible(true);
        jTextField1.setText(jTable1.getValueAt(selectedRow, 0).toString());
        jTextField2.setText(jTable1.getValueAt(selectedRow, 1).toString());
        jTextField3.setText(jTable1.getValueAt(selectedRow, 2).toString());
        jTextField4.setText(jTable1.getValueAt(selectedRow, 3).toString());
        jTextField5.setText(jTable1.getValueAt(selectedRow, 4).toString());
        jTextField6.setText(jTable1.getValueAt(selectedRow, 5).toString());
        jTextField7.setVisible(false);
        jTextField8.setVisible(false);
        jTextField10.setVisible(false);
        jTextField11.setVisible(false);
        jTextField12.setVisible(false);
        stext2.setVisible(false);
        
        jLabel1.setVisible(true);
        jLabel2.setVisible(true);
        jLabel3.setVisible(true);
        jLabel4.setVisible(true);
        jLabel5.setVisible(true);
        jLabel6.setVisible(true);
        jLabel7.setVisible(true);
        jLabel8.setVisible(true);
        jLabel9.setVisible(true);
        jLabel10.setVisible(true);
        jLabel11.setVisible(true);
        jLabel1.setText(jTable1.getModel().getColumnName(0));
        jLabel2.setText(jTable1.getModel().getColumnName(1));
        jLabel3.setText(jTable1.getModel().getColumnName(2));
        jLabel4.setText(jTable1.getModel().getColumnName(3));
        jLabel5.setText(jTable1.getModel().getColumnName(4));
        jLabel6.setText(jTable1.getModel().getColumnName(5));
        jLabel7.setVisible(false);
        jLabel8.setVisible(false);
        jLabel9.setVisible(false);
        jLabel10.setVisible(false);
        jLabel11.setVisible(false);
        slabel2.setVisible(false);
        
        slabel.setText("Pharmacy name");
        stext.setText("");
    }
    private void searchPharmacy() {
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      String sql = null;

        try {
            if(stext.getText().equals("")) {
                sql = "select * from " + gs + "";
                pstmt = conn.prepareStatement(sql);
            }
            else {
                sql  = "select * from " + gs + " where pharmacy_name = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, stext.getText());
            }
            
            //pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            jTable1.setModel(rstoTable(rs));
            jTable1.setRowSelectionInterval(0, 0);
            showPharmacy();

        }
        catch(Exception e) {
            System.err.printf(e.getMessage());
        }
        finally {
            try {
                pstmt.close();
                rs.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }
    }
    private void clearPharmacy() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setVisible(false);
    }
    private void addPharmacy() {
      CallableStatement cst = null;
      String sql = null;
        try {
            sql  = "{call addPharmacyRecord(?, ?, ?, ?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, jTextField1.getText());
            cst.setString(2, jTextField2.getText());
            cst.setString(3, jTextField3.getText());
            cst.setString(4, jTextField4.getText());
            cst.setString(5, jTextField5.getText());
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record added");
            showData(gs);
            showPharmacy();
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    private void deletePharmacy() {
      CallableStatement cst = null;
      String sql = null;
        
        try {
            int rownum = jTable1.getSelectedRow();
            String phar_name = jTable1.getModel().getValueAt(rownum, 0).toString();
            sql  = "{call deletePharmacyRecord(?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, phar_name);
            // cst.setInt(1, Integer.parseInt(id)); // for integer
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record is deleted");
            showData(gs);
            showPharmacy();
            
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    private void updatePharmacy() {
      CallableStatement cst = null;
      String sql = null;
        try {
            sql  = "{call updatePharmacyRecord(?, ?, ?, ?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, jTextField1.getText());
            cst.setString(2, jTextField2.getText());
            cst.setString(3, jTextField3.getText());
            cst.setString(4, jTextField4.getText());
            cst.setString(5, jTextField5.getText());
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record is updated");
            showData(gs);
            showPharmacy();
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    
    // some functions for Sell
    private void showSell() {
        int selectedRow = jTable1.getSelectedRow();
        btnSearch.setVisible(true);
        btnAdd.setVisible(true);
        btnDelete.setVisible(true);
        btnNew.setVisible(true);
        btnPrint.setVisible(true);
        btnUpdate.setVisible(true);
        jTextField1.setVisible(true);
        jTextField2.setVisible(true);
        jTextField3.setVisible(true);
        jTextField4.setVisible(true);
        jTextField5.setVisible(true);
        jTextField6.setVisible(true);
        jTextField7.setVisible(true);
        jTextField8.setVisible(true);
        jTextField10.setVisible(true);
        jTextField11.setVisible(true);
        jTextField12.setVisible(true);
        jTextField1.setText(jTable1.getValueAt(selectedRow, 0).toString());
        jTextField2.setText(jTable1.getValueAt(selectedRow, 1).toString());
        jTextField3.setText(jTable1.getValueAt(selectedRow, 2).toString());
        jTextField4.setVisible(false);
        jTextField5.setVisible(false);
        jTextField6.setVisible(false);
        jTextField7.setVisible(false);
        jTextField8.setVisible(false);
        jTextField10.setVisible(false);
        jTextField11.setVisible(false);
        jTextField12.setVisible(false);
        stext2.setVisible(false);
        
        jLabel1.setVisible(true);
        jLabel2.setVisible(true);
        jLabel3.setVisible(true);
        jLabel4.setVisible(true);
        jLabel5.setVisible(true);
        jLabel6.setVisible(true);
        jLabel7.setVisible(true);
        jLabel8.setVisible(true);
        jLabel9.setVisible(true);
        jLabel10.setVisible(true);
        jLabel11.setVisible(true);
        jLabel1.setText(jTable1.getModel().getColumnName(0));
        jLabel2.setText(jTable1.getModel().getColumnName(1));
        jLabel3.setText(jTable1.getModel().getColumnName(2));
        jLabel4.setVisible(false);
        jLabel5.setVisible(false);
        jLabel6.setVisible(false);
        jLabel7.setVisible(false);
        jLabel8.setVisible(false);
        jLabel9.setVisible(false);
        jLabel10.setVisible(false);
        jLabel11.setVisible(false);
        slabel2.setVisible(false);
        
        slabel.setText("Medicine name");
        stext.setText("");
    }
    private void searchSell() {
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      String sql = null;

        try {
            if(stext.getText().equals("")) {
                sql = "select * from " + gs + "";
                pstmt = conn.prepareStatement(sql);
            }
            else {
                sql  = "select * from " + gs + " where medicine_name = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, stext.getText());
            }
            
            //pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            jTable1.setModel(rstoTable(rs));
            jTable1.setRowSelectionInterval(0, 0);
            showSell();

        }
        catch(Exception e) {
            System.err.printf(e.getMessage());
        }
        finally {
            try {
                pstmt.close();
                rs.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }
    }
    private void clearSell() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
    }
    private void addSell() {
      CallableStatement cst = null;
      String sql = null;
        try {
            sql  = "{call addSellRecord(?, ?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, jTextField1.getText());
            cst.setString(2, jTextField2.getText());
            cst.setString(3, jTextField3.getText());
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record added");
            showData(gs);
            showSell();
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    private void deleteSell() {
      CallableStatement cst = null;
      String sql = null;
        
        try {
            int rownum = jTable1.getSelectedRow();
            String med_name = jTable1.getModel().getValueAt(rownum, 0).toString();
            String phar_name = jTable1.getModel().getValueAt(rownum, 1).toString();
            sql  = "{call deleteSellRecord(?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, med_name);
            cst.setString(2, phar_name);
            // cst.setInt(1, Integer.parseInt(id)); // for integer
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record is deleted");
            showData(gs);
            showSell();
            
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    private void updateSell() {
      CallableStatement cst = null;
      String sql = null;
        try {
            sql  = "{call updateSellRecord(?, ?, ?)}";
            cst = conn.prepareCall(sql);
            cst.setString(1, jTextField1.getText());
            cst.setString(2, jTextField2.getText());
            cst.setString(3, jTextField3.getText());
            
            cst.execute();

            JOptionPane.showMessageDialog(null, "Record is updated");
            showData(gs);
            showSell();
        }
        catch(Exception e) {
            //System.err.printf(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        finally {
            try {
                cst.close();
            } catch(Exception e) {
                System.err.printf(e.getMessage());
            }
        }     
    }
    
    private TableModel rstoTable(ResultSet rs) {
        try {
            ResultSetMetaData md = rs.getMetaData(); // data of data
            int noc = md.getColumnCount();
            
            Vector cNames = new Vector();
            //get column name
            
            for(int i = 0; i < noc; i++) {
                cNames.addElement(md.getColumnLabel(i+1)); // just one line
            }
            // get rows of data from rs
            
            Vector rows = new Vector();
            while(rs.next()) {
                Vector newrow = new Vector();
                for(int i=0; i<noc; i++) {
                    newrow.addElement(rs.getObject(i+1));
                }
                rows.addElement(newrow);
            }
            return new DefaultTableModel(rows, cNames);
        }
        catch(Exception e) {
            System.err.printf(e.getMessage());
            return null;
        }
    }
    
    private String mySelectedNode() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
        String sn = node.getUserObject().toString();
        return sn;
    }
    
    private void addNodestoTree() {
        DefaultTreeModel treeModel = (DefaultTreeModel) jTree1.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Files");
        DefaultMutableTreeNode tables = new DefaultMutableTreeNode("Tables");
        DefaultMutableTreeNode utilities = new DefaultMutableTreeNode("Utilities");
        DefaultMutableTreeNode reports = new DefaultMutableTreeNode("Reports");
        
        treeModel.setRoot(root);
        root.add(tables);
        root.add(utilities);
        root.add(reports);
        
        DefaultMutableTreeNode t1 = new DefaultMutableTreeNode("Certificate");
        DefaultMutableTreeNode t2 = new DefaultMutableTreeNode("Certification");
        DefaultMutableTreeNode t3 = new DefaultMutableTreeNode("Company");
        DefaultMutableTreeNode t4 = new DefaultMutableTreeNode("Log");
        DefaultMutableTreeNode t5 = new DefaultMutableTreeNode("Medicine");
        DefaultMutableTreeNode t6 = new DefaultMutableTreeNode("Pharmacy");
        DefaultMutableTreeNode t7 = new DefaultMutableTreeNode("Sell");
        
        
        tables.add(t1);
        tables.add(t2);
        tables.add(t3);
        tables.add(t4);
        tables.add(t5);
        tables.add(t6);
        tables.add(t7);
        
        
        DefaultMutableTreeNode u1 = new DefaultMutableTreeNode("Utility1");
        DefaultMutableTreeNode u2 = new DefaultMutableTreeNode("Utility2");
        DefaultMutableTreeNode u3 = new DefaultMutableTreeNode("Utility3");
        
        utilities.add(u1);
        utilities.add(u2);
        utilities.add(u3);
        
        DefaultMutableTreeNode r1 = new DefaultMutableTreeNode("Report1");
        DefaultMutableTreeNode r2 = new DefaultMutableTreeNode("Report2");
        DefaultMutableTreeNode r3 = new DefaultMutableTreeNode("Report3");
        
        reports.add(r1);
        reports.add(r2);
        reports.add(r3);
        
        treeModel.reload();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnSearch = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        stext = new javax.swing.JTextField();
        slabel = new javax.swing.JLabel();
        slabel2 = new javax.swing.JLabel();
        stext2 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jTree1);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable1);

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnNew.setText("New");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnPrint.setText("Print");

        slabel.setText("jLabel12");

        slabel2.setText("jLabel12");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1005, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnSearch)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnNew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAdd))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(slabel)
                                .addGap(12, 12, 12)
                                .addComponent(stext, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnUpdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPrint))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(slabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stext2, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch)
                    .addComponent(btnNew)
                    .addComponent(btnAdd)
                    .addComponent(btnDelete)
                    .addComponent(btnUpdate)
                    .addComponent(btnPrint))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(slabel)
                    .addComponent(slabel2)
                    .addComponent(stext2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("jLabel1");

        jLabel2.setText("jLabel2");

        jLabel3.setText("jLabel3");

        jLabel4.setText("jLabel4");

        jLabel5.setText("jLabel5");

        jLabel6.setText("jLabel6");

        jLabel7.setText("jLabel7");

        jLabel8.setText("jLabel8");

        jLabel9.setText("jLabel9");

        jLabel10.setText("jLabel10");

        jTextField3.setText("jTextField3");

        jTextField4.setText("jTextField4");

        jTextField5.setText("jTextField5");

        jTextField6.setText("jTextField6");

        jTextField7.setText("jTextField7");

        jTextField8.setText("jTextField8");

        jTextField10.setText("jTextField10");

        jTextField11.setText("jTextField11");

        jTextField1.setText("jTextField1");

        jTextField2.setText("jTextField2");

        jLabel11.setText("jLabel11");

        jTextField12.setText("jTextField12");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        gs = mySelectedNode();
        showData(gs);
        if(gs == "Certificate")
            showCertificate();
        else if(gs == "Certification")
            showCertification();
        else if(gs == "Company")
            showCompany();
        else if(gs == "Log")
            showLog();
        else if(gs == "Medicine")
            showMedicine();
        else if(gs == "Pharmacy")
            showPharmacy();
        else if(gs == "Sell")
            showSell();
    }//GEN-LAST:event_jTree1ValueChanged

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if(gs == "Certificate")
            showCertificate();
        else if(gs == "Certification")
            showCertification();
        else if(gs == "Company")
            showCompany();
        else if(gs == "Log")
            showLog();
        else if(gs == "Medicine")
            showMedicine();
        else if(gs == "Pharmacy")
            showPharmacy();
        else if(gs == "Sell")
            showSell();
    }//GEN-LAST:event_jTable1MouseClicked

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        if(gs == "Certificate")
            clearCertificate(); 
        else if(gs == "Certification")
            clearCertification();
        else if(gs == "Company")
            clearCompany();
        else if(gs == "Medicine")
            clearMedicine();
        else if(gs == "Pharmacy")
            clearPharmacy();
        else if(gs == "Sell")
            clearSell();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        if(gs == "Certificate")
            addCertificate(); 
        else if(gs == "Certification")
            addCertification();
        else if(gs == "Company")
            addCompany();
        else if(gs == "Medicine")
            addMedicine();
        else if(gs == "Pharmacy")
            addPharmacy();
        else if(gs == "Sell")
            addSell();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        if(gs == "Certificate")
            searchCertificate(); 
        else if(gs == "Certification")
            searchCertification();
        else if(gs == "Company")
            searchCompany();
        else if(gs == "Log")
            searchLog();
        else if(gs == "Medicine")
            searchMedicine();
        else if(gs == "Pharmacy")
            searchPharmacy();
        else if(gs == "Sell")
            searchSell();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if(gs == "Certificate")
            deleteCertificate(); 
        else if(gs == "Certification")
            deleteCertification();
        else if(gs == "Company")
            deleteCompany();
        else if(gs == "Medicine")
            deleteMedicine();
        else if(gs == "Pharmacy")
            deletePharmacy();
        else if(gs == "Sell")
            deleteSell();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if(gs == "Certificate")
            updateCertificate(); 
        else if(gs == "Certification")
            updateCertification();
        else if(gs == "Company")
            updateCompany();
        else if(gs == "Medicine")
            updateMedicine();
        else if(gs == "Pharmacy")
            updatePharmacy();
        else if(gs == "Sell")
            updateSell();
    }//GEN-LAST:event_btnUpdateActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel slabel;
    private javax.swing.JLabel slabel2;
    private javax.swing.JTextField stext;
    private javax.swing.JTextField stext2;
    // End of variables declaration//GEN-END:variables
}
