import { TodoItemService, TodoItem, TodoItemModel, TodoItemPatchModel, OpenAPI } from './api/index'

const backendUrl = 'http://localhost:5166';
OpenAPI.BASE = backendUrl;

const updateBackendUrl = () => {
    const url = prompt("Input backend url", OpenAPI.BASE);
    if(url) OpenAPI.BASE = url;
}

let tasks: TodoItem[] = [];

const reloadTasks = async () => {
    tasks = await TodoItemService.getTasks()
}

const addTask = async (task: TodoItemModel) => {
    await TodoItemService.addTask(task);
    await reloadTasks();
}

const editTask = async (id: string, data: TodoItemPatchModel) => {
    await TodoItemService.editTask(id, data);
    await reloadTasks();
}

const deleteTask = async (id: string) => {
    await TodoItemService.deleteTask(id);
    await reloadTasks();
}

const loadTasks = async (data: TodoItem[]) => {
    await TodoItemService.loadTasks(data);
    await reloadTasks();
}

export { updateBackendUrl, tasks, reloadTasks, addTask, editTask, deleteTask, loadTasks }