package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class InternalStorage {

    private InternalStorage() {
    }

    /**
     * Used to write an object to the internal storage
     *
     * @param context The context from which we are calling
     * @param key The object key
     * @param object The object to write
     * @throws IOException Exception
     */
    public static void writeObject(Context context, String key, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    /**
     * Attempt to read an object from the internal storage
     *
     * @param context The context from which we are calling
     * @param key The object key
     * @return The object
     * @throws IOException Exception
     * @throws ClassNotFoundException Exception
     */
    public static Object readObject(Context context, String key) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        return object;
    }
}