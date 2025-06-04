using MOIT151.Application;
using File = MOIT151.Core.File;

namespace MOIT151.Infrastructure.Data.Repositories;

public class FileRepository(MOIT151Context dbContext) : IFileRepository
{
    public async Task AddAsync(File file, CancellationToken ct)
    {
        await dbContext.Files.AddAsync(file, ct);
    }
}