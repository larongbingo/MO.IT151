using MOIT151.Application;

namespace MOIT151.Web.Modules;

public class FileCleanupBackgroundService(IServiceScopeFactory serviceScopeFactory) : BackgroundService
{
    private readonly PeriodicTimer timer = new(TimeSpan.FromHours(1));
    
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        while (await timer.WaitForNextTickAsync(stoppingToken) && !stoppingToken.IsCancellationRequested)
        {
            using var scope = serviceScopeFactory.CreateScope();
            
            var fileRepository = scope.ServiceProvider.GetRequiredService<IFileRepository>();
            var unitOfWork = scope.ServiceProvider.GetRequiredService<IUnitOfWork>();
            var logger = scope.ServiceProvider.GetRequiredService<ILogger<FileCleanupBackgroundService>>();

            await fileRepository.DeleteInvalidFilesAsync(stoppingToken);
            
            var rows = await unitOfWork.SaveChangesAsync(stoppingToken);
            
            logger.LogInformation("Deleted {rows} invalid files", rows);
        }
    }
}