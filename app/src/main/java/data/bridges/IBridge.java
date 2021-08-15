package data.bridges;

import java.util.List;

public interface IBridge<M>
{
	public M insert(M model);

	public M getById(long modelId);

	public List<M> getAll();

	public int delete(M model);
}
