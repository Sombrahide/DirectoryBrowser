/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juanjomp.directorybrowser;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
/**
 *
 * @author Juanjo
 */
public class WriteLog {
    private String _path;
    private boolean _appendToFile = false;
    
    public WriteLog(String filePath){
        _path = filePath;
    }
    public WriteLog(String file_path, boolean appendValue){
        _path = file_path;
        _appendToFile = appendValue;
    }
    public void writeToLog(String textLine) throws IOException {
        FileWriter write = new FileWriter(_path, _appendToFile);
        PrintWriter printLine = new PrintWriter(write);
        
        printLine.printf("%s"+"%n", textLine);
        printLine.close();
    }
}
