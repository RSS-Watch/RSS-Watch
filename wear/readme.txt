To enable debugging on an android wear device, connect everything,
enable bluetooth debugging on the smartphone and on the watch,
get all the adb drivers up and running on your PC and run the following
commands from your computer:

adb forward tcp:4444 localabstract:/adb-hub
(this creates a tunnel through your device to the watch)

adb connect localhost:4444
(this actually connects your computer to the watch)

To check, if everything works correctly run
adb devices
This should show two devices, your phone and the watch and the
status "device" at both of them.