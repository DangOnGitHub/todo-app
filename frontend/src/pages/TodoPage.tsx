import { useState } from 'react';
import { useTodos, type Filter } from '../store/todos';

interface Props {
  onLogout: () => void;
}

export default function TodoPage({ onLogout }: Props) {
  const { todos, filter, setFilter, list, create, item } = useTodos();

  const [titleInput, setTitleInput] = useState('');

  async function handleCreate(event: React.SubmitEvent) {
    event.preventDefault();
    try {
      await create.run(titleInput);
      setTitleInput('');
    } catch {
      // create.error is set by the hook; leave input populated so user can correct
    }
  }

  const filterLabels: { value: Filter; label: string }[] = [
    { value: 'all', label: 'All' },
    { value: 'active', label: 'Active' },
    { value: 'completed', label: 'Completed' },
  ];

  return (
    <div id="todo-page">
      <div className="todo-header">
        <h1>My Todos</h1>
        <button type="button" className="todo-logout-btn" onClick={onLogout}>
          Log out
        </button>
      </div>

      <form className="todo-create-form" onSubmit={handleCreate}>
        <input
          type="text"
          placeholder="New todo…"
          value={titleInput}
          onChange={(e) => setTitleInput(e.target.value)}
          autoComplete="off"
        />
        <button type="submit" disabled={create.loading}>
          Add
        </button>
        <p role="alert" className="todo-create-error">
          {create.error ?? ''}
        </p>
      </form>

      <div className="todo-filters">
        {filterLabels.map(({ value, label }) => (
          <button
            key={value}
            type="button"
            className={`todo-filter-btn${filter === value ? ' todo-filter-btn--active' : ''}`}
            onClick={() => setFilter(value)}
          >
            {label}
          </button>
        ))}
      </div>

      {list.error && (
        <p role="alert" className="todo-list-error">
          {list.error}
        </p>
      )}

      {todos.length === 0 ? (
        <p className="todo-empty" aria-live="polite">
          {list.loading ? 'Loading…' : 'No todos yet.'}
        </p>
      ) : (
        <ul className="todo-list">
          {todos.map((todo) => (
            <li key={todo.id} className="todo-item">
              <input
                type="checkbox"
                checked={todo.completed}
                onChange={() => item.toggle(todo.id, todo.completed)}
                aria-label={`Mark "${todo.title}" as ${todo.completed ? 'active' : 'completed'}`}
              />
              <span className={todo.completed ? 'todo-title--completed' : ''}>{todo.title}</span>
              <button
                type="button"
                className="todo-delete-btn"
                onClick={() => item.delete(todo.id)}
              >
                Delete
              </button>
              {item.errors[todo.id] && (
                <p role="alert" className="todo-item-error">
                  {item.errors[todo.id]}
                </p>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
