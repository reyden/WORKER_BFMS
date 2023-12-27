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
import static net.sf.dynamicreports.report.builder.DynamicReports.type;
import net.sf.dynamicreports.report.builder.MarginBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;

/**
 *
 * @author 19950014 :: Reyden Yanoc
 */
public class ResignedWorkersRpt {
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    public int syear , eyear, smon, emon;
    
     public JasperReportBuilder ResignedRptBuilder() 
            throws DRException{
           Date day = new Date();
           String now = day.toString();
           JasperReportBuilder resigned = report();      
           MarginBuilder margin = DynamicReports.margin().setLeft(20)
                   .setTop(7);
           StyleBuilder headerStyle = DynamicReports.stl.style()
                   .setBackgroundColor(Color.WHITE)
                   .setBold(Boolean.TRUE)
                   .setFontSize(12)
                   .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
           
           StyleBuilder centerStyle = DynamicReports.stl.style()
                   .setFontSize(12)
                   .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
                   
                 
           StyleBuilder fontStyle = DynamicReports.stl.style() 
                   .setFontSize(12) 
                   .setPadding(1)
                   .setForegroundColor(Color.BLACK);
             
          resigned.setTitleStyle(fontStyle);
          TextColumnBuilder<Integer> noColumn = col.column("No", "No", type
                   .integerType()).setFixedWidth(40).setStyle(fontStyle)
                   .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
           TextColumnBuilder<String> idColumn = col.column("ID number", 
                   "ID number", type.stringType()).setFixedWidth(65)
                   .setStyle(fontStyle)
                   .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
           TextColumnBuilder<String> workerColumn = col.column("Name", "Name",
                   type.stringType()).setFixedWidth(160).setStyle(fontStyle)
                   .setHorizontalAlignment(HorizontalAlignment.LEFT);
           TextColumnBuilder<String> desigColumn = col.column("Designation", "Designation",
                   type.stringType()).setFixedWidth(140).setStyle(fontStyle);
           TextColumnBuilder<String> catColumn = col.column("Category",
                   "Category", type.stringType()).setFixedWidth(80).setStyle(fontStyle)         
                   .setHorizontalAlignment(HorizontalAlignment.CENTER);
           TextColumnBuilder<String> dateColumn = col.column("Date",
                   "Date", type.stringType()).setFixedWidth(70).setStyle(fontStyle)
                   .setHorizontalAlignment(HorizontalAlignment.LEFT);

           resigned
                   .setTemplate(Templates.reportTemplate)
                   .columns( noColumn, idColumn, workerColumn, desigColumn,
                           catColumn, dateColumn)
                   
                   .setColumnTitleStyle(headerStyle)
                   .setHighlightDetailEvenRows(false)
                   .setHighlightDetailOddRows(false)
                   .title(Components.horizontalFlowList(Components
                   .text("                                                     "
                        + "                       COMPANY XXX \n"
                        + "                                            "
                        + "   ENGINEERING AND CONSTRUCTION "
                        + "DEPARTMENT")))
                   .title(Components.horizontalFlowList(Components
                   .text("\n\n  RESIGNED WORKERS")).setDimension(8, 24))
                   
                   .setPageFormat(PageType.LETTER, PageOrientation.PORTRAIT)
                   
                   .setPageMargin(margin)
                   .pageFooter(Templates.footerComponent)
                   .setDataSource(createDataSource());
                   
           return resigned;
    }
    private JRDataSource createDataSource(){
        
        DRDataSource data_source = new DRDataSource("No", "ID number", "Name", "Designation", "Category", "Date");
        
        con=ConnectorDB();
        String sql = "SELECT DISTINCT w.id_number, (w.lastname||', '||w.firstname||'  '||w.middlename) AS name, c.designation, "
                + "c.category,(c.recordyear||'-'||c.recordmonth||'-'||c.recordday) AS petsa "
                + "FROM tb_worker w, tb_category c WHERE w.id_number = c.id_number AND c.category = 'Resigned' "
                + "AND c.recordyear >= ? AND c.recordyear <= ? AND c.recordmonth >= ? AND c.recordmonth <= ?"
                + "ORDER BY petsa ASC, w.lastname ASC, w.firstname ASC, "
                + "w.middlename ASC";
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, syear);
            ps.setInt(2, eyear);
            ps.setInt(3, smon);
            ps.setInt(4, emon);
            rs = ps.executeQuery();
            
            int dataNo = 1;
            while(rs.next()){                
                data_source.add(dataNo,rs.getString(1),rs.getString(2),
                rs.getString(3),rs.getString(4),rs.getString(5));           
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
                        con = DriverManager.getConnection("jdbc:sqlite:../WCR/db_workercategory.sqlite");	
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
