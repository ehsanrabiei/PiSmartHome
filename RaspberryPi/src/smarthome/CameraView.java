
package smarthome;
import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class CameraView extends javax.swing.JFrame {

    // Directory to save pictures to
    String saveDir = "/home/pi/Desktop/SmartHome/SavedImg/";
    RPiCamera camera ;
    public CameraView() {
        initComponents();
        try {
            setCameraSetting();
        } catch (FailedToRunRaspistillException | IOException | InterruptedException ex) {
            Logger.getLogger(CameraView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public CameraView(boolean f){
         try {
            setCameraSetting();
        } catch (FailedToRunRaspistillException | IOException | InterruptedException ex) {
            Logger.getLogger(CameraView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setCameraSetting() throws FailedToRunRaspistillException, IOException, InterruptedException{
      camera = new RPiCamera(saveDir)
        .setWidth(500)   // Set width property of RPiCamera.
        .setHeight(350); // Set height property of RPiCamera.
         camera.setFullPreviewOff();//turn of preview 
      System.out.println("Camera Is ready");
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Camera View");
        setMinimumSize(new java.awt.Dimension(630, 425));
        setResizable(false);

        jButton1.setBackground(new java.awt.Color(0, 153, 153));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setText("Refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/smarthome/cameDefault.png"))); // NOI18N

        jButton2.setBackground(new java.awt.Color(0, 102, 102));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton2.setText("auto Refresh");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 204, 204));
        jButton3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton3.setText("Take and save");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3))
                    .addComponent(jLabel1))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            TakeOnePicture(false);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(CameraView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //auto refresh on
        AutoTakePic();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // take and save
        if(t!=null)t.cancel();//stop auto refresh first
        try {
            TakeOnePicture(true);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(CameraView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    public void TakeOnePicture(boolean Save) throws IOException, InterruptedException{
        String filename = "";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("SM_yyyy-MM-dd HH-mm-ss");  
        LocalDateTime now = LocalDateTime.now();  
        filename = dtf.format(now) + ".jpg" ;
        //////////////////////////////////
        if(Save) 
            camera.takeStill(filename);
        else 
            camera.takeStill(filename); //create .trash file and remove it every time program started
        //////////////////////////////////
        System.out.println("cam img saved to " + saveDir+filename);
        //show on ui
        ShowImageOnUi(saveDir+filename);
        
    }
    public void ShowImageOnUi(String filePath){
        if(filePath==null || filePath.length()<5) return;
        ImageIcon icon = new ImageIcon(filePath);
        // comment below code
        
        //filePath="abc.jpg" ;
        File test = new File(filePath);
        if(test.exists())
            //jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource(filePath)));
               jLabel1.setIcon(icon);
        else
            System.out.println("Image Not Found XXXXXXXXX");
        
        jLabel1.repaint();
    }
    
    Timer t = new Timer();
    public void AutoTakePic(){
        // take and show pic every 3 sec 
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    TakeOnePicture(false);
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(CameraView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 0, 3500);
        
    }

    public String JustTakeImage(){
        if(camera==null) return "T";
        String filename = "";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("SM_yyyy-MM-dd HH-mm-ss");  
        LocalDateTime now = LocalDateTime.now();  
        filename = dtf.format(now) + ".jpg" ;
        try {
            camera.takeStill(filename);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(CameraView.class.getName()).log(Level.SEVERE, null, ex);
        }
        return filename;        
    }
    
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CameraView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CameraView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CameraView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CameraView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CameraView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
