#include <BluetoothSerial.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_TSL2591.h>

#define LED_PIN 26  // External LED
#define SCL_PIN 22  // TSL2591 SCL
#define SDA_PIN 21  // TSL2591 SDA

BluetoothSerial SerialBT;
Adafruit_TSL2591 tsl = Adafruit_TSL2591(2591);
bool isConnected = false;

void configureSensor() {
    tsl.setGain(TSL2591_GAIN_MED);  // Medium gain
    tsl.setTiming(TSL2591_INTEGRATIONTIME_100MS);  // 100ms integration time
}

void setup() {
    Serial.begin(115200);
    SerialBT.begin("BrightX");  // Bluetooth device name

    pinMode(LED_PIN, OUTPUT);
    digitalWrite(LED_PIN, LOW);  // LED starts OFF

    Wire.begin(SDA_PIN, SCL_PIN);
    if (!tsl.begin()) {
        Serial.println("No TSL2591 sensor found! Check wiring.");
        while (1);
    }

    configureSensor();
    Serial.println("ESP32 Bluetooth Serial Ready.");
}

void loop() {
    if (SerialBT.connected()) {
        if (!isConnected) {
            digitalWrite(LED_PIN, HIGH);  // Turn on LED when connected
            isConnected = true;
            Serial.println("Bluetooth Connected: LED ON");
        }

        // Read TSL2591 sensor data
        uint32_t fullSpectrum, infrared;
        fullSpectrum = tsl.getFullLuminosity();
        infrared = fullSpectrum >> 16;  // Extract IR part
        uint32_t visibleLight = fullSpectrum - infrared;  // Calculate visible light
        float lux = tsl.calculateLux(infrared, visibleLight);  // Calculate Lux

        // Format data
        String dataString = "Lux: " + String(lux, 2) + " | IR: " + String(infrared) + " | Spectrum: " + String(visibleLight);
        SerialBT.println(dataString);
        Serial.println("Sent Data: " + dataString);
    } else {
        if (isConnected) {
            digitalWrite(LED_PIN, LOW);  // Turn off LED when disconnected
            isConnected = false;
            Serial.println("Bluetooth Disconnected: LED OFF");
        }
    }

    delay(100);
}