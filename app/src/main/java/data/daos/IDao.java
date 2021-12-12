package data.daos;

import java.util.List;

import data.models.BaseEntity;

public interface IDao<T extends BaseEntity>
{
	long insert(T entity);

	T get(long uid);

	List<T> getAll();

	int update(T entity);

	int delete(T entity);
}
