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
public class WCR {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //MainForm mf = new MainForm();
        //mf.setVisible(true);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }
    
}
