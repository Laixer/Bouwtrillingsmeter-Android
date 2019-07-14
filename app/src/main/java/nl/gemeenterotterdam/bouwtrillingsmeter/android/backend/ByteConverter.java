package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.location.Address;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Used to handle converting objects to byte arrays
 * and back.
 */
public class ByteConverter {

    /**
     * Converts an {@link Address} object to a byte array.
     *
     * @param address The address to convert
     * @return The byte array
     * @throws ByteConverterException If we fail
     */
    public static byte[] addressToBytes(Address address) throws ByteConverterException {

        ByteArrayOutputStream byteStream = null;
        ObjectOutputStream objectStream = null;

        try {

            byteStream = new ByteArrayOutputStream();
            objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(address);
            return byteStream.toByteArray();

        } catch (IOException e) {
            System.out.println(e.toString());
        } finally {
            if (byteStream != null) try {
                byteStream.close();
            } catch (IOException e) {
                /* Do nothing */
            }

            if (objectStream != null) try {
                objectStream.close();
            } catch (IOException e) {
                /* Do nothing */
            }
        }

        // If we reach this we failed
        throw new ByteConverterException("Could not write address to bytes: " + address.toString());

    }

    /**
     * Converts a byte array to an {@link Address} object.
     *
     * @param byteArray The array
     * @return The Address
     * @throws ByteConverterException If we fail
     */
    public static Address bytesToAddress(byte[] byteArray) throws ByteConverterException {

        ByteArrayInputStream byteStream = null;
        ObjectInputStream objectStream = null;

        try {

            byteStream = new ByteArrayInputStream(byteArray);
            objectStream = new ObjectInputStream(byteStream);
            return (Address) objectStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.toString());
        } finally {
            if (byteStream != null) try {
                byteStream.close();
            } catch (IOException e) {
                /* Do nothing */
            }

            if (objectStream != null) try {
                objectStream.close();
            } catch (IOException e) {
                /* Do nothing */
            }
        }


        throw new ByteConverterException("Could not convert byte array to Address");
    }

}
