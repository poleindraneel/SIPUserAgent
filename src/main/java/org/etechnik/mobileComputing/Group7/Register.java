package org.etechnik.mobileComputing.Group7;

public class Register implements Runnable {

	public static Register instance = null;
	
	public Register(){
		instance = this;
	}
	
	public static Register getInstance(){
		return instance;
	}
	
	public Thread t = null;
	private boolean keepRunning = true;
	
	public void startRegister(){
		t = new Thread(this);
		t.start();
		
	}
		
	@Override
	public void run() {
			
		while(keepRunning){
			try{
				UAClient.getInstance().sendREGISTER(60000);
				Thread.sleep(500000);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
				keepRunning = false;
			}
		
		}
	}
	public void deRegister(){
		this.t.interrupt();
		UAClient.getInstance().sendREGISTER(0);
		instance = null;
		
	}

}
