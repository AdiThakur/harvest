package common;

public class Event<T>
{
	private boolean consumed;
	private T payload;

	public Event(T payload)
	{
		this.payload = payload;
		this.consumed = false;
	}

	// lol
	public boolean isFreshPiece()
	{
		return !consumed;
	}

	public T peek()
	{
		return payload;
	}

	public T get()
	{
		if (!consumed) {
			consumed = true;
			return payload;
		}

		return null;
	}

	public void consume()
	{
		consumed = true;
	}
}
