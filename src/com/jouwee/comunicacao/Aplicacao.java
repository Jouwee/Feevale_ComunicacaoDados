package com.jouwee.comunicacao;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbHub;
import org.omg.CORBA.TIMEOUT;
import org.usb4java.BufferUtils;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;
import org.usb4java.TransferCallback;

/**
 *
 * @author Nícolas Pohren
 */
public class Aplicacao {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        lowLevel();
    }

    public static void highLevel() {
//        findDevice(UsbR, 0, 0)
    }

    public static UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) {
                return device;
            }
            if (device.isUsbHub()) {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) {
                    return device;
                }
            }
        }
        return null;
    }

    public static void lowLevel() {
        Context context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to initialize libusb.", result);
        }

        // Read the USB device list
        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(null, list);
        if (result < 0) {
            throw new LibUsbException("Unable to get device list", result);
        }

        try {
            // Iterate over all devices and scan for the right one
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
                System.out.println("---");
                System.out.println(descriptor.toString());

                DeviceHandle handle = new DeviceHandle();
                result = LibUsb.open(device, handle);
                if (result != LibUsb.SUCCESS) {
                    System.out.println("*** Unable to open USB device " + result);
                    continue;
                }
                System.out.println("Connected!");
                try {

                    result = LibUsb.claimInterface(handle, 0);
                    if (result != LibUsb.SUCCESS) {
                        System.out.println("*** Unable to claim interface " + result);
                    }
                    try {

                        
                        ByteBuffer buffer = BufferUtils.allocateByteBuffer(8).order(
                                ByteOrder.LITTLE_ENDIAN);
                        Transfer transfer = LibUsb.allocTransfer();
                        LibUsb.fillBulkTransfer(transfer, handle, (byte)0x83, buffer,
                                new TransferCallback() {
                            @Override
                            public void processTransfer(Transfer trnsfr) {
                                System.out.println("uhiqwdhuiqwhdi");
                            }
                        }, null, 100);
                        System.out.println("Reading " + 8 + " bytes from device");
                        result = LibUsb.submitTransfer(transfer);
                        
//                        ByteBuffer buffer = ByteBuffer.allocateDirect(8);
//                        buffer.put(new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
//                        int transfered = LibUsb.controlTransfer(handle,
//                                (byte) (LibUsb.REQUEST_TYPE_CLASS | LibUsb.RECIPIENT_INTERFACE),
//                                (byte) 0x09, (short) 2, (short) 1, buffer, 100);
                        if (result != LibUsb.SUCCESS) {
                            new LibUsbException("Unable to submit transfer", result).printStackTrace(System.out);
                        }

                    } finally {
                        result = LibUsb.releaseInterface(handle, 0);
                        if (result != LibUsb.SUCCESS) {
                            System.out.println("*** Unable to release interface " + result);
                        }
                    }

                } finally {
                    LibUsb.close(handle);
                }

            }

        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        LibUsb.exit(context);

    }

}
