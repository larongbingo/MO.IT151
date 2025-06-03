using System.Security.Claims;

namespace MOIT151.Web.Authorization;

public class ExternalIdentityService(IHttpContextAccessor httpContextAccessor) : IExternalIdentityService
{
    public string? GetExternalUserId()
        => httpContextAccessor.HttpContext?.User.GetExternalId();
}

public interface IExternalIdentityService
{
    string? GetExternalUserId();
}

internal static class ClaimsPrincipalExtensions
{
    public static string? GetExternalId(this ClaimsPrincipal principal)
        => principal.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
}