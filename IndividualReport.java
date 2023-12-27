/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wcr.reports;

import java.awt.Color;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;
import net.sf.dynamicreports.examples.Templates;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.base.expression.AbstractValueFormatter;
import net.sf.dynamicreports.report.builder.DynamicReports;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.sbt;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;
import net.sf.dynamicreports.report.builder.MarginBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;

/**
 *
 * @author 19950014
 */
public class IndividualReport {
        Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    public String idn;//sdat , edat;
    public int syear, eyear, smon, emon;
     public JasperReportBuilder SummaryReportBuilder() 
            throws DRException{
           Date day = new Date();
           String now = day.toString();
           JasperReportBuilder summarybf = report();      
           //initialize columns 
           MarginBuilder margin = DynamicReports.margin().setLeft(20)
                   .setTop(7);
           StyleBuilder headerStyle = DynamicReports.stl.style()
                   .setBackgroundColor(Color.WHITE)
                   .setBold(Boolean.TRUE)
                   .setFontSize(11)
                   .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                   .setVerticalTextAlignment(VerticalTextAlignment.TOP);
           StyleBuilder fontStyle = DynamicReports.stl.style()                   
                   .setHorizontalAlignment(HorizontalAlignment.LEFT)
                   .setVerticalTextAlignment(VerticalTextAlignment.TOP)
                   .setFontSize(11)
                   .setPadding(1)
                   .setForegroundColor(Color.BLACK);
           StyleBuilder tableFont = DynamicReports.stl.style()
                   .setFontSize(11).setPadding(1);
           StyleBuilder remarkFont = DynamicReports.stl.style()
                   .setFontSize(11).setPadding(1);
           StyleBuilder dateandbf = DynamicReports.stl.style()
                   .setFontSize(11)
                   .setVerticalTextAlignment(VerticalTextAlignment.TOP)
                   .setRightPadding(6);
           
           summarybf.setTitleStyle(fontStyle);
          TextColumnBuilder<String> idColumn = col.column("ID No.", 
                   "ID number", type.stringType()).setFixedWidth(60)
                   .setHorizontalAlignment(HorizontalAlignment.CENTER)
                   .setStyle(tableFont);
           TextColumnBuilder<String> workerColumn = col.column("Name", "Name",
                   type.stringType()).setFixedWidth(150).setStyle(tableFont)
                   .setHorizontalAlignment(HorizontalAlignment.LEFT);
           TextColumnBuilder<String> desigColumn = col.column("Designation", "Designation",
                   type.stringType()).setFixedWidth(65).setStyle(remarkFont);
           TextColumnBuilder<String> dateColumn = col.column("Date",
                   "Date", type.stringType()).setFixedWidth(60)
                   .setHorizontalAlignment(HorizontalAlignment.LEFT);
           TextColumnBuilder<String> catColumn = col.column("Category",
                   "Category", type.stringType()).setFixedWidth(60)
                   .setStyle(remarkFont);
           TextColumnBuilder<Double> bfColumn = col.column("BF", 
                   "BF", type.doubleType()).setValueFormatter
                   (new ValueFormater()).setFixedWidth(70).setStyle(dateandbf);
           TextColumnBuilder<String> proColumn = col.column("Project", "Project", 
                   type.stringType()).setFixedWidth(75).setStyle(remarkFont);
           TextColumnBuilder<String> remColumn = col.column("Remarks", "Remarks", 
                   type.stringType()).setFixedWidth(75).setStyle(remarkFont);
           
           summarybf
                   .setTemplate(Templates.reportTemplate)
                   .columns( idColumn, workerColumn, desigColumn, //noColumn,
                           dateColumn, catColumn, bfColumn, proColumn, remColumn)
                   .setColumnTitleStyle(headerStyle)
                   .setHighlightDetailEvenRows(false)
                   .setHighlightDetailOddRows(false)
                   .groupBy(dateColumn)
                   .title(Components.horizontalFlowList(Components
                   .text          ("                                                     "
                        + "                     COMPANY XXX \n"
                        + "                                            "
                        + " ENGINEERING AND CONSTRUCTION "
                        + "DEPARTMENT")))
                   .title(Components.horizontalFlowList(Components
                   .text("\n\nWORKERS BENEFIT FUND SUMMARY")).setDimension(8, 24))
                   .subtotalsAtSummary(sbt.sum(bfColumn).setValueFormatter(
                    new ValueFormater())).setTextStyle(tableFont)
                   .setPageFormat(PageType.LETTER, PageOrientation.PORTRAIT)
                   .setPageMargin(margin)
                   .pageFooter(Templates.footerComponent)
                   .setDataSource(createDataSource());
                   
           return summarybf;
    }
    private JRDataSource createDataSource(){
        
        DRDataSource data_source = new DRDataSource("Date","ID number","Name"
                ,"Designation","Category","BF","Project","Remarks");
        
        con=ConnectorDB();
        String sql = "SELECT DISTINCT(c.recordyear||'-'||c.recordmonth||'-'||c.recordday) AS petsa,"
                + "w.id_number, (w.lastname||', '||w.firstname||"
                + "'  '||w.middlename) AS name, c.designation, c.category, "
                + "c.tulong, (c.locale||', '||c.district) project, c.remarks, (c.recordyear||'-'||c.recordmonth) AS yrmon "
                + "FROM tb_worker w, tb_category c WHERE c.id_number = w.id_number AND w.id_number = ? ORDER BY petsa ASC,  w.lastname ASC, w.firstname ASC,  " 
                + "w.middlename ASC";
        try{
            ps = con.prepareStatement(sql);
            ps.setString(1, idn);
            rs = ps.executeQuery();
            
            int dataNo = 1;
            while(rs.next()){                
                data_source.add(rs.getString(1),rs.getString(2),//dataNo,
                rs.getString(3),rs.getString(4),rs.getString(5), rs.getDouble(6), 
                rs.getString(7),rs.getString(8));             
            dataNo++; 
            
            }
            
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, "Error in "
                    + "SummaryReport::CreateDataSource ==> "+e);
        }finally{
            try{
                ps.close();
                rs.close();
            }catch(Exception e){
               JOptionPane.showMessageDialog(null, "Error in "
                       + "SummaryReport::CreateDataSource "+e); 
            }
        }
        return  data_source;
    }
    public final Connection ConnectorDB() {
		try{
			Class.forName("org.sqlite.JDBC");
                        con = DriverManager.getConnection("jdbc:sqlite:../WCR/db_workercategory.sqlite");//"jdbc:sqlite:C:\\ECDCS\\WCR\\db_workercategory.sqlite"	
                }
                catch(ClassNotFoundException | SQLException e)
                {
		    JOptionPane.showMessageDialog(null, 
                                "Error in getConnection: "+e);
		}	
		return con;
    }
    private static class ValueFormater extends AbstractValueFormatter<String, 
           Number>{
           private static final long serialVersionUID = 1L;
           
           @Override
           public String format(Number value, ReportParameters reportParameters)
           {
               return type.bigDecimalType().valueToString(value, 
                       reportParameters.getLocale());
           }

       } 
}
