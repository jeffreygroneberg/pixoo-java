# Pixoo-Java

This is a Java library for interacting with Pixoo devices. It allows you to send images, animations, and display various information on your Pixoo device.

This project is a Java port and extension of the original Python library available at [https://github.com/SomethingWithComputers/pixoo](https://github.com/SomethingWithComputers/pixoo).

## Features

*   Send static images to the Pixoo device.
*   Play animated GIFs on the Pixoo device.
*   Configure device settings like brightness.
*   Support for both direct device communication and a simulator.

## Getting Started

### Prerequisites

*   Java Development Kit (JDK) 8 or higher.
*   Maven for building the project.
*   A Pixoo device (optional, a simulator is available).

### Configuration

1.  **Network Configuration**:
    The application requires the IP address and port of your Pixoo device. This is configured in the `src/main/resources/config.properties` file.

    You'll need to create this file if it doesn't exist, or modify the existing one. You can copy `src/main/resources/config.properties.example` to `src/main/resources/config.properties` to get started.

    Modify the `config.properties` file with the following content, replacing the example IP with your device's actual IP address and port:
    ```properties
    # Example configuration:
    ip.address=192.168.1.123:80
    ```
    Replace `192.168.1.123:80` with the actual IP address and port of your Pixoo device. If you are unsure, you might find this information in your router's DHCP client list or by using a network scanning tool.

2.  **Assets**:
    Place any local GIF files you want to display in the `assets/` directory in the root of the project. The `SimpleTest.java` demo uses `assets/test_2.gif` by default.

### Building the Project

You can build the project using Maven:

```bash
mvn clean install
```

This will compile the source code, run tests, and create a JAR file in the `target/` directory.

### Running the Demo

The `SimpleTest.java` class provides a simple demonstration of how to send an animated GIF to your Pixoo device.

1.  **Ensure Configuration**: Make sure you have configured the `config.properties` file as described above.
2.  **Run from IDE**: You can run the `main` method in `de.jeffreygroneberg.pixooj.demo.SimpleTest` directly from your IDE.
3.  **Run from Command Line (after building)**:
    Navigate to the project's root directory and execute the following command:

    ```bash
    mvn exec:java -Dexec.mainClass="de.jeffreygroneberg.pixooj.demo.SimpleTest"
    ```
    Or, if you have built the JAR:
    ```bash
    java -cp target/pixooj-1.0.0.jar de.jeffreygroneberg.pixooj.demo.SimpleTest
    ```
    (Replace `pixooj-1.0.0.jar` with the actual name of the generated JAR file if it differs).

    You should see output in your console indicating the attempt to send the animation, and the GIF should play on your Pixoo device.

## How it Works

The `Pixoo` class is the main entry point for interacting with the device. It handles the communication and provides methods for various operations.

The `SimpleTest` class demonstrates:
1.  Loading the Pixoo device IP address from `config.properties`.
2.  Creating an instance of the `Pixoo` object.
3.  Calling `sendAnimatedGif()` to send a local GIF file to the device.

## Simulator

If you don't have a physical Pixoo device, you can use the built-in simulator. To enable the simulator mode, you need to modify the `Pixoo` object instantiation in your code. For example, in `SimpleTest.java`:

```java
// To use the simulator:
// Pixoo pixoo = new Pixoo("127.0.0.1", 64, true, true, true, new SimulatorConfiguration());

// To use a real device (as it is now):
Pixoo pixoo = new Pixoo(ipAddress, 64, true, true, false, null);
```
When `simulated` is set to `true` and a `SimulatorConfiguration` is provided, a window will pop up simulating the Pixoo display.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues.

## Original Repository

This project is based on the work done in the [pixoo Python library by SomethingWithComputers](https://github.com/SomethingWithComputers/pixoo). Many thanks to the original authors for their work.

## License

This project is licensed under the [MIT License](LICENSE) - see the LICENSE file for details (assuming you will add one, or specify otherwise).
