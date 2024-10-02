import {tasks, addTask, deleteTask, editTask, loadTasks, reloadTasks, updateBackendUrl} from './data-provider'
import { renderButton, renderList } from './rendering';
import { saveAs } from 'file-saver';

let render = () => {};

const add = async () => {
    await addTask({isDone: false, text: ''})
    render();
}

const toggle = async (id: string) => {
    const task = tasks.find(x => x.id == id);
    if (!task) return;
    await editTask(id, {isDone: !task.isDone})
    render();
}

const edit = async (id: string) => {
    const task = tasks.find(x => x.id == id);
    if (!task) return;
    const newText = prompt("Enter new text", task.text ?? '');
    if(newText)
        await editTask(id, {text: newText});
    render();
}

const remove = async (id: string) => {
    const task = tasks.find(x => x.id == id);
    if (!task) return;
    await deleteTask(id);
    render();
}

const exportTasks = () => {
    var blob = new Blob([JSON.stringify(tasks, undefined, 2)], {type: "application/json;charset=utf-8"});
    saveAs(blob, "data.json")
}

const importTasks = async () => {
    var fileInput = document.getElementById("upload")! as HTMLInputElement;
    fileInput.onchange = async function onchange(e) {
        if(!fileInput.files) return;
        var data = JSON.parse(await fileInput.files[0].text())
        if(!Array.isArray(data)) return;
        if(!data.every(v => 'id' in v && 'isDone' in v && 'text' in v && 'inEdit' in v)) return;
        await loadTasks(data);
        render();
    }
    fileInput.click();
}

var controls = document.getElementById("controls")!;
controls.appendChild(renderButton("Add", add));
controls.appendChild(renderButton("Import", importTasks));
controls.appendChild(renderButton("Export", exportTasks));
controls.appendChild(renderButton("Change backend URI", updateBackendUrl));

render = () => renderList(tasks, toggle, edit, remove);
await reloadTasks();
render();