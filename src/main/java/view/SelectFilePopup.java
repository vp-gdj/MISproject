/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author Charlotte
 */
class SelectFilePopup {
    public static File getFile(String defaultPath){
        JFileChooser jfc = new JFileChooser(defaultPath);
        
        int returnValue = jfc.showOpenDialog(null);
        
        if( returnValue == JFileChooser.APPROVE_OPTION ){
            File selectedFile = jfc.getSelectedFile();
            System.out.println(selectedFile.getAbsolutePath());
            
            return selectedFile;
        }
        
        return null;
    }
    public static File getFile(){
        String defaultPath = "C:\\Users\\Charlotte\\Documents\\MA1\\2e quadri\\medical information systems\\labos\\DICOMDIR";
        
        return getFile(defaultPath);
    }
    
}
