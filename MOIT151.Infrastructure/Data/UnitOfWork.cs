using MOIT151.Application;

namespace MOIT151.Infrastructure.Data;

public class UnitOfWork (MOIT151Context dbContext): IUnitOfWork
{
    public async Task<int> SaveChangesAsync(CancellationToken ct = default)
    {
        return await dbContext.SaveChangesAsync(ct);
    }
}