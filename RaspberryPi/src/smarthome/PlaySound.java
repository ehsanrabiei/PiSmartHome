
package smarthome;
import java.io.File;
import java.io.IOException;
 
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
 

public class PlaySound implements LineListener {
    boolean playCompleted;
    public void play(int AudioNum) {
        String audioFilePath = "/home/pi/Desktop/SmartHome/doorbell.wav";//addres ahango benvis
        String audioFilePath2 = "D://SmartHome_farnaz/ding.wav";
        
        File audioFile;
        if(AudioNum==0)
            audioFile = new File(audioFilePath);
        else
            audioFile = new File(audioFilePath2);

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat(); 
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            try (Clip audioClip = (Clip) AudioSystem.getLine(info)) {
                audioClip.addLineListener(this);
                audioClip.open(audioStream);
                audioClip.start();
                while (!playCompleted) {
                    // wait for the playback completes
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                }
            }  
        } catch (UnsupportedAudioFileException e) {
            System.out.println("The specified audio file is not supported.");
        } catch (LineUnavailableException e) {
            System.out.println("Audio line for playing back is unavailable.");
        } catch (IOException e) {
            System.out.println("Error playing the audio file.");
        }
         
    }
    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
        if (type == LineEvent.Type.START) {
           ;// System.out.println("Playback started.");
             
        } else if (type == LineEvent.Type.STOP) {
            playCompleted = true;
           ;// System.out.println("Playback completed.");
        }
    }
}