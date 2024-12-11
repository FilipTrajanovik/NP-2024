package mk.ukim.finki.av3i4;

class InsuficentAmountException extends Exception {
    public InsuficentAmountException() {
        super("Insufficient amount");
    }
}
interface InterestBearingAccount{
    void addInterest();
}
 abstract class Account{

    public static int COUNT=1;

    public String name;
    public String number;
    public double balance;

    public Account(String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.number="2300000"+COUNT;
        COUNT++;
    }

    public void addFunds(double amount)
    {
        balance+=amount;
    }
    public void withdrawFunds(double amount) throws InsuficentAmountException {
        if(balance < amount)
        {
            throw new InsuficentAmountException();
        }else{
            balance-=amount;
        }
    }
    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", balance=" + balance +
                '}';
    }
}

class InterestCheckingAccount extends Account implements InterestBearingAccount{

    public static final double INTEREST=0.03;

    public InterestCheckingAccount(String name, double balance) {
        super(name, balance);
    }

    @Override
    public void addInterest() {
       addFunds(getBalance()*INTEREST);
    }

    @Override
    public String toString() {
        return "InterestChecking" + super.toString();
    }
}
class PlatinumCheckingAccount extends InterestCheckingAccount {

    public PlatinumCheckingAccount(String name, double balance) {
        super(name, balance);
    }

    @Override
    public void addInterest() {
        addFunds(getBalance()*(2*InterestCheckingAccount.INTEREST));
    }

    @Override
    public String toString() {
        return  "PlatinumChecking"+super.toString();
    }
}
class NonInterestCheckingAccount extends Account{
    public NonInterestCheckingAccount(String name, double balance) {
        super(name, balance);
    }

    @Override
    public String toString() {
        return "NonInterestCheckin"+super.toString();
    }
}

class Bank {
    public Account[] accounts=new Account[100];
    public String name;
    public int totalAssets;
    public int n;

    public Bank(String name) {
        this.name = name;
        this.totalAssets=0;
        n=0;

    }
    public void addAccount(Account account) {
        accounts[n++]=account;
    }
    public int getTotalAssets()
    {
        for (Account account : accounts) {
            totalAssets += (int) account.getBalance();
        }
        return totalAssets;
    }
    public String getName() {
        return name;
    }
    public void addInterest() {
        for (Account account : accounts) {
            if(account instanceof InterestBearingAccount) {
                InterestBearingAccount iba = (InterestBearingAccount) account;
                iba.addInterest();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<n;i++) {
           sb.append(accounts[i].toString()).append("\n");
        }
        return sb.toString();
    }
}
public class BankTest {
    public static void main(String[] args) {
        Bank bank=new Bank("Filip's Bank");
        Account a1=new InterestCheckingAccount("Filip", 10000);
        Account a2=new PlatinumCheckingAccount("Stefa", 1000000);
        Account a3=new NonInterestCheckingAccount("Petko", 10000);

        bank.addAccount(a1);
        bank.addAccount(a2);
        bank.addAccount(a3);
        System.out.println(bank);
        System.out.println("AFTER ADDING INTEREST");
        bank.addInterest();
        System.out.println(bank);
    }
}
