
#  <h1 align="center" >PiSmartHome</h1>

## Smart Home using Raspberry pi

### Controll raspberry pi 3/4 GPIO pins and piCam using java  

>* ## Features
>   * friendly interface to controll your SmartHome
>   * Android Application use local network to controll SmartHome
>   * support PiCamera 
>   * Unlock the Door with android app/raspApp
>   * controll your home lights
>   * Voice Command Android app
>   * Detect Gas leak and flame , and set the Alarms on
>   * Sending camera picture to you , when your not home and someone push doorbell
>   * have secret key to open door with 3buttons pattern

 
## picture of interface :

> main interface :
> 
<img src="https://github.com/ehsanrabiei/PiSmartHome/blob/main/Doc%26Images/mainMenu.JPG" alt="Main interface" width="450" height="">

> camera View interface :
> 
<img src="https://github.com/ehsanrabiei/PiSmartHome/blob/main/Doc%26Images/cameraView.JPG" alt="cameraView" width="450" height="">

> Android screenshots :
>
<img src="https://github.com/ehsanrabiei/PiSmartHome/blob/main/Doc%26Images/AndroidShot2.jpg" alt="androidShot1" width="300" height="">

<img src="https://github.com/ehsanrabiei/PiSmartHome/blob/main/Doc%26Images/AndroidShot1.jpg" alt="androidShot1" width="300" height="">


# Requirements
* ### hardware
    * Raspberry pi 3/4 ( B ) 
    * 2X , ['2channel relay delay module'](https://www.geeetech.com/wiki/index.php/2-Channel_Relay_module) 
    * Gas Leak Detector 
    * Flame sensor
    * motion sensor
    * buzzer
    * LED ( any color )
    * 3X push button
    * ['breadboard'](https://en.wikipedia.org/wiki/Breadboard )  
    * ....
* ### Software
    * Jre
    * Jdk 1.8
    * netbeans V 8.2 / higher
    * Android Studio 2020.3 / higher

# install and run
* ## Raspberry pi
using  ['this file'](https://github.com/ehsanrabiei/PiSmartHome/blob/main/Doc%26Images/GpioPins_guide.xlsx) to connect GPIO pins <br>
To install this program, download the following ['folder'](https://github.com/ehsanrabiei/PiSmartHome/tree/main/RaspberryPi/bin) and run the command in the [.sh] file.

* ## Android
for android app , download APK file from ['here'](https://github.com/ehsanrabiei/PiSmartHome/blob/main/Android/APK/SmartHome_debugBuild.apk)  and install it ( its debug build ) 
<br>there's three types of user
* * admin  12311 ( no limit )
* * user1  12322
* * user2  12345
<hr>

 # libs 
* ### javaX.mail
* ### pi4j
* ### Jrpicam
<hr>

 # Licence 
 
* ['Licence Apache 2'](https://www.apache.org/licenses/LICENSE-2.0)

<hr>
