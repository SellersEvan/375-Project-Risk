import org.junit.jupiter.api.Test;
import view.Main;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ProductionModeTest {

    @Test
    void validateProductionMode() {
        assertFalse(Main.DEVELOPER_MODE);
    }

}
