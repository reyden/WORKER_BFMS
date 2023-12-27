/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wcr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * @author 19950014 :: Reyden Yanoc
 */
public class DataAccessForm {
  Connection con;
  public String l, f, m;
  public boolean isexisting = false;
  
  
  public TableModel searchKeyWord(String keyword, TableModel model){
        String query = "SELECT DISTINCT w.id_number,(w.lastname||', '||w.firstname||' '||w.middlename) AS Name, "
              + "c.designation,c.category,(c.recordmonth||'/'||c.recordday||'/'||c.recordyear) AS date, "
              + "c.tulong,(c.locale||', '||c.district) AS Project, c.remarks, c.catid FROM tb_worker w, tb_category c "
              + "WHERE w.id_number = c.id_number AND (w.id_number||Name||" //(w.lastname||w.firstname||w.middlename||
              + "c.designation||c.category||date ||c.tulong||Project||c.remarks) LIKE ?  ORDER BY lastname DESC, firstname DESC, "
              + "middlename DESC, recordmonth DESC, recordday DESC, recordyear DESC";//GROUP BY w.id_number 
      PreparedStatement ps=null;
      ResultSet rs=null;
      JTable table = new JTable();
      table.setModel(model);
      DefaultTableModel tm = (DefaultTableModel)table.getModel();
      tm.setRowCount(0);
      table.setModel(tm);
      
      try{
            ConnectorDB();
            ps = con.prepareStatement(query); 
            ps.setString(1,"%"+keyword+"%");
            rs = ps.executeQuery();
            while(rs.next())
            {
                Object[] rows = new Object[9];                
                for(int i = 0; i < 9; i++){
                    rows[i]=rs.getObject(i+1);
                }
                int y=0; 
                ((DefaultTableModel)table.getModel()).insertRow(y, rows);
                y++;           
            }           
        }catch(Exception e){ 
            JOptionPane.showMessageDialog(null, "fillDataTable :"+e);
        }finally{
            try{
                ps.close();
                rs.close();
            }catch(Exception e){}
        }
        return table.getModel();
  }
  public Object[] workerData(String id){
      String query = "SELECT w.tbwid, w.firstname, w.middlename, w.lastname, c.locale, "
              + "c.district, c.catid FROM tb_worker w, tb_category c WHERE "
              + "w.id_number = c.id_number AND w.id_number = ?";
      Object[] data = new Object[7];
      PreparedStatement ps = null;
      ResultSet rs = null;
      try{
          ConnectorDB();
          ps = con.prepareStatement(query);
          ps.setString(1, id);
          rs = ps.executeQuery();
          
          while(rs.next()){
              for(int i=0;i<7;i++){
                  data[i]=rs.getString(i+1);
              }
          }
          
      }catch(Exception e){
          JOptionPane.showMessageDialog(null, "Error in workerData :: "+e);
      }finally{
          try{
              ps.close();
              rs.close();
          }catch(Exception e){}
      }
      return data;
  }
  public void verifyId(String id){
      String query = "SELECT * FROM tb_worker WHERE id_number LIKE ? ";
      
      PreparedStatement ps=null;
      ResultSet rs = null;
     
      try{
          ConnectorDB();
          ps = con.prepareStatement(query);
          ps.setString(1, "%"+id+"%");
          rs = ps.executeQuery();
          
          if(rs.next()){              
             
              l=rs.getString(3);
              f=rs.getString(4);
              m=rs.getString(5);
              isexisting = true;
          }
      }catch(Exception e){
          JOptionPane.showMessageDialog(null, e);
      }finally{
          try{
              ps.close();
              rs.close();
          }catch(Exception e){
              
          }
      }
  }
  public void updateRecord(int wid, String id, String ln, String fname, 
          String mname,String des, String cat, int rd, int rm, int ry, String loc, 
          String dis, double tul, String rem, int catid){
      //JOptionPane.showMessageDialog(null, id+ "   "+des+ "   "+cat+ "    "+rd+"/"+rm+"/"+ry+"  "+loc+" "+dis+"  "+tul+"  "+rem+"  "+catid);
      //UPDATE WORKER TABLE...
      String query = "UPDATE tb_worker SET id_number = ?, lastname = ?, "
              + "firstname = ?, middlename = ? WHERE tbwid = ?";
      PreparedStatement ps = null;
      ResultSet rs = null;
      try{
          ConnectorDB();
          ps=con.prepareStatement(query);
          ps.setString(1, id);
          ps.setString(2, ln);
          ps.setString(3, fname);
          ps.setString(4, mname);
          ps.setInt(5, wid);
          ps.executeUpdate();
          //JOptionPane.showMessageDialog(null, id+" "+ln+" "+fname+" "+wid);
      }catch(Exception e){
          JOptionPane.showMessageDialog(null,"Error updating Record :: "+ e);
      }finally{
          try{
              ps.close();
              //rs.close();
          }catch(Exception e){}
      }
      //UPDATE CATEGORY TABLE...
      String query2 = "UPDATE tb_category SET id_number = ?, designation = ?, "
              + "category = ?, recordday = ?, recordmonth = ?, recordyear = ?, "
              + "locale = ?, district = ?, tulong = ?, remarks = ? WHERE catid = ? ";
      
      try{
          ConnectorDB();
          ps = con.prepareStatement(query2);
          ps.setString(1, id);
          ps.setString(2, des);
          ps.setString(3, cat);
          ps.setInt(4, rd);
          ps.setInt(5, rm);
          ps.setInt(6, ry);
          ps.setString(7, loc);
          ps.setString(8, dis);
          ps.setDouble(9, tul);
          ps.setString(10, rem);
          ps.setInt(11, catid);
          ps.executeUpdate();
          //JOptionPane.showMessageDialog(null, id+" "+des+" "+cat+" "+rd+" "+rm+" "+ry+" "+loc+" "+dis+" "+tul+" "+rem);
      }catch(Exception e){
          JOptionPane.showMessageDialog(null, "Error updating tb_category :: "+e);
      }finally{
          try{
              ps.close();
              //rs.close();
          }catch(Exception e){}
      } 
      JOptionPane.showMessageDialog(null, "Information updated.");
  }
  public void deleteData(int ctid, String id){
      String query = "SELECT COUNT(id_number) FROM tb_category WHERE id_number = ?";
      String query2 = "DELETE FROM tb_category WHERE catid = ?";
      String query3 = "DELETE FROM tb_worker WHERE id_number = ?";
      String query3a = "DELETE FROM tb_category WHERE id_number = ?";
      
      //JOptionPane.showMessageDialog(null, ctid +"  "+id);
      PreparedStatement ps = null;
      ResultSet rs = null;
      try{
          ConnectorDB();
          ps = con.prepareStatement(query);
          ps.setString(1, id);
          rs = ps.executeQuery();
          if(rs.next()){   
              //JOptionPane.showMessageDialog(null, rs.getInt(1));
              int cnt = rs.getInt(1);
              ps.close();
              rs.close();
              if(cnt > 1){
                  try{
                      ConnectorDB();
                      ps = con.prepareStatement(query2);
                      ps.setInt(1, ctid);
                      ps.executeUpdate();
                  }catch(Exception e){
                      JOptionPane.showMessageDialog(null,"Error deleting part information :: "+ e);
                  }
              }else if(cnt == 1){
                  try{
                      //JOptionPane.showMessageDialog(null, id);
                      ConnectorDB();                      
                      ps = con.prepareStatement(query3);
                      ps.setString(1, id);
                      ps.executeUpdate();
                      
                      ps.close();
                      
                      ConnectorDB();
                      ps = con.prepareStatement(query3a);
                      ps.setString(1, id);
                      ps.executeUpdate();                    
                      
                  }catch(Exception e){
                      JOptionPane.showMessageDialog(null,"Error deleting one information :: "+ e);
                  }  
              }
          }
      }catch(Exception e){
          JOptionPane.showMessageDialog(null, "Error in counting data :: "+e);
      }finally{
          try{
              ps.close();
              rs.close();
          }catch(Exception e){}
      }
      JOptionPane.showMessageDialog(null,"Information completely deleted.");
  }
  public void insertNewRecord(String id, String ln, String fname, String mname, 
          String des, String cat, int rd, int rm, int ry, String loc, String dis, 
          double tul, String rem ){
    
      //SAVE THE WORKER INFO FIRST...
      String query = "INSERT INTO tb_worker(id_number, lastname, firstname, "
              + "middlename) VALUES (?,?,?,?)";
      
      PreparedStatement ps=null;
      ResultSet rs=null;
      try{
          ConnectorDB();
          ps = con.prepareStatement(query);
          ps.setString(1, id);
          ps.setString(2,ln);
          ps.setString(3,fname);
          ps.setString(4,mname);
                    
          ps.executeUpdate();
          
      }catch(Exception e){
          JOptionPane.showMessageDialog(null,"Error saving worker profile => "+ e);
      }finally{
          try{
              ps.close();
              rs.close();
          }catch(Exception e){}
      }
      //NOW SAVE THE CATEGORY DATA...
      String query2 = "INSERT INTO tb_category(id_number, designation, category, "
              + "recordday, recordmonth, recordyear, locale, district, tulong, remarks) "
              + "VALUES(?,?,?,?,?,?,?,?,?,?)";
      
      try{
          ConnectorDB();
          ps = con.prepareStatement(query2);
          ps.setString(1, id);
          ps.setString(2, des);
          ps.setString(3, cat);
          ps.setInt(4, rd);
          ps.setInt(5, rm);
          ps.setInt(6, ry);
          ps.setString(7, loc);
          ps.setString(8, dis);
          ps.setDouble(9, tul);
          ps.setString(10, rem);
          
          ps.executeUpdate();
          
      }catch(Exception e){
          JOptionPane.showMessageDialog(null, "Error saving worker category => "+ e);
      }finally{
          try{
              ps.close();
              rs.close();
          }catch(Exception e){              
          }
      }
      JOptionPane.showMessageDialog(null, "Record successfully saved.");
  }
  public TableModel fillDataTable(TableModel model){
      
      String query = "SELECT DISTINCT w.id_number,(w.lastname||', '||w.firstname||' '||w.middlename) AS Name, "
              + "c.designation,c.category,(c.recordmonth||'/'||c.recordday||'/'||c.recordyear) AS date, "
              + "c.tulong,(c.locale||', '||c.district) AS Project, c.remarks, c.catid FROM tb_worker w, tb_category c "
              + "WHERE w.id_number = c.id_number ORDER BY lastname DESC, firstname DESC, "
              + "middlename DESC, recordmonth DESC, recordday DESC, recordyear DESC"; //GROUP BY w.id_number
      
      /*ORDER BY recordmonth DESC ORDER BY recordday "
      + "ORDER BY recordyear DESC ORDER BY familyname DESC ORDER BY lastname DESC ORDER BY middlename DESC";*/
      
      PreparedStatement ps=null;
      ResultSet rs=null;
      JTable table = new JTable();
      table.setModel(model);
      DefaultTableModel tm = (DefaultTableModel)table.getModel();
      tm.setRowCount(0);
      table.setModel(tm);
      
      try{
            ConnectorDB();
            ps = con.prepareStatement(query); 
            rs = ps.executeQuery();
            while(rs.next())
            {
                Object[] rows = new Object[9];                
                for(int i = 0; i < 9; i++){
                    rows[i]=rs.getObject(i+1);
                }
                int y=0; 
                ((DefaultTableModel)table.getModel()).insertRow(y, rows);
                y++;           
            }           
        }catch(Exception e){ 
            JOptionPane.showMessageDialog(null, "fillDataTable :"+e);
        }finally{
            try{
                ps.close();
                rs.close();
            }catch(Exception e){                
            }
        }
        return table.getModel();
  }
  
  public Connection ConnectorDB() {        
		try {
			
                        Class.forName("org.sqlite.JDBC");
                        con = DriverManager.getConnection("jdbc:sqlite:./db_workercategory.sqlite");
		        //"jdbc:sqlite:C:\\ECDCS\\WCR\\db_workercategory.sqlite"
                }catch(Exception e){
				JOptionPane.showMessageDialog(null, "Error in getConnection: "+e);
		}			
		return con;
}    
}
