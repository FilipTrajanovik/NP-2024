package mk.ukim.finki.av1;

class CombinationLock {
    private int combination;
    private boolean locked;

    public CombinationLock(int combination) {
        String comb=String.valueOf(combination);
        if(comb.length()<3)
        {
            throw new IllegalArgumentException("Combination length must be at least 3 characters");
        }
        this.combination = combination;
    }

    public boolean isOpen(int combination) {
        if (this.combination == combination) {
            locked = !locked;
            System.out.println("Combination: " + combination + ". Lock is open");
        }

        return this.combination == combination;
    }

    public boolean changeCombo(int oldCombination, int newCombination) {
        if (oldCombination == this.combination) {
            this.combination = newCombination;
            System.out.println("Combination changed to:" + newCombination);
            return true;
        }else{
            System.out.println("Old combination is not same as the previous combination" );
            return false;
        }

    }
}


public class CombinationLockTest {
    public static void main(String[] args) {
        CombinationLock combinationLock = new CombinationLock(100);
        System.out.println("TRYING COMBINATION: 101");
        combinationLock.isOpen(101);
        System.out.println("TRYING COMBINATION: 100");
        combinationLock.isOpen(100);
        System.out.println("CHANGING COMBINATION TO 500");
        combinationLock.changeCombo(100, 500);
    }
}