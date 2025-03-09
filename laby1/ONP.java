/**
 * @author Sławek Klasa implementująca kalkulator ONP
 */
public class ONP {
    private TabStack stack = new TabStack();

    /**
     * Metoda sprawdza czy równanie INFIX(zwykłe) jest poprawne
     *
     * @param rownanie równanie do sprawdzenia
     * @return true jeśli równanie jest poprawne, false jeśli niepoprawne
     */
    boolean czyPoprawneRownanie(String rownanie) {
        //Usunmy spacje
        rownanie= rownanie.replaceAll("\\s","");
        //Sprawdzmy czy rownanie konczy się znakiem równości
        if (!rownanie.endsWith("=")) {
            throw new MyExceptions.InvalidEquation("Równanie nie jest zakonczone znakiem równości");
        }

        // Sprawdźmy czy ilość lewych nawiasów jest taka sama jak liczba prawych nawiasów
        int openParentheses = 0;
        for (int i = 0; i < rownanie.length(); i++) {
            if (rownanie.charAt(i) == '(') {
                openParentheses++;
            } else if (rownanie.charAt(i) == ')') {
                openParentheses--;
            }
        }
        //Jeżeli liczba nawiasów otwierających i zamykających nie zgadza się to wyrzućmy błąd
        if (openParentheses != 0) {
            throw new MyExceptions.TooMuchOperators("Liczba nawiasów się nie zgadza!");
        }


        // Sprawdzmy czy przedostatni znak to operator
        if (rownanie.length() > 1) {
            char secondLastChar = rownanie.charAt(rownanie.length() - 2);
            if (isOperator(secondLastChar)&&secondLastChar!='!'&&secondLastChar!='s') {
                throw new MyExceptions.InvalidEquation("Przedostatni element w równaniu to operator a dokładniej to:"+secondLastChar);
            }
        }

        for (int i = 0; i < rownanie.length(); i++) {
            char c = rownanie.charAt(i);
            if(!isOperator(c)&&!Character.isDigit(c)&&c!='('&&c!=')'&&c!='='&&c!=' '){
                throw new MyExceptions.InvalidOperator(c+" nie jest prawidłowym operatorem!");
            }
        }




        return true;
    }

    //Metoda pomocniczna do okreslenia operatorow arytmetycznych
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == 'x' || c == '%' || c == 's' || c == '!';
    }



    /**
     * Metoda oblicza wartość wyrażenia zapisanego w postaci ONP.
     * @param rownanie równanie zapisane w postaci ONP
     * @return wartość obliczonego wyrażenia
     */
    public String obliczOnp(String rownanie) {
        //ONP musi się kończyć znakiem równości
        if (rownanie.endsWith("=")) {
            stack.setSize(0);
            String wynik = "";
            Double a = 0.0;
            Double b = 0.0;
            //Iterujemy przez wyrażenie
            for (int i = 0; i < rownanie.length(); i++) {
                //Jeżeli dany znak jest cyfrą
                if (rownanie.charAt(i) >= '0' && rownanie.charAt(i) <= '9') {
                    wynik += rownanie.charAt(i);
                    if (!(rownanie.charAt(i + 1) >= '0' && rownanie.charAt(i + 1) <= '9')) {
                        stack.push(wynik);
                        wynik = "";
                    }
                }
                //Jezeli doszlismy do znaku rownosci to oznacza koniec rownania
                else if (rownanie.charAt(i) == '=') {
                    return stack.pop();
                }
                //Jezeli dane pole nie jest puste czyli jezeli jest operatorem
                else if (rownanie.charAt(i) != ' ') {
                    //Kazdy operator z wyjątkiem pierwiastka oraz silni potrzebuje dwóch argumentów
                    if (stack.getSize() < 2 && rownanie.charAt(i) != 's' && rownanie.charAt(i) != '!') {
                        throw new MyExceptions.NotEnoughOperands("Not enough operands for " + rownanie.charAt(i));
                    }
                    if (rownanie.charAt(i) == '!') {
                        a = Double.parseDouble(stack.pop());
                        stack.push(factorial(a) + "");
                    } else if (rownanie.charAt(i) == 's') {
                        a = Double.parseDouble(stack.pop());
                        stack.push(Math.sqrt(a) + "");  // Pierwiastkowanie
                    } else {
                        b = Double.parseDouble(stack.pop());
                        a = Double.parseDouble(stack.pop());
                        switch (rownanie.charAt(i)) {
                            case ('+'): {
                                stack.push((a + b) + "");
                                break;
                            }
                            case ('-'): {
                                stack.push((a - b) + "");
                                break;
                            }
                            case ('x'):
                            case ('*'): {
                                stack.push((a * b) + "");
                                break;
                            }
                            case ('/'): {
                                stack.push((a / b) + "");
                                break;
                            }
                            case ('^'): {
                                stack.push(Math.pow(a, b) + "");
                                break;
                            }
                            case ('%'): {
                                stack.push((a % b) + "");
                                break;
                            }
                        }
                    }
                }
            }
            return "0.0";
        } else
            throw new MyExceptions.InvalidEquation("Rownanie nie konczy sie znakiem równości!");
    }



    /**
     * Metoda zamienia równanie na postać ONP
     *
     * @param rownanie równanie do zamiany na postać ONP
     * @return równanie w postaci ONP
     */
    public String przeksztalcNaOnp(String rownanie) {
        //Usunmy spacje
        rownanie = rownanie.replaceAll("\\s", "");

        if (czyPoprawneRownanie(rownanie)) {
            String wynik = "";
            for (int i = 0; i < rownanie.length(); i++) {
                //Jezeli to cyfra
                if (rownanie.charAt(i) >= '0' && rownanie.charAt(i) <= '9') {
                    wynik += rownanie.charAt(i);
                    if (!(rownanie.charAt(i + 1) >= '0' && rownanie.charAt(i + 1) <= '9'))
                        wynik += " ";
                }
                //Jezeli to operator
                else
                    switch (rownanie.charAt(i)) {
                        case ('+'):
                        case ('-'): {
                            while (stack.getSize() > 0 && !stack.showValue(stack.getSize() - 1).equals("(")) {
                                wynik = wynik + stack.pop() + " ";
                            }
                            String str = "" + rownanie.charAt(i);
                            stack.push(str);
                            break;
                        }
                        case ('x'):
                        case ('*'):
                        case ('%'):
                        case ('/'): {
                            while (stack.getSize() > 0 && !stack.showValue(stack.getSize() - 1).equals("(")
                                    && !stack.showValue(stack.getSize() - 1).equals("+")
                                    && !stack.showValue(stack.getSize() - 1).equals("-")) {
                                wynik = wynik + stack.pop() + " ";
                            }
                            String str = "" + rownanie.charAt(i);
                            stack.push(str);
                            break;
                        }
                        case ('^'): {
                            while (stack.getSize() > 0 && stack.showValue(stack.getSize() - 1).equals("^")) {
                                wynik = wynik + stack.pop() + " ";
                            }
                            String str = "" + rownanie.charAt(i);
                            stack.push(str);
                            break;
                        }
                        case ('('): {
                            String str = "" + rownanie.charAt(i);
                            stack.push(str);
                            break;
                        }
                        case (')'): {
                            while (stack.getSize() > 0 && !stack.showValue(stack.getSize() - 1).equals("(")) {
                                wynik = wynik + stack.pop() + " ";
                            }
                            stack.pop(); // Usuwamy '('
                            break;
                        }
                        case ('='): {
                            while (stack.getSize() > 0) {
                                wynik = wynik + stack.pop() + " ";
                            }
                            wynik += "=";
                            break;
                        }
                        case ('!'):
                        case ('s'): {
                            wynik += rownanie.charAt(i) + " ";
                            break;
                        }
                        default: {
                            if (rownanie.charAt(i) != '=' && rownanie.charAt(i) != ' ')
                                throw new MyExceptions.InvalidOperator(rownanie.charAt(i) + " jest niedozwolonym operatorem!");
                        }
                    }
            }
            return wynik;
        } else
            throw new MyExceptions.InvalidEquation("Równanie nie jest prawidłowe");
    }


    private double factorial(double n) {
        if (n < 0 || n != Math.floor(n)) {
            throw new IllegalArgumentException("Silnia jest zdefiniowana tylko dla liczb całkowitych nieujemnych");
        }
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }



    public static void main(String[] args) {
        //Pierwiastkowanie zapisujemy w taki sposób że pierwiastek z 36 to 36s lub (36)s
        String infixEquation = " (22+3)/(2-34)*3+8)= ";
        ONP onp = new ONP();
        String rownanieOnp = onp.przeksztalcNaOnp(infixEquation);
        System.out.println(onp.obliczOnp(rownanieOnp));
    }
}