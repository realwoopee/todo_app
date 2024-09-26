namespace TodoApp.Models;

public class TodoItem
{
    public Guid Id { get; set; }
    
    public bool IsDone { get; set; }
    
    public string Text { get; set; }
}