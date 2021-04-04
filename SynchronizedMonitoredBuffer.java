public class SynchronizedMonitoredBuffer implements Buffer {
  private int buffer = -1; // shared by producer and consumer threads
	// place value into buffer
	public synchronized void set(int value) {
		try {
			while(buffer != -1) {
				wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.printf("Producer writes\t%2d", value);
		buffer = value;
		notifyAll();
	} // end method set 

	// return value from buffer
	public synchronized int get() {
		int valueToReturn = -1;
		try {
			while (buffer == -1) {
					wait();	
			}
			System.out.printf("Consumer reads\t%2d", buffer);
			valueToReturn =  buffer;
			buffer = -1;
			notifyAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return valueToReturn;
	} // end method get
}
