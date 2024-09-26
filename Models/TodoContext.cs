using Microsoft.EntityFrameworkCore;

namespace TodoApp.Models;

public class TodoContext : DbContext
{
    public TodoContext(DbContextOptions options) : base(options) {}
    
    public DbSet<TodoItem> TodoItems { get; set; }
}