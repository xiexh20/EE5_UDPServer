import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.util.Console;

import java.io.IOException;
import java.util.Arrays;

/**
 * main class in Raspberry Pi for I2C communication
 */


public class PiI2CMain {
    // TSL2561 I2C address
    public static final int TSL2561_ADDR = 0x39; // address pin not connected (FLOATING)
    //public static final int TSL2561_ADDR = 0x29; // address pin connect to GND
    //public static final int TSL2561_ADDR = 0x49; // address pin connected to VDD

    // TSL2561 registers
    public static final byte TSL2561_REG_ID = (byte)0x8A;
    public static final byte TSL2561_REG_DATA_0 = (byte)0x8C;
    public static final byte TSL2561_REG_DATA_1 = (byte)0x8E;
    public static final byte TSL2561_REG_CONTROL = (byte)0x80;

    // TSL2561 power control values
    public static final byte TSL2561_POWER_UP = (byte)0x03;
    public static final byte TSL2561_POWER_DOWN = (byte)0x00;

    public static final byte PIC1_ADDR = 0x20;       // address of the PIC
    public static final byte PIC2_ADDR = 0x20;      // address of the second PIC

    public static final int FRAME_LEN = 10;      // the length of a frame

    /**
     * Program Main Entry Point
     *
     * @param args
     * @throws InterruptedException
     * @throws PlatformAlreadyAssignedException
     * @throws IOException
     * @throws I2CFactory.UnsupportedBusNumberException
     */
    public static void main(String[] args) throws InterruptedException, PlatformAlreadyAssignedException, IOException, I2CFactory.UnsupportedBusNumberException {

        // create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate code)
        final Console console = new Console();

        // print program title/header
        console.title("<-- The Pi4J Project -->", "I2C Example");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        // fetch all available busses
        try {
            int[] ids = I2CFactory.getBusIds();
            console.println("Found follow I2C busses: " + Arrays.toString(ids));
        } catch (IOException exception) {
            console.println("I/O error during fetch of I2C busses occurred");
        }

        // find available busses
        for (int number = I2CBus.BUS_0; number <= I2CBus.BUS_17; ++number) {
            try {
                @SuppressWarnings("unused")
                I2CBus bus = I2CFactory.getInstance(number);
                console.println("Supported I2C bus " + number + " found");

            } catch (IOException exception) {
                console.println("I/O error on I2C bus " + number + " occurred");
            } catch (I2CFactory.UnsupportedBusNumberException exception) {
                console.println("Unsupported I2C bus " + number + " required");
            }
        }

        // get the I2C bus to communicate on
        I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_1);

        // create an I2C device for an individual device on the bus that you want to communicate with
        // in this example we will use the default address for the TSL2561 chip which is 0x39.
//        I2CDevice device = i2c.getDevice(TSL2561_ADDR);
        I2CDevice device = i2c.getDevice(PIC1_ADDR);
        I2CDevice device2 = i2c.getDevice(PIC2_ADDR);
        //TODO: get address automatically


        // next, lets perform am I2C READ operation to the TSL2561 chip
        // we will read the 'ID' register from the chip to get its part number and silicon revision number
//        console.println("... reading ID register from TSL2561");
//        int response = device.read(TSL2561_REG_ID);
//        console.println("TSL2561 ID = " + String.format("0x%02x", response) + " (should be 0x50)");

        // next we want to start taking light measurements, so we need to power up the sensor
//        console.println("... powering up TSL2561");
        byte data1 = 0;
        byte data2 = 0b1111111;

//        byte recv = 0;      // received data
        while(true){

            // send 2 frames to PIC
            for(int k= 0; k<3; k++) {
                for (int i = 0; i < FRAME_LEN; i++) {
                    console.println("########################################");
                    console.println("Sending data[" + i + "] to PIC1: " + String.format("0x%02x", data1));
                    device.write(data1);
                    data1++;
                    Thread.sleep(300);
                }
            }

            // read one frame from PIC
            for(int i = 0;i<FRAME_LEN;i++)
            {
                int recv1 = device.read();
                console.println("data["+i+"] from PIC1: "+ String.format("0x%02x", recv1));
                Thread.sleep(600);
            }



            // send data to PIC2
//            console.println("-------------------------------------------");
//            console.println("Sending to PIC2, data= "+String.format("0x%02x", data2));
//            device2.write(data2);
//            data2--;
//            Thread.sleep(1);
//
//            int recv2 = device2.read();
//            console.println("received data from PIC2: "+ String.format("0x%02x", recv2));
//            Thread.sleep(400);
//
//            console.println("");
        }


        // print raw integration results from DATA_0 and DATA_1 registers
//        console.println("TSL2561 DATA 0 = " + String.format("0x%02x", data0));
//        console.println("TSL2561 DATA 1 = " + String.format("0x%02x", data1));

        // before we exit, lets not forget to power down light sensor
//        console.println("... powering down TSL2561");
//        device.write(TSL2561_REG_CONTROL, TSL2561_POWER_DOWN);
    }

}
