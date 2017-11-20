package tick3;

class BankAccount {
	
	private int balance;
	private int account;

	public synchronized void transferTo(BankAccount b, int amount) {
	
		int accountThis = this.account;
		int accountB = b.getAccount();
		
		//If order of locking is not implemented as below, if 2 accounts transfer to each other simultanously, both could execute
		//the first if(){code} here. So A locks B, then wants lock on itself. Meanwhile, B locks A and wants lock on itself. So
		//A and B both have a lock on the other, and both wish to lock themselves. Deadlock.
		
		//With order implemented as below. A locks B, then wants lock on itself. Meanwhile, B tries to lock itself but is blocked. 
		//Execution of account A's transfer completes as it is free to get a lock on itself, and unlock both to let B proceed.
		if(accountB <= accountThis){
			synchronized(b){
				synchronized(this){
					this.balance -= amount;
					b.balance += amount;
				}
			}
		}
		else{
			synchronized(this){
				synchronized(b){
					this.balance -= amount;
					b.balance += amount;
				}
			}
		}
	}
	
	
	BankAccount(int bal, int acc){
		this.balance = bal;
		this.account = acc;
	}
	
	
	public int getAccount(){
		return this.account;
	}
}
