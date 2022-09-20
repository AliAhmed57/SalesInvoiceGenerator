/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.InvoiceFrame1;
import model.InvoiceLine;
import model.invHeaderTableModel;
import model.invLineTableModel;
import view.InvoiceFrame;
import view.InvoiceHeaderDialog;
import view.InvoiceLineDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author eng_Nourhane
 */

    public class newListener implements ActionListener, ListSelectionListener {

 private InvoiceFrame invoice;
    private DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    
    public newListener(InvoiceFrame invoice) {
        this.invoice = invoice;
    }
 @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
        case "CreateNewInvoice":

            Inv_dialouge();
            break;
        case "DeleteInvoice":
             Delete();
            break;
            case "LoadFile":
          Load();
          break;
          case "SaveFile":
         Save();
        case "CreateNewLine":
            Line_dialouge();
         break;

        case "DeleteLine":
            Line_delete();
            break;

         case "createInvCancel":
             createInv_cancel();
             break;
             case "createInvOK":
                 createInv_OK();
             break;
             case "createLineCancel":
                 createLine_cancel();
                 break;
             case "createLineOK":
                 createLine_oK();
                 break;

}
    }
   private void Load() {
        JOptionPane.showMessageDialog(invoice, "Select header file ", "Attention", JOptionPane.WARNING_MESSAGE);
        JFileChooser openFile = new JFileChooser();
        int result = openFile.showOpenDialog(invoice);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File headerFile = openFile.getSelectedFile();
            try{
            FileReader headerFr = new FileReader(headerFile);
            BufferedReader headerBr = new BufferedReader (headerFr);
            String headerLine = null;
            
            while (( headerLine = headerBr.readLine()) != null)
            {
                String[] headerParts = headerLine.split(",");
                String invNumStr = headerParts[0];
                String invDateStr = headerParts[1];
                String custName = headerParts[2];

                int invNum = Integer.parseInt(invNumStr);
                Date invDate = df.parse(invDateStr);

                InvoiceFrame1 inv = new InvoiceFrame1(invNum, custName, invDate);
                invoice.getInvoicesArray().add(inv);
            
            }
            
            JOptionPane.showMessageDialog(invoice, "Select lines file ", "Attention", JOptionPane.WARNING_MESSAGE);
                result = openFile.showOpenDialog(invoice);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File linesFile = openFile.getSelectedFile();
                    BufferedReader linesBr= new BufferedReader(new FileReader(linesFile));
                    String linesLine = null;
                    while ((linesLine = linesBr.readLine()) !=null)
                    {
                        String[] lineParts = linesLine.split(",");
                        String invNumStr = lineParts[0];
                        String itemName = lineParts[1];
                        String itemPriceStr = lineParts[2];
                        String itemCountStr = lineParts[3];
                        int invNum = Integer.parseInt(invNumStr);
                        double itemPrice = Double.parseDouble(itemPriceStr);
                        int itemCount = Integer.parseInt(itemCountStr);
                        InvoiceFrame1 header = findInv(invNum);
                        InvoiceLine invLine = new InvoiceLine(itemName, itemPrice, itemCount, header);
                        header.getLines().add(invLine);
                    }
                    invoice.setInvHeaderTableModel(new invHeaderTableModel(invoice.getInvoicesArray()));
                    invoice.getInvoicesTable().setModel(invoice.getInvHeaderTableModel());
                    invoice.getInvoicesTable().validate();
                }
                System.out.println("Check");
            } catch (ParseException ex) {
                ex.printStackTrace();
               JOptionPane.showMessageDialog(invoice, "Date Format Error\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(invoice, "Number Format Error\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(invoice, "File Error\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(invoice, "Read Error\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        displayInvoices();
    }  


   
         
        
      private InvoiceFrame1 findInv(int invNum){
    InvoiceFrame1 header = null;
    for(InvoiceFrame1 inv : invoice.getInvoicesArray()) {
        if (invNum == inv.getInvNum()){
            header = inv;
            break;
        }
    }
    return header ;
}                 
    private void Save() 
    {
        String headers = "";
        String lines = "";
        for (InvoiceFrame1 header : invoice.getInvoicesArray()) {
            headers += header.getDataAsCSV();
            headers += "\n";
            for (InvoiceLine line : header.getLines()) {
                lines += line.getDataAsCSV();
                lines += "\n";
            }
        }
        JOptionPane.showMessageDialog(invoice, "Select file to save header data ", "Attention", JOptionPane.WARNING_MESSAGE);
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(invoice);
        if (result == JFileChooser.APPROVE_OPTION) {
            File headerFile = fileChooser.getSelectedFile();
            try {
                FileWriter hFW = new FileWriter(headerFile);
                hFW.write(headers);
                hFW.flush();
                hFW.close();

                JOptionPane.showMessageDialog(invoice, "Select file to save lines data ", "Attention", JOptionPane.WARNING_MESSAGE);
                result = fileChooser.showSaveDialog(invoice);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File linesFile = fileChooser.getSelectedFile();
                    FileWriter fw = new FileWriter(linesFile);
                    fw.write(lines);
                    fw.flush();
                    fw.close();
                }
                                           JOptionPane.showMessageDialog(null, "File Saved");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(invoice, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
   

    @Override
    public void valueChanged(ListSelectionEvent e) {
    System.out.println("Invoice Selected");
invoicesTableRowSelected();
    }

    private void invoicesTableRowSelected() {
int selectedRowIndex = invoice.getInvoicesTable().getSelectedRow();
if (selectedRowIndex >= 0){
    InvoiceFrame1 row = invoice.getInvHeaderTableModel().getInvoicesArray().get(selectedRowIndex);
    invoice.getCustNameTF().setText(row.getCustomerName());
    invoice.getInvDateTF().setText(df.format(row.getInvDate()));
    invoice.getInvNumLbl().setText("" + row.getInvNum());
    invoice.getInvTotalLbl().setText("" + row.getInvTotal());
    ArrayList<InvoiceLine> lines = row.getLines();
    invoice.setInvLineTableModel(new invLineTableModel(lines));
    invoice.getInvLineTable().setModel(invoice.getInvLineTableModel());
    invoice.getInvLineTableModel().fireTableDataChanged();
    
}
    }
 private void Delete() {
        int invIndex = invoice.getInvoicesTable().getSelectedRow();
        InvoiceFrame1 header = invoice.getInvHeaderTableModel().getInvoicesArray().get(invIndex);
        invoice.getInvHeaderTableModel().getInvoicesArray().remove(invIndex);
       invoice.getInvHeaderTableModel().fireTableDataChanged();
        invoice.setInvLineTableModel(new invLineTableModel(new ArrayList<InvoiceLine>()));
        invoice.getInvLineTable().setModel(invoice.getInvLineTableModel());
        invoice.getInvLineTableModel().fireTableDataChanged();
        invoice.getCustNameTF().setText("");
        invoice.getInvDateTF().setText("");
        invoice.getInvNumLbl().setText("");
        invoice.getInvTotalLbl().setText("");
        displayInvoices();
             JOptionPane.showMessageDialog(null, "Invoice Deleted ");
 

     }  

   
    private void Line_delete() {
       
           
        int lineIndex = invoice.getInvLineTable().getSelectedRow();
        InvoiceLine line = invoice.getInvLineTableModel().getInvoiceLines().get(lineIndex);
           invoice.getInvLineTableModel().getInvoiceLines().remove(lineIndex);
            invoice.getInvHeaderTableModel().fireTableDataChanged();
        invoice.getInvLineTableModel().fireTableDataChanged();
                invoice.getInvTotalLbl().setText("" + line.getHeader().getInvTotal());
         JOptionPane.showMessageDialog(null, "Line Deleted");
          displayInvoices();
        }      
      
    
     
     private void displayInvoices(){
         for (InvoiceFrame1 header :invoice.getInvoicesArray()) {
             System.out.println(header);
         }
     }
      
private void Inv_dialouge() {
invoice.setHeaderDialog(new InvoiceHeaderDialog(invoice));
invoice.getHeaderDialog().setVisible(true);
       
    }
  private void Line_dialouge() {
invoice.setLineDialog(new InvoiceLineDialog(invoice));
invoice.getLineDialog().setVisible(true);

    }
    
    private void createInv_cancel() {
invoice.getHeaderDialog().setVisible(false);
invoice.getHeaderDialog().dispose();
invoice.setHeaderDialog(null);
    }

    private void createInv_OK() {
String custName = invoice.getHeaderDialog().getCustNameField().getText();
String invDateStr = invoice.getHeaderDialog().getInvDateField().getText();
invoice.getHeaderDialog().setVisible(false);
invoice.getHeaderDialog().dispose();
invoice.setHeaderDialog(null);
try {
    Date invDate = df.parse(invDateStr);
    int invNum = getNextInvoiceNum();
    InvoiceFrame1 invoiceFrame1 = new InvoiceFrame1(invNum, custName, invDate);
    invoice.getInvoicesArray().add(invoiceFrame1);
    invoice.getInvHeaderTableModel().fireTableDataChanged();}
    catch (ParseException ex) {
    JOptionPane.showMessageDialog(invoice , "Wrong date Format" , "Error" , JOptionPane.ERROR_MESSAGE);
  ex.printStackTrace();
  displayInvoices();
}
    }
    
    private int getNextInvoiceNum() {
        int max = 0;
        for(InvoiceFrame1 header : invoice.getInvoicesArray()) {
            if (header.getInvNum()> max) {
                max = header.getInvNum();
                
            }
        }
        return max + 1;
    }

    private void createLine_cancel() {
invoice.getLineDialog().setVisible(false);
invoice.getLineDialog().dispose();
invoice.setLineDialog(null);
    }

    private void createLine_oK() {
String itemName = invoice.getLineDialog().getItemNameField().getText();
String itemCountStr = invoice.getLineDialog().getItemCountField().getText();
String itemPriceStr = invoice.getLineDialog().getItemPriceField().getText();
invoice.getLineDialog().setVisible(false);
invoice.getLineDialog().dispose();
invoice.setLineDialog(null);
int itemCount = Integer.parseInt(itemCountStr);
double itemPrice = Double.parseDouble(itemPriceStr);
 int headerIndex = invoice.getInvoicesTable().getSelectedRow();
        InvoiceFrame1 invoice = this.invoice.getInvHeaderTableModel().getInvoicesArray().get(headerIndex); 
        InvoiceLine invoiceLine = new InvoiceLine(itemName, itemPrice, itemCount, invoice);
    invoice.addInvLine(invoiceLine);
        this.invoice.getInvLineTableModel().fireTableDataChanged();
        this.invoice.getInvHeaderTableModel().fireTableDataChanged();
        this.invoice.getInvTotalLbl().setText("" + invoice.getInvTotal());
      displayInvoices();   
    }

    
    }
