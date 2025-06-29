using MOIT151.Application;
using NewRelic.Api.Agent;

namespace MOIT151.Infrastructure.Data;

public class UnitOfWork (MOIT151Context dbContext): IUnitOfWork
{
    [Trace]
    public async Task<int> SaveChangesAsync(CancellationToken ct = default)
    {
        return await dbContext.SaveChangesAsync(ct);
    }
}