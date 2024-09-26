import { v4 as uuidv4 } from 'uuid';
import { saveAs } from 'file-saver';
import { killAllChildren } from './utils';
import { Task } from './task';
import { renderButton, renderList } from './rendering';


let render = () => {};

const tasks: Task[] = [{id: '0', isDone: false, text: 'abc', inEdit: false},{id: '2', isDone: true, text: 'cbd', inEdit: false},{id: '3', isDone: false, text: 'triple e', inEdit: false}];

const addTask = () => {
    tasks.unshift({id: uuidv4(), isDone: false, text: '', inEdit: true})
    render();
}

const toggleTask = (id: string) => {
    const task = tasks.find(x => x.id == id);
    if (!task) return;
    task.isDone = !task.isDone;
    render();
}

const editTask = (id: string) => {
    const task = tasks.find(x => x.id == id);
    if (!task) return;
    task.inEdit = !task.inEdit;
    render();
}

const deleteTask = (id: string) => {
    const task = tasks.find(x => x.id == id);
    if (!task) return;
    const index = tasks.indexOf(task);
    if(index > -1) tasks.splice(index, 1)
    render();
}

const textChange = (id: string, text: string) => {
    const task = tasks.find(x => x.id == id);
    if (!task) return;
    task.text = text;
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
        tasks.splice(0, tasks.length, ...data);
        render();
    }
    fileInput.click();
}

var controls = document.getElementById("controls")!;
controls.appendChild(renderButton("Add", addTask));
controls.appendChild(renderButton("Import", importTasks));
controls.appendChild(renderButton("Export", exportTasks));

render = () => renderList(tasks, toggleTask, textChange, editTask, deleteTask);

render();