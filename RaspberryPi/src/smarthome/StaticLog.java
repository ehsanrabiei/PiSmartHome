
package smarthome;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public  class StaticLog {
    
    public static StaticLog instance ;

    public StaticLog() {
        if(instance==null)
            this.instance = new StaticLog();
            
    }
    
    
    public static String logtxt = "" ;
    
    public static void SaveInFile(String log){
        if(log == null) return;
        System.out.println("log-> " + log);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();  
        logtxt = dtf.format(now) + " ; " + log + "\n";  
        try{
            FileWriter fileWriter = new FileWriter("log.txt" , true);
            fileWriter.write("" + logtxt);
           // fileWriter.append(logtxt);
           fileWriter.flush();
           fileWriter.close();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
         
    }
}
