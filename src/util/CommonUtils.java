package util;

import javafx.scene.control.TextField;

public class CommonUtils {

    public static boolean tryParseDouble(TextField field) {
        try {
            Double.parseDouble(field.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean tryParseInt(TextField field) {
        try {
            Integer.parseInt(field.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Double getSafeDouble(TextField field) {
        if (tryParseDouble(field)) {
            return Double.parseDouble(field.getText());
        } else {
            return 0.00;
        }
    }

    public static int getSafeInt(TextField field) {
        if (tryParseInt(field)) {
            return Integer.parseInt(field.getText());
        } else {
            return 0;
        }
    }
}
