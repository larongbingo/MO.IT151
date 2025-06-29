using Microsoft.EntityFrameworkCore;
using MOIT151.Application;
using NewRelic.Api.Agent;
using File = MOIT151.Core.File;

namespace MOIT151.Infrastructure.Data.Repositories;

public class FileRepository(MOIT151Context dbContext) : IFileRepository
{
    [Trace]
    public async Task AddAsync(File file, CancellationToken ct)
    {
        await dbContext.Files.AddAsync(file, ct);
    }

    [Trace]
    public async Task<File?> GetByUserIdAndFileIdAsync(Guid userId, Guid fileId, CancellationToken ct = default)
    {
        return await dbContext.Files.FirstOrDefaultAsync(x => x.UserId == userId && x.Id == fileId, ct);
    }

    [Trace]
    public async Task<List<File>> GetListByUserIdAsync(Guid userId, CancellationToken ct = default)
    {
        return await dbContext.Files.Where(x => x.UserId == userId).ToListAsync(ct);
    }
    
    [Trace]
    public Task UpdateAsync(File file, CancellationToken ct = default)
    {
        dbContext.Files.Update(file);
        return Task.CompletedTask;
    }
}