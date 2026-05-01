import { useState, useEffect, useCallback } from 'react';
import { api, type ApiSchema } from '../api/client';

export type Filter = 'all' | 'active' | 'completed';

type TodoResponse = ApiSchema<'TodoResponse'>;

function extractErrorMessage(error: unknown): string {
  const validationProblem = error as ApiSchema<'ValidationProblem'>;
  if (validationProblem.errors?.length) {
    return validationProblem.errors.map((e) => e.detail).join(', ');
  }
  const problem = error as ApiSchema<'Problem'>;
  return problem.detail ?? problem.title ?? 'Something went wrong';
}

export function useTodos() {
  const [todos, setTodos] = useState<TodoResponse[]>([]);
  const [filter, setFilterState] = useState<Filter>('all');
  const [listLoading, setListLoading] = useState(false);
  const [listError, setListError] = useState<string | null>(null);
  const [createLoading, setCreateLoading] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);
  const [itemErrors, setItemErrors] = useState<Record<number, string>>({});

  const fetchTodos = useCallback(async (currentFilter: Filter, silent = false) => {
    if (!silent) setListLoading(true);
    setListError(null);
    try {
      const path =
        currentFilter === 'all' ? '/todos' : `/todos?completed=${currentFilter === 'completed'}`;
      const data = await api.get<TodoResponse[]>(path);
      setTodos(data.slice().sort((a, b) => a.id - b.id));
    } catch (error) {
      setListError(extractErrorMessage(error));
    } finally {
      if (!silent) setListLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTodos(filter);
  }, [filter, fetchTodos]);

  function setFilter(newFilter: Filter) {
    setFilterState(newFilter);
  }

  async function createTodo(title: string): Promise<void> {
    setCreateError(null);
    setCreateLoading(true);
    try {
      await api.post<TodoResponse>('/todos', { title });
      await fetchTodos(filter, true);
    } catch (error) {
      setCreateError(extractErrorMessage(error));
      throw error;
    } finally {
      setCreateLoading(false);
    }
  }

  async function toggleCompleted(id: number, currentCompleted: boolean): Promise<void> {
    setTodos((previous) =>
      previous.map((todo) => (todo.id === id ? { ...todo, completed: !currentCompleted } : todo))
    );
    setItemErrors((previous) => ({ ...previous, [id]: '' }));
    try {
      await api.patch<TodoResponse>(`/todos/${id}`, { completed: !currentCompleted });
    } catch (error) {
      setTodos((previous) =>
        previous.map((todo) => (todo.id === id ? { ...todo, completed: currentCompleted } : todo))
      );
      setItemErrors((previous) => ({ ...previous, [id]: extractErrorMessage(error) }));
    }
  }

  async function deleteTodo(id: number): Promise<void> {
    const deleted = todos.find((todo) => todo.id === id);
    setTodos((previous) => previous.filter((todo) => todo.id !== id));
    setItemErrors((previous) => ({ ...previous, [id]: '' }));
    try {
      await api.delete(`/todos/${id}`);
    } catch (error) {
      if (deleted) {
        setTodos((previous) => [...previous, deleted].sort((a, b) => a.id - b.id));
      }
      setItemErrors((previous) => ({ ...previous, [id]: extractErrorMessage(error) }));
    }
  }

  return {
    todos,
    filter,
    setFilter,
    list: { loading: listLoading, error: listError },
    create: { loading: createLoading, error: createError, run: createTodo },
    item: { errors: itemErrors, toggle: toggleCompleted, delete: deleteTodo },
  };
}
