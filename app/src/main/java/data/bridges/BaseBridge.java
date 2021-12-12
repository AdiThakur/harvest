package data.bridges;

import java.util.List;

import data.daos.IDao;
import data.models.BaseEntity;

public class BaseBridge<T extends BaseEntity>
{
	private final IDao<T> dao;

	public BaseBridge(IDao<T> dao)
	{
		this.dao = dao;
	}

	public T insert(T entity)
	{
		entity.uid = dao.insert(entity);
		return entity;
	}

	public T get(long uid)
	{
		return dao.get(uid);
	}

	public List<T> getAll()
	{
		return dao.getAll();
	}

	public int update(T entity)
	{
		if (entity.uid == 0) { return 0; }
		return dao.update(entity);
	}

	public int delete(T entity)
	{
		return dao.delete(entity);
	}
}
