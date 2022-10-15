# Project 7: ITIS 5280
## UNC Charlotte | Advanced Mobile Application Development
### Members:
- Alex Miller
- Tom Va
- Jared Tamulynas

## Link to Youtube Channel
[Project Demo](https://youtu.be/CvF-QhZMxes)

## Project Purpose
This assignment focuses communicating with a smart BLE bulb that is also capable of beeping and measuring temperature.

Smart BLE Bulb Android Simulator: You are provided with an Android app that simulates the Smart BLE bulb functionalities. 
To install the app on an Android phone you should open the following link on an android phone https://www.dropbox.com/s/33eiu8i3jndzkf8/app-release.apk?dl=0Links to an external site.

The first time you install the app will create a unique SERVICE UUID.
When discovering (scanning) for devices your should look for device called "Smart Bulb" and has your assigned SERVICE UUID. You should use Android BLE ScanFilter https://developer.android.com/reference/android/bluetooth/le/ScanFilterLinks to an external site. 

Below are the characteristics provided by the app and how to communicate with them. 

Characteristic 	                  UUID 	                                      Features
Bulb Characteristic 	              FB959362-F26E-43A9-927C-7E17D8FB2D8D 	      READ/WRITE Write 0 will turn the bulb OFF Write 1 will turn the bulb ON
Temperature Characteristic 	      0CED9345-B31F-457D-A6A2-B3DB9B03E39A 	      READ/NOTIFY
Beep Characteristic 	              EC958823-F26E-43A9-927C-7E17D8F32A90 	      READ/WRITE Write 0 will stop the beeping Write 1 will start the beeping

Application Requirements:
- You should build an application that connects to the Smart BLE bulb by searching for peripherals that provide the SERVICE UUID (which is displayed on the Smart BLE Bulb app provided).
- Your app should be able to turn off and on the bulb.
- Your app should periodically listen to the temperature notifications and update the displayed temperature based on the received notifications.
- Your app should provide a feature to trigger the beeping of the bulb.

#### Submission should include:
- Create a Github or Bitbucket repo for the assignment.
- Push your code to the created repo. Should contain all your code. 
- On the same repo create a wiki page describing your app design and implementation. 
- A 5 minute (max) screencast to demo your application. The video should demo the features of your app, it should show both the Smart BLE bulb screen and your app. Make sure to submit the video as a Youtube video.
