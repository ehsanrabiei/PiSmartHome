
package smarthome;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class Client_Test {
   public static void main(String args[])throws Exception{  
        Socket s=new Socket("192.168.1.82",3333);  // ip raspberry pi
        DataInputStream din=new DataInputStream(s.getInputStream());  
        DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  

        String str="",str2=""; 
        dout.writeUTF("hello");  
        dout.flush();  
        while(!str.equals("stop")){  
            str2=din.readUTF();
            System.out.println("Server says: "+str2);  
            if(!str2.contains("XVS")){     
                System.out.println("hand shaking problem from serever to client");  
                //return;
            }
            //send orders
            str = br.readLine();
            dout.writeUTF(str);
            dout.flush();
        }  

        dout.close();  
        s.close();  
        }
}
