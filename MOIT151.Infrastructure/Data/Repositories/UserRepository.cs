using Microsoft.EntityFrameworkCore;
using MOIT151.Application;
using MOIT151.Core;

namespace MOIT151.Infrastructure.Data.Repositories;

public class UserRepository(MOIT151Context dbContext) : IUserRepository
{
    public async Task AddAsync(User user, CancellationToken ct = default)
    {
        await dbContext.Users.AddAsync(user, ct).ConfigureAwait(false);
    }

    public async Task<User?> GetByExternalIdAsync(string externalId, CancellationToken ct = default)
    {
        return await dbContext.Users.FirstOrDefaultAsync(x => x.ExternalId == externalId, ct).ConfigureAwait(false);
    }

    public async Task<User?> GetByUsernameAsync(string username, CancellationToken ct = default)
    {
        return await dbContext.Users.FirstOrDefaultAsync(x => x.Username == username, ct).ConfigureAwait(false);
    }
}