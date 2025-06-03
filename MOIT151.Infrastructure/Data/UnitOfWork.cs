using MOIT151.Application;

namespace MOIT151.Infrastructure.Data;

public class UnitOfWork (MOIT151Context dbContext): IUnitOfWork
{
    public async Task SaveChangesAsync(CancellationToken ct = default)
    {
        await dbContext.SaveChangesAsync(ct);
    }
}