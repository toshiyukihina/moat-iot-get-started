MOAT IoT Application Example
========
Simple Example Android with Arduino Application
--------

This example works with `simple-example-android-arduino` and `simple-example-js` project files.

## Expected Devices

You need to prepare the following devices:

- [Arduino UNO R3](http://arduino.cc/en/Main/Buy)
- [LCD Keypad Shield for Arduino](http://www.dfrobot.com/index.php?route=product/product&product_id=51#.UWZd56uSASg)

You can also customize the sketch so that you can choose your favorite gadgets!

## Bundled Libraries

The two libraries under directory "library" must copy to Arduino's library directory. This directory can be found somewhere in the [Arduino IDE](http://arduino.cc/en/main/software). Arduino IDE must be restarted after copying.

- [DallasTemperature](https://github.com/milesburton/Arduino-Temperature-Control-Library)
- [OneWire](https://github.com/ntruchsess/arduino-OneWire)

## Misc

The folder name uses '_' instead of '-' because of [an Arduino IDE's limitation](http://arduino.cc/forum/index.php/topic,23015.0.html).
