/*
 
rspi4 GPIO HANDLER
 */
package smarthome;
//download and import pi4j lib  -> http://pi4j.com
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GpioHandler {

    final GpioController gpio ; 
    final GpioPinDigitalOutput Room1Out , Room2Out , UnlockDoor , OtherDevice , LEDAlert , Buzzer;
    final GpioPinDigitalInput MotionSensor , FlameSensor , GasSensor , DoorBell_BTN , Button_B , Button_C;
    
    // machine State
    private int State = 0 ;//0 motion sensor turn on the room1 lamp , 1 motion Sensor Turn On Alerts
    public void setState(int State) {
        this.State = State;
    }
    public int getState() {
        return State;
    }
    
    private int DoorBellState = 0 ;
    private String Pattern = "" ;
    
    public void setDoorBellState(int s){
        this.DoorBellState = s;
    }
    public int getDoorBellState(){
        return this.DoorBellState;
    }
    public boolean ignoreMotion = false;
    //defines
    private int turnOnLight = 6000 ;//time delay turn on light room1 by motion 
    private int TimeBuzz = 2000;//time delay buzzer BIZ
    
    public GpioHandler() {
        StaticLog.SaveInFile("Device Turned On");
        //config pins
        gpio = GpioFactory.getInstance();

        //CONFIG ALL PINS & provision gpio pin #01 as an output pin and turn on
        Room1Out   = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "Room1", PinState.HIGH);//#12
        Room2Out   = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "Room2", PinState.HIGH);//#13
        UnlockDoor = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "UDoor", PinState.HIGH);//#11
        OtherDevice= gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "OtherDevice", PinState.HIGH);//#15
        LEDAlert   = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, "LEDAlert", PinState.LOW);//#40
        Buzzer     = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "BUZZER", PinState.LOW);//#38
        //inputs 
        MotionSensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);//#16			
        FlameSensor  = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_DOWN);//#18
        GasSensor    = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06, PinPullResistance.PULL_DOWN);//#22
        // BTNS
        DoorBell_BTN = gpio.provisionDigitalInputPin(RaspiPin.GPIO_27, PinPullResistance.PULL_DOWN);//#36
        //buttons for open door without phone 
        Button_B    = gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, PinPullResistance.PULL_DOWN);//#35
        Button_C    = gpio.provisionDigitalInputPin(RaspiPin.GPIO_25, PinPullResistance.PULL_DOWN);//#37

        //init pins 
        
        Room1Out.setShutdownOptions   (true, PinState.HIGH);//4relays active means disable
        Room2Out.setShutdownOptions   (true, PinState.HIGH);//4relays active means disable
        UnlockDoor.setShutdownOptions (true, PinState.HIGH);//4relays active means disable
        OtherDevice.setShutdownOptions(true, PinState.HIGH);//4relays active means disable
        LEDAlert.low();//turn off by default
        Buzzer.low(); // turn off buzzer
        State = 0 ;// set motion sensor as RIP LAMP
        EventsSetter();
    }
    //add events for inputs pins
    public void EventsSetter(){
        //motion Sensor Event
        MotionSensor.addListener((GpioPinListenerDigital) (GpioPinDigitalStateChangeEvent event) -> {
        if(event.getState().isHigh()){
            //System.out.println("Motion DetectOR WORKING.....!");
            if(ignoreMotion) return;// do nothing ...
            if(this.getState()==0){
                TurnOnOrOFFRoom1(true);
                LEDAlert.high();
                             
                try {
                    Thread.sleep(turnOnLight);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GpioHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                TurnOnOrOFFRoom1(false);
                LEDAlert.low();
            }else{//security Alarms
              LEDAlert.high();
              Buzzer.high();
              StaticLog.SaveInFile("motion sensor Detected _ ");
               
              CameraView CV = new CameraView(false) ;
              String img = CV.JustTakeImage();
                
              SendMail sm = new SendMail();
              sm.SendIT(img, "motion", " motion Detected ") ;
                
                try {
                    Thread.sleep(TimeBuzz);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GpioHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                Buzzer.low();
            }
        }//end of if ( high)
        
        if(event.getState().isLow()){
          // System.out.println("All is quiet...");
            LEDAlert.low();
        }
      });
        
        // GAS SENSOR
        GasSensor.addListener((GpioPinListenerDigital) (GpioPinDigitalStateChangeEvent event) -> {
            if(event.getState().isHigh()){
                AlarmDetect(true);
                StaticLog.SaveInFile("Alarm Detected GAS _ ");
            }
            if(event.getState().isLow()){
              // System.out.println("All is quiet...");
                AlarmDetect(false);
            }
        });
        
     //Flame
     FlameSensor.addListener((GpioPinListenerDigital) (GpioPinDigitalStateChangeEvent event) -> {
            if(event.getState().isHigh()){
                AlarmDetect(true);
            }
            if(event.getState().isLow()){
              // System.out.println("All is quiet...");
                AlarmDetect(false);
            }
        
        });
     //DoorBell
     DoorBell_BTN.addListener((GpioPinListenerDigital) (GpioPinDigitalStateChangeEvent event) -> {
            if(event.getState().isHigh()){
                //Door ring
                System.out.println("A BTN");
               
                if(Pattern.length()>1){
//                       /..paternS ADD keys
                       Pattern +="A";
                       DoorBellState=0;
                       return ;
                 }  else{
                     PlaySound ring = new PlaySound();             
                     ring.play(0);
                }   
                //if DoorBellState is 0 do nothing
                if(DoorBellState==1){
                    //just send email
                   
                    CameraView cm = new CameraView(false);
                    String picFile= cm.JustTakeImage();
                    
                    try {
                        Thread.sleep(4000);
                        //send
                        SendMail mail = new SendMail();
                        System.out.println("Sending Email...");
                        boolean mflag = mail.SendIT(picFile, "Knock the Door" , " Smart home " ) ; 
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GpioHandler.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("smarthome_farnaz.GpioHandler.EventsSetter()" + ex.getMessage());
                    }
                   
                }
                
            }
        });
     
      Button_B.addListener((GpioPinListenerDigital) (GpioPinDigitalStateChangeEvent event) -> {
            if(event.getState().isHigh()){
                this.Pattern += "B";
                System.out.println("B BTN");
            }
        });
      Button_C.addListener((GpioPinListenerDigital) (GpioPinDigitalStateChangeEvent event) -> {
            if(event.getState().isHigh()){
                System.out.println("C BTN");
                this.Pattern += "C";
                //System.out.println("Pattern = " + Pattern);
                
                if(Pattern.equals("BBAACCBAC")){
                    Pattern="";
                    try {
                        UnlockTheDoor();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GpioHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }//pattern is accepted
                if(Pattern.length() > 9){
                    this.Pattern="";//reset pattern
                    StaticLog.SaveInFile("Someone try to use the 'unlock pattern' ");
                    CameraView cm = new CameraView(false);
                    String picFile= cm.JustTakeImage();
                    
                    try {
                        Thread.sleep(4000);
                        //send
                        SendMail mail = new SendMail();
                        System.out.println("Sending Email...");
                        boolean mflag = mail.SendIT(picFile, "Wrong Pattern Entered" , " Smart home " ) ; 
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GpioHandler.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("smarthome_farnaz.GpioHandler.EventsSetter()" + ex.getMessage());
                    }
                    AlarmDetect(true);
                }
            }
        });
      
      StaticLog.SaveInFile("All Sensors are online/active");
    }
    //all action when gas and flame detect 
    private void AlarmDetect(boolean active){
        if(active){
          try {
               StaticLog.SaveInFile("Alarms in ON");
                Buzzer.high();
                LEDAlert.blink(80);
                Thread.sleep(5000);
                Buzzer.low();
                Thread.sleep(350);
                Buzzer.high();
                Thread.sleep(3000);
                Buzzer.low();
                
            } catch (InterruptedException ex) {
                Logger.getLogger(GpioHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }else{
            //LEDAlert.low();
            Buzzer.low();
        }
    }
    public void turnOffAlarm(){
        LEDAlert.pulse(150);
        LEDAlert.low();
        Buzzer.low();
        AlarmDetect(false);
        StaticLog.SaveInFile("turn Off Alarms");
    }
    boolean Room1Flag =false;
    public boolean ToggleRoom1Lamp() throws InterruptedException{
      
        Thread.sleep(200);//sleep for 200 mil Sec 
        if(Room1Flag){
            Room1Flag =false;
            Room1Out.toggle();
            StaticLog.SaveInFile("Room1's Lamp is OFF");

        }else{
            Room1Flag =true;
            Room1Out.toggle();
           StaticLog.SaveInFile("Room1's Lamp is ON");
        }
        return Room1Flag;
    }
    public void  TurnOnOrOFFRoom1(boolean S){
        if(Room1Flag) return;
        if(S) Room1Out.low();//turn on lamp
        else Room1Out.high();//turn off lamp
        StaticLog.SaveInFile("TurnOnOrOFFRoom1 630");

    }
    boolean Room2Flag =false;
    public boolean ToggleRoom2Lamp() throws InterruptedException{
        Thread.sleep(200);//sleep for 200 mil Sec 
        if(Room2Flag){
            Room2Flag =false;
            Room2Out.toggle();
            StaticLog.SaveInFile("Room2 Lamp is OFF");
        }else{
            Room2Flag =true;
            Room2Out.toggle();
            StaticLog.SaveInFile("Room2 Lamp is ON");
        }
        return Room2Flag;
    }
    boolean OtherD2Flag =false;
    public boolean ToggleOtherDevice() throws InterruptedException{
        Thread.sleep(200);//sleep for 200 mil Sec 
        StaticLog.SaveInFile("Other device Toggle");

        if(OtherD2Flag){
            OtherD2Flag =false;
            OtherDevice.toggle();
        }else{
            OtherD2Flag =true;
            OtherDevice.toggle();
        }
        return Room2Flag;
    }
    public void UnlockTheDoor() throws InterruptedException{//pin of door have to always high as disable
        //for open the door for a few secund its be negative 
         Thread.sleep(50);//sleep for 50 mil Sec 
         UnlockDoor.low();
         Thread.sleep(1500);
         UnlockDoor.high();
         StaticLog.SaveInFile(" + Door Unlocked + ");

    }
}
