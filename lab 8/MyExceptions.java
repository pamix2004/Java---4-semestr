public class MyExceptions {


    public static class EmptyStack extends RuntimeException {
        public EmptyStack() {
            super("Stack jest pusty!");
        }
    }

    public static class FullStack extends RuntimeException {
        public FullStack() {
            super("Stack jest pełny!");
        }
    }

    public static class StackOutOfRange extends RuntimeException {
        /**
         * @param maximum_size is max elements that stack can hold at the time
         * */
        public StackOutOfRange(int maximum_size) {
            super("Błąd, stos przechowuje element od 0 do "+maximum_size+"!");
        }
    }


    public static class TooMuchOperators extends RuntimeException {
        public TooMuchOperators(String message) {
            super(message);
        }
    }

    public static class NotEnoughOperands extends RuntimeException {
        public NotEnoughOperands(String message) {
            super(message);
        }
    }

    /**
     * Wyjątek ten powinien być wyrzucony gdy równanie ONP zawiera operator który nie jest obsługiwany.
     * */
    public static class InvalidOperator extends RuntimeException {
        public InvalidOperator(String message) {
            super(message);
        }
    }

    /**
     * Wyjątek ten powinien być wyrzucony gdy równanie ONP zawiera operator który nie jest obsługiwany.
     * */
    public static class InvalidEquation extends RuntimeException {
        public InvalidEquation(String message) {
            super(message);
        }
    }


}
