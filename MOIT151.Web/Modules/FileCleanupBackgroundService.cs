using Microsoft.EntityFrameworkCore;
using MOIT151.Application;
using MOIT151.Infrastructure.Data;

namespace MOIT151.Web.Modules;

public class FileCleanupBackgroundService(IServiceScopeFactory serviceScopeFactory) : BackgroundService
{
    private readonly PeriodicTimer timer = new(TimeSpan.FromMinutes(1));
    
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        while (await timer.WaitForNextTickAsync(stoppingToken) && !stoppingToken.IsCancellationRequested)
        {
            using var scope = serviceScopeFactory.CreateScope();
            
            var dbContext = scope.ServiceProvider.GetRequiredService<MOIT151Context>();
            var logger = scope.ServiceProvider.GetRequiredService<ILogger<FileCleanupBackgroundService>>();

            var rows = 
                await dbContext.Files
                    .Where(x => !x.IsExists && DateTime.UtcNow - x.CreatedAt > TimeSpan.FromHours(1))
                    .ExecuteDeleteAsync(stoppingToken);
            
            logger.LogInformation("Deleted {rows} invalid files", rows);
        }
    }
}