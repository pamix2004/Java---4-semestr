import SetExceptions.NieZnalezionoElementuException;
import SetExceptions.PrzepelnienieException;

public class Set<T extends Comparable<T>>
{
    T[] set;
    int pojemnosc = 10;
    int rozmiar = 0;

    public Set(int pojemnosc)
    {
        this.pojemnosc = pojemnosc;
        this.rozmiar = 0;
        this.set = (T[]) new Comparable[pojemnosc];
    }

    public void dodajElement(T element)
    {
        if (zawiera(element)) return;
        if(rozmiar >= pojemnosc)
        {
            try
            {
                throw new PrzepelnienieException("nie mozna dodac. Set jest pelny");
            }
            catch (PrzepelnienieException e)
            {
                throw new RuntimeException(e);
            }
        }

        /* znajdowanie nowego miejsca dla elementu */
        int i = 0;
        while (i < rozmiar && set[i].compareTo(element) < 0)
        {
            i++;
        }

        /* przesuniecie elementow w prawo aby zrobic miejsce */
        for (int j = rozmiar; j > i; j--)
        {
            set[j] = set[j - 1];
        }
        set[i] = element;
        rozmiar++;
    }

    public void usunElement(T element)
    {
        int index = szukaj(element, false);
        if (index == -1) return;

        /* przesuniecie elementow w lewo */
        for (int i = index; i < rozmiar - 1; i++)
        {
            set[i] = set[i + 1];
        }
        set[rozmiar - 1] = null;
        rozmiar--;
    }

    public Set<T> dodajElementy(Set<T> innySet)
    {
        Set<T> nowySet = new Set<>(rozmiar + innySet.rozmiar);
        for (int i = 0; i < rozmiar; i++)
        {
            nowySet.dodajElement(set[i]);
        }
        for (int i = 0; i < innySet.rozmiar; i++)
        {
            nowySet.dodajElement(innySet.set[i]);
        }
        return nowySet;
    }

    public Set<T> odejmijElementy(Set<T> innySet)
    {
        Set<T> nowySet = new Set<>(rozmiar);
        for (int i = 0; i < rozmiar; i++)
        {
            if (!innySet.zawiera(set[i]))
            {
                nowySet.dodajElement(set[i]);
            }
        }
        return nowySet;
    }

    public Set<T> przeciecie(Set<T> innySet)
    {
        Set<T> nowySet = new Set<>(Math.min(rozmiar, innySet.rozmiar));
        for (int i = 0; i < rozmiar; i++)
        {
            if (innySet.zawiera(set[i]))
            {
                nowySet.dodajElement(set[i]);
            }
        }
        return nowySet;
    }

    public Integer szukaj(T element, Boolean wyjatek)
    {
        /* set jest posortowany wiec mozna uzyc binary search w celu zwiekszenia efektywnosci dziala*/
        int lewo = 0;
        int prawo = rozmiar - 1;
        while (lewo <= prawo)
        {
            int srodek = (lewo + prawo) / 2;
            int cmp = set[srodek].compareTo(element);
            if (cmp == 0)
            {
                return srodek;
            }
            else if (cmp < 0)
            {
                lewo = srodek + 1;
            }
            else
            {
                prawo = srodek - 1;
            }
        }

        if (wyjatek)
        {
            try
            {
                throw new NieZnalezionoElementuException("Nie znaleziono elementu");
            }
            catch (NieZnalezionoElementuException e)
            {
                throw new RuntimeException(e);
            }
        }

        return -1;
    }

    public Boolean zawiera(T element)
    {
        return szukaj(element, false) != -1;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("rozmiar = ").append(rozmiar)
                .append(", pojemnosc = ").append(pojemnosc)
                .append(", elementy = [");
        for (int i = 0; i < rozmiar; i++)
        {
            sb.append(set[i]);
            if (i < rozmiar - 1)
            {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
