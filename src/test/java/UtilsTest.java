import dev.donhk.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest {
    @Test
    public void test1() {
        assertEquals("/this-file-has--s-aces.txt", Utils.urlEncode("/this file has  s[aces.txt"));
    }
}
