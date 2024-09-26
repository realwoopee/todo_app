using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using TodoApp.Models;

namespace TodoApp.Features;

[ApiController]
[Route("tasks")]
public class TodoItemController(TodoContext db)
{
    [HttpGet]
    public async Task<List<TodoItem>> GetTasks() => await db.TodoItems.ToListAsync();

    [HttpGet("{id}")]
    public async Task<ActionResult<TodoItem>> GetTask(Guid id)
    {
        var task = await db.TodoItems.FirstOrDefaultAsync(x => x.Id == id);
        if (task is null) return new NotFoundResult();
        return task;
    }

    public class TodoItemModel
    {
        public bool IsDone { get; set; }
        
        public string Text { get; set; }
    }
    
    [HttpPost]
    public async Task<TodoItem> CreateTask(TodoItemModel model)
    {
        var task = new TodoItem() { IsDone = model.IsDone, Text = model.Text }; 
        db.TodoItems.Add(task);
        await db.SaveChangesAsync();
        return task;
    }
    
    public class TodoItemPatchModel
    {
        public bool? IsDone { get; set; }
        
        public string? Text { get; set; }
    }

    [HttpPatch("{id}")]
    public async Task<ActionResult<TodoItem>> EditTask(Guid id, TodoItemPatchModel model)
    {
        var task = await db.TodoItems.FirstOrDefaultAsync(x => x.Id == id);
        
        if (task is null) return new NotFoundResult();

        task.IsDone = model.IsDone ?? task.IsDone;
        task.Text = model.Text ?? task.Text;

        await db.SaveChangesAsync();
        return task;
    }

    [HttpDelete("{id}")]
    public async Task<ActionResult> DeleteTask(Guid id)
    {
        var task = await db.TodoItems.FirstOrDefaultAsync(x => x.Id == id);
        
        if (task is null) return new NotFoundResult();

        db.TodoItems.Remove(task);
        await db.SaveChangesAsync();

        return new OkResult();
    }

    [HttpPut]
    public async Task<List<TodoItem>> LoadTasks(List<TodoItem> data)
    {
        db.TodoItems.RemoveRange(db.TodoItems);
        await db.SaveChangesAsync();
        
        db.TodoItems.AddRange(data);
        await db.SaveChangesAsync();

        return data;
    }
}