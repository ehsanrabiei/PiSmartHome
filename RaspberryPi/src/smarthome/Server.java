package smarthome;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

//main class 
public class Server {
    
    MainMenu mainmenu = new MainMenu();
    
    public void MainStarter(){
        Socket s=null;
        ServerSocket ss2=null;
        System.out.println("Server Listening......");
        Startmenu();
        System.out.println("Interface Actived");
        try{
            ss2 = new ServerSocket(3333); 
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Server error");
        }
        while(true){
            try{
                s= ss2.accept();
                System.out.println("connection Established");
                ServerThread st=new ServerThread(s , this);
                st.start();
            }
            catch(IOException e){
                e.printStackTrace();
                System.out.println("Connection Error");
            }
        }
    }
    
    public void Startmenu(){
        mainmenu.show();
    }
    public void SendCommand(String cmd){
        if(cmd==null || cmd.length()>20) return;
        mainmenu.ServerToGpio(cmd);
    }
    public String GetStatusFromMenu(){
        return mainmenu.GetStatus();
        //return "XVS-1-1-1-XVS";
    }
}

class ServerThread extends Thread{  
    String line=null;
    Socket s=null;
    DataInputStream din;
    DataOutputStream dout;
    private Server instance;
    public ServerThread(Socket s , Server Instance){
        this.s=s;
        this.instance = Instance;
    }
    @Override
    public void run() {
    try {
        din=new DataInputStream(s.getInputStream());
        dout=new DataOutputStream(s.getOutputStream());
        try {
            line=din.readUTF();
             //line=din.readUTF();
            System.out.println("client says: "+line);
            if(!line.toLowerCase().contains("hello")){
                System.out.println("client not accepted_hand shaking problem");
                dout.writeUTF(" -_-_-_- ");
                return;
            }//else;
            while(line.compareTo("QUIT_S")!=0){   
                System.out.println("Send status to client");
                String str2=instance.GetStatusFromMenu();
                dout.writeUTF(str2);
                dout.flush();
                
                str2 = din.readUTF();
                System.out.println("Command from client is :" + str2 );
                
                //send commend for exe
                instance.SendCommand(str2);
            }
        } catch (IOException e) {
            line=this.getName(); //reused String line for getting thread name
            System.out.println("IO Error/ Client "+line+" terminated abruptly");
        }
        catch(NullPointerException e){
            line=this.getName(); //reused String line for getting thread name
            System.out.println("Client "+line+" Closed");
        }
        finally{
            try{
                System.out.println("Connection Closing..");
                if (din!=null){
                    din.close(); 
                    System.out.println(" Socket Input Stream Closed din");
                }
                if(dout!=null){
                    dout.close();
                    System.out.println("Socket Out Closed");
                }
                if (s!=null){
                    s.close();
                    System.out.println("Socket Closed");
                }
            }
            catch(IOException ie){
                System.out.println("Socket Close Error");
            }
        }//end finally
    } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex); 
    }//end finally
    }
}