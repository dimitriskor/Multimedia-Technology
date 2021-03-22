package sample;

import java.lang.Exception;

public class CustomExceptions {
    public static class OversizeException extends Exception {
        public OversizeException(String s) {
            super(s);
        }
    }

    public static class InputFileException extends Exception {
        public InputFileException(String s) {
            super(s);
        }
    }

    public static class OverlapTilesException extends Exception {
        public OverlapTilesException(String s) {
            super(s);
        }
    }

    public static class InvalidCountExeception extends Exception {
        public InvalidCountExeception(String s) {
            super(s);
        }
    }
}
