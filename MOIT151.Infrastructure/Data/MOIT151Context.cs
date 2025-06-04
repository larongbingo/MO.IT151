using Microsoft.EntityFrameworkCore;
using MOIT151.Core;
using File = MOIT151.Core.File;

namespace MOIT151.Infrastructure.Data;

public class MOIT151Context : DbContext
{
    public DbSet<User> Users { get; set; }
    public DbSet<File> Files { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        base.OnConfiguring(optionsBuilder);

        optionsBuilder.UseNpgsql(Environment.GetEnvironmentVariable("MOIT151_POSTGRES_CONNECTION_STRING"));
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.ApplyConfigurationsFromAssembly(typeof(MOIT151Context).Assembly);
    }
}