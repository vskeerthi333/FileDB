import com.fileDB.DBStore;
import com.fileDB.DataStore;
import com.fileDB.Exception.ConstrainViolationException;
import com.fileDB.Exception.DataStoreException;
import com.fileDB.Exception.KeyAlreadyExistsException;
import com.fileDB.Exception.KeyNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class testDataStore {
    private static DataStore dataStore;

    @BeforeEach
    public void getDataStoreInstance() throws FileNotFoundException {
        dataStore = new DBStore("./testDB");
    }
    
    @Test
    public void testCreateAndRead() throws IOException, DataStoreException {
        dataStore.create("key-1", "value-1");

        String result = dataStore.read("key-1");
        assertEquals("value-1", result);

    }

    @Test
    public void testCreateAndDelete() throws IOException, DataStoreException {
        dataStore.create("key-2", "value-2");

        dataStore.delete("key-2");

        assertThrows(DataStoreException.class,
                () -> dataStore.delete("key-2"));
    }

    @Test
    public void testReadUnavailableKey() throws FileNotFoundException {
        assertThrows(KeyNotFoundException.class,
                () -> dataStore.read("unknown-key"));
    }

    @Test
    public void testCreateSameKeyAgain() throws IOException, DataStoreException {
        dataStore.create("newKey-1", "newKey-value");
        assertThrows(KeyAlreadyExistsException.class,
                () -> dataStore.create("newKey-1", "new-value"));
    }

    @Test
    public void testReadKeyAfterTTL() throws IOException, DataStoreException {
        dataStore.create("key-expire", "value-expire", 1);

        assertThrows(KeyNotFoundException.class, () -> dataStore.read("key-3"));
    }

    @Test
    public void testCreatingSameKeyAfterTTL() throws IOException, DataStoreException {
        dataStore.create("new-key-4", "value-4", 1);

        try {
            Thread.sleep(1000 * 60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dataStore.create("new-key-4", "new-value-4");
        String result = dataStore.read("new-key-4");

        assertEquals("new-value-4", result);
    }

    @Test
    public void testCreateLargeKey() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 35; i++) {
            str.append("x");
        }

        String largeKey = str.toString();
        assertThrows(ConstrainViolationException.class,
                () -> dataStore.create(largeKey, "largeKey-value"));
    }

    @Test
    public void testCreateLargeValue() {
        char[] value = new char[(16 * 1024) + 1];
        Arrays.fill(value, 'x');
        String largeValue = new String(value);

        assertThrows(ConstrainViolationException.class,
                () -> dataStore.create("largeValue-key", largeValue));

    }

    @Test
    public void testCreateInfiniteKeys() throws IOException, DataStoreException {
        int sizeRequiredInChars = (15 * 1024)/2;
        char[] garbage = new char[sizeRequiredInChars];
        Arrays.fill(garbage, 'x');

        for (int i = 10; i < 10001; i++) {
            if (i % 2 == 0) {
                dataStore.create(String.format("key-%d", i), String.format("value-%d", i));
            } else {
                dataStore.create(String.format("key-%d", i), String.format("value-%d", i), 1);
            }
        }

        String result = dataStore.read("key-12");
        assertEquals("value-12", result);

        assertThrows(KeyNotFoundException.class,
                () -> dataStore.read("key-13"));

    }
}
