/*
    developer Ehsan Rabiei --> [Ehsan645**gmail.com]
    Apache V2 Licence
*/


package pro.blackrainbow.pismarthome;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Menu extends AppCompatActivity {
    TextView heyUser;
    TextView ConnectStatus;// show connect or disconnect
    EditText IpAddView ;
    Button connect ;

    // limited buttons
    Button sendmail , room1 , unlock , turnOfAlarm , motionAlarm , ignoreMotion ;

    //Ui elements for Status
    TextView room1Status , room2Status , otherDeviceStatus , sendEmailStatus ,
            ignoreMotionStatus , motionAlarmStatus ;

    private SpeechRecognizer speechRecognizer ;
    private TextToSpeech textToSpeech ;
    private Intent intent;

    TextView commandText ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //ask for permitions
        ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE}
                , PackageManager.PERMISSION_GRANTED ) ;

        //intilize interface components
        heyUser       = (TextView) findViewById(R.id.userhey)       ;
        IpAddView     = (EditText) findViewById(R.id.IpInput)       ;
        connect       = (Button) findViewById(R.id.connectBtn)      ;
        ConnectStatus = (TextView) findViewById(R.id.ConnectStatus) ;

        ////////
        sendmail     = (Button) findViewById(R.id.sendmailBtn)  ;
        room1        = (Button) findViewById(R.id.room1Btn)     ;
        unlock       = (Button) findViewById(R.id.unlock)       ;
        turnOfAlarm  = (Button) findViewById(R.id.turnOfAlarm)  ;
        motionAlarm  = (Button) findViewById(R.id.motionAlarm)  ;
        ignoreMotion = (Button) findViewById(R.id.ignoreMotion) ;
        ///////
        // Status Filds
        room1Status        = (TextView) findViewById(R.id.room1Status)        ;
        room2Status        = (TextView) findViewById(R.id.room2Status)        ;
        otherDeviceStatus  = (TextView) findViewById(R.id.otherDeviceStatus)  ;
        sendEmailStatus    = (TextView) findViewById(R.id.sendEmailStatus)    ;
        ignoreMotionStatus = (TextView) findViewById(R.id.ignoreMotionStatus) ;
        motionAlarmStatus  = (TextView) findViewById(R.id.motionAlarmStatus)  ;


        //change value of Interface
        Bundle extras = getIntent().getExtras();
        String userType = "" ;
        if (extras != null) {
            userType = extras.getString("usertype");
            heyUser.setText("Hello " + userType );
            heyUser.setTextColor(Color.GREEN);
            UserPermitionSetter(userType);
        }else{
            heyUser.setText("Something is wrong login again");
            heyUser.setTextColor(Color.RED);
        }

        ///////voice to speech
        commandText = (TextView) findViewById(R.id.CommandView) ;
        //voiceBtn    = (FloatingActionButton)   findViewById(R.id.Voice_Btn) ;
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        }) ;
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM) ;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {            }
            @Override
            public void onBeginningOfSpeech() {            }
            @Override
            public void onRmsChanged(float v) {            }
            @Override
            public void onBufferReceived(byte[] bytes) {            }
            @Override
            public void onEndOfSpeech() {            }
            @Override
            public void onError(int i) {            }
            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION) ;

                TextCommandExcuter(matches);///
            }
            @Override
            public void onPartialResults(Bundle bundle) {            }
            @Override
            public void onEvent(int i, Bundle bundle) {            }
        });
    }

    public void  UserPermitionSetter(String userType) {
        switch (userType){
            case "Admin" : return ; // do nothing
            case "user1" : {
                sendmail.setEnabled(false);
                room1.setEnabled(false);
                unlock.setEnabled(false);
                turnOfAlarm.setEnabled(false);
                motionAlarm.setEnabled(false);
                ignoreMotion.setEnabled(true);
            }break;
            case "user2" : {
                sendmail.setEnabled(false);
                room1.setEnabled(false);
                unlock.setEnabled(true);//this user can unlock the door
                turnOfAlarm.setEnabled(false);
                motionAlarm.setEnabled(false);
                ignoreMotion.setEnabled(false);
            }break;
            default:break;
        }
    }

    //btns events
    public void StartRecorde(View vl) throws InterruptedException {
        commandText.setText("Please tell me , how can i help you");
        textToSpeech.speak("how can i help you?" , TextToSpeech.QUEUE_FLUSH , null , null) ;
        try{Thread.sleep( 3000);}catch (InterruptedException e){
            commandText.setText("InterruptedException");
        }
        speechRecognizer.startListening(intent);
    }
    public void ConnectBtnEvent(View v)  {
        ip = IpAddView.getText().toString();
        if(ip==null || ip.length() <5 || !isNetworkConnected()){
            //
            Log.i("ip/internet " , "ConnectBtnEvent ...") ;
            return;
        }

        nt.start();

        Log.d("ConnectBtnEvent", "ConnectBtnEvent: done");
        try {

            connect.setEnabled(false);
            ConnectStatus.setText("Connected");
        }  catch (Exception e) {
            Log.d("ConnectBtnEvent", "ConnectBtnEvent: Exception");
            connect.setEnabled(true);
            ConnectStatus.setText("Disconnected");
            e.printStackTrace();
        }
    }
    public void SendCommand(View v){
           // this.Command = "1011" ;
        switch (v.getId()){

            case R.id.room1Btn:{
                this.Command = "1011" ;
            }break;
            case R.id.room2Btn:{
                this.Command="2011" ;
            }break;
            case R.id.unlock:{ //
                this.Command = "3001";
            }break;
            case R.id.otherDeviceBtn:{
                this.Command = "4011";
            }break;
            case R.id.turnOfAlarm:{
                this.Command = "7001";
            }break;
            case R.id.motionAlarm:{
                this.Command = "6011";
            }break;
            case R.id.ignoreMotion:{
                this.Command = "8091";
            }break;
            case R.id.sendmailBtn:{
                this.Command ="9001";
            }break;
            default:{//for unkown command just send refresh Command
                this.Command="6661" ;//refresh command this command ignored from server
            }break;
        }
    }

    //extra method
    //translate and send command from voice
    public void TextCommandExcuter(ArrayList<String> cmd){
        if(cmd==null ) return ;
        String voice ;
        commandText.setText(voice =TextUtils.join(" ", cmd));
        switch (voice.toLowerCase()){

            case "room" :
            case "room 1" :
            case "room one" :{
                Command="1011";
            }break;

            case "two" :
            case "room two" :
            case "room 2" :{
                Command="2011";
            }break;

            case "other device":
            case "device" :{
                Command="4011";
            }break;

            case "sent mail" :
            case "email" :
            case "send email" :{
                Command="9001";
            }break;
            case "door" :
            case "unlock" :{
                Command="3001";
            }break;
            case "camera" :{
                Command="5001";//not suport yet
            }break;

            case "motion" :
            case "motion alarm" :{
                Command="6011";
            }break;

            case "turn off alarm" :
            case "turn off" :{
                Command="7001";
            }break;

            case "ignore motion" :{
                Command="8091";
            }break;
            default:{
                Command="6661";//refresh status command
            }break;
        }
    }

    //check internet connection
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    //set status to app ui
    private void SetStatusToUI(){
        if(status==null || status.length()<14) {
            room1Status.setText("NAN");
            return;
        }
        //else
        if(!status.contains("XVS")){//if the status is not valid
            room1Status.setText("NAN");
            room2Status.setText("NAN");
            motionAlarmStatus.setText("NAN");
            Log.i("Set Status to UI" , "Invalid Status ");
            return;
        }
        Log.i("STATUS" , this.status) ;
        StringTokenizer tokenizer = new StringTokenizer(this.status , "-") ;//split status XVS-A-B-C--E-F-xvs

        Log.i("tokenizer" , tokenizer.nextToken());


        Log.i("tokenizer" , tokenizer.toString());
        if(tokenizer.nextToken().equals("1")){
            room1Status.setText("Room 1 : ON");
        }else{//zero
            room1Status.setText("Room 1 : OFF");
        }
        if(tokenizer.nextToken().equals("1")){
            room2Status.setText("Room 2 : ON");
        }else{
            room2Status.setText("Room 2 : OFF");
        }
        if(tokenizer.nextToken().equals("1")){
            otherDeviceStatus.setText("Other device : ON");
        }else{
            otherDeviceStatus.setText("Other device : OFF");
        }
        if(tokenizer.nextToken().equals("1")){
            motionAlarmStatus.setText("Motion Alarm : ON");
        }else{
            motionAlarmStatus.setText("Motion Alarm : OFF");
        }
        if(tokenizer.nextToken().equals("1")){
            ignoreMotionStatus.setText("Ignore motion : ON");
        }else{
            ignoreMotionStatus.setText("Ignore motion : OFF");
        }
        if(tokenizer.nextToken().equals("1")){
            sendEmailStatus.setText("Send Email : ON");
        }else{
            sendEmailStatus.setText("Send Email : OFF");
        }
    }
    /////////////////////////////////////
    String ip ="192.168.1.82";
    ClientSocketHandler Client = new ClientSocketHandler(this);
    Thread nt = new Thread(Client);
    String status = "" ;//server status
    String Command = "" ;
    int UpdateStatusTime = 10 ;//every 5 second status will be refresh ;\

    public class ClientSocketHandler implements Runnable{
        public Socket           socket;
        private final int        SERVERPORT = 3333;
        public  DataInputStream  din ;
        public  DataOutputStream dout ;
        private Menu instance ;

        public ClientSocketHandler(Menu Instance){
            if(this.instance==null)
                this.instance = Instance;
        }

        @Override
        public void run() {
            try {
                //InetAddress serverAddr = InetAddress.getByName(ip);
                socket = new Socket(ip, SERVERPORT);
                din  = new DataInputStream(socket.getInputStream());
                dout = new DataOutputStream(socket.getOutputStream());
                Log.i("EEEE", "run: socket ");

                dout.writeUTF("hello");//hand shaking
                dout.flush();
                instance.status =din.readUTF();
                instance.SetStatusToUI();
                Log.i("status", "run: socket ^^^^^  " +status);

                int CounterUpdate = 0 ;

                while(status.compareTo("QUIT_S") != 0){//loop for ever
                    if(instance.Command.length() > 2){//if Command is not empty
                        //send it
                        dout.writeUTF(instance.Command);//send command via Socket to Raspberry pi Server
                        dout.flush();
                        instance.status =din.readUTF();
                        instance.Command="";//clear command after send it
                        instance.SetStatusToUI();
                    }//if
                    else{ // if command is empty just wait a second
                        try {
                            Thread.sleep(1000);// wait
                            CounterUpdate++;
                            if(CounterUpdate > UpdateStatusTime){
                                instance.Command="6661" ;//refresh Status
                                CounterUpdate = -1 ;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }//else
                }//end of while

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
                Log.e("*/*", "run: UnknownHostException");
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e("*-*", "run: IOException");
            }

        }
    }// END OF THREAD CLASS


}
