using Microsoft.EntityFrameworkCore;
using MOIT151.Application;
using File = MOIT151.Core.File;

namespace MOIT151.Infrastructure.Data.Repositories;

public class FileRepository(MOIT151Context dbContext) : IFileRepository
{
    public async Task AddAsync(File file, CancellationToken ct)
    {
        await dbContext.Files.AddAsync(file, ct);
    }

    public async Task<File?> GetByUserIdAndFileIdAsync(Guid userId, Guid fileId, CancellationToken ct = default)
    {
        return await dbContext.Files.FirstOrDefaultAsync(x => x.UserId == userId && x.Id == fileId, ct);
    }

    public async Task<List<File>> GetListByUserIdAsync(Guid userId, CancellationToken ct = default)
    {
        return await dbContext.Files.Where(x => x.UserId == userId).ToListAsync(ct);
    }

    public Task UpdateAsync(File file, CancellationToken ct = default)
    {
        dbContext.Files.Update(file);
        return Task.CompletedTask;
    }
}