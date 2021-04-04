import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CircularLockedBuffer implements Buffer {
  private int capacity;
  private int[] buffer;
  private int elementsInBuffer = 0;
  ReentrantLock mutex = new ReentrantLock();
	Condition canGet = mutex.newCondition();
	Condition canSet = mutex.newCondition();

  public CircularLockedBuffer(int capacity) {
    this.buffer = new int[capacity];
    this.capacity = capacity;
  }
	// place value into buffer
	public  void set(int value) {
		try {
			mutex.lock();
			while(elementsInBuffer >= capacity) {
				canGet.signalAll();
				canSet.await();
			}
			System.out.printf("Producer writes\t%2d", value);
			int positionToAdd = elementsInBuffer;
			buffer[positionToAdd] = value;
			elementsInBuffer++;
			canGet.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			mutex.unlock();
		}
	} // end method set 

	// return value from buffer
	public int get() {
		int valueToReturn = -1;
		int positionToGetValue = -1;
		try {
			mutex.lock();
			while (elementsInBuffer == 0) {
				canSet.signalAll();
				canGet.await();
			}
			positionToGetValue = elementsInBuffer -1;
			valueToReturn =  buffer[positionToGetValue];
			clearBufferInLocation(positionToGetValue);
			System.out.printf("Consumer reads\t%2d", valueToReturn);
			canSet.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			mutex.unlock();
		}
		return valueToReturn;
	} // end method get

	private void clearBufferInLocation(int location) {
		buffer[location] = -1;
		elementsInBuffer = elementsInBuffer - 1;
	}
}
