import { Task } from "./task";
import { killAllChildren } from "./utils";

const renderButton = (text: string, action: () => void) => {
    const elem = document.createElement("a");
    elem.onclick = function onclick(this, e) {
        e.preventDefault();
        action();
    }
    elem.href = "#";
    elem.innerText = text;
    return elem;
}

const renderTask = (data: Task, onToggle: (id: string) => void, onChange: (id: string, text: string) => void, onEdit: (id: string) => void, onDelete: (id: string) => void) => {
    const taskSpan = document.createElement("span");
    taskSpan.classList.add("task");

    const taskCheckbox = document.createElement("input");
    taskCheckbox.type = "checkbox";
    taskCheckbox.checked = data.isDone;
    taskCheckbox.onclick = function toggleHandler(this: GlobalEventHandlers, ev: MouseEvent) {
        ev.preventDefault();
        onToggle(data.id);
    }
    taskSpan.appendChild(taskCheckbox);

    if(!data.inEdit) {
        const taskText = document.createElement("span");
        taskText.innerText = data.text;
        taskSpan.appendChild(taskText);
    }
    else {
        const textInput = document.createElement("input");
        textInput.type = "text";
        textInput.value = data.text;
        textInput.onchange = function onchange(this, e) {
            onChange(data.id, (e.target as HTMLInputElement).value)
        }

        taskSpan.appendChild(textInput);
    }

    
    const btnSpan = document.createElement("span");
    btnSpan.classList.add("buttons");

    btnSpan.appendChild(renderButton(data.inEdit ? 'Save' : 'Edit', () => onEdit(data.id)));
    btnSpan.appendChild(renderButton('Delete', () => onDelete(data.id)));

    taskSpan.appendChild(btnSpan);

    return taskSpan;
}

const renderList = (data: Task[], onToggle: (id: string) => void, onChange: (id: string, text: string) => void, onEdit: (id: string) => void, onDelete: (id: string) => void) => {
    const listEl = document.getElementById("list");
    if(!listEl) return;
    killAllChildren(listEl)
    data.map(t => renderTask(t, onToggle, onChange, onEdit, onDelete)).forEach((taskSpan) => listEl.appendChild(taskSpan));
};

export { renderList, renderButton };