/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wcr;

/**
 *
 * @author 19950014
 */
import java.text.DecimalFormat;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author win7
 */
        final class RenderPrice extends DefaultTableCellRenderer {
  
        RenderPrice() { 
                setHorizontalAlignment(SwingConstants.RIGHT);
               
                setForeground(new java.awt.Color(255,0,0));
                
        }
  
        @Override 
        public void setValue(Object aValue) {
                    Object result = aValue;
                    if ((aValue != null) && (aValue instanceof Number)) {
                            DecimalFormat df = new DecimalFormat("#,##0.00  ");
                            result = df.format(aValue);
                    } 
        super.setValue(result);
        
        }
        
    }
