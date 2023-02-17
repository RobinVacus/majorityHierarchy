package exactComputations;

public class Box<T> {

	private boolean empty;
	private T content;
	
	public Box() {
		empty = true;
	}
	
	public Box(T content) {
		put(content);
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
	public T get() {
		if (empty) {
			throw new RuntimeException("Trying to access empty box");
		}
		return content;
	}
	
	public void clear() {
		empty = true;
	}
	
	public void put(T content) {
		this.content = content;
		empty = false;
	}
	
}
