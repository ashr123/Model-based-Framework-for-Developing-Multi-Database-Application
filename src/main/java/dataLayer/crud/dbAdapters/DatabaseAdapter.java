package dataLayer.crud.dbAdapters;

import dataLayer.configReader.Entity;
import dataLayer.crud.filters.*;

import java.util.Set;

/**
 * Element
 */
public interface DatabaseAdapter
{
	void revealQuery(VoidFilter voidFilter);

	Set<Entity> revealQuery(Filter filter);

	void executeCreate(CreateSingle createSingle);

	void executeCreate(CreateMany createMany);

	Set<Entity> execute(Eq eq);

	Set<Entity> execute(Ne ne);

	Set<Entity> execute(Gt gt);

	Set<Entity> execute(Lt lt);

	Set<Entity> execute(Gte gte);

	Set<Entity> execute(Lte lte);

	Set<Entity> execute(And and);

	Set<Entity> execute(Or or);

	Set<Entity> execute(All all);
}
