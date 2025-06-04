using System.Security.Claims;
using MOIT151.Application;
using MOIT151.Core;

namespace MOIT151.Web.Authorization;

public class ExternalIdentityService(IHttpContextAccessor httpContextAccessor, IUserRepository userRepository) 
    : IExternalIdentityService
{
    public string? GetExternalUserId()
        => httpContextAccessor.HttpContext?.User.GetExternalId();

    public async Task<User?> GetUserAsync(CancellationToken ct = default)
    {
        var externalUserId = GetExternalUserId();
        
        if (externalUserId is null)
            return null;
        
        return await userRepository.GetByExternalIdAsync(externalUserId, ct).ConfigureAwait(false);
    }
}

public interface IExternalIdentityService
{
    public string? GetExternalUserId();
    public Task<User?> GetUserAsync(CancellationToken ct = default);
}

internal static class ClaimsPrincipalExtensions
{
    public static string? GetExternalId(this ClaimsPrincipal principal)
        => principal.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
}