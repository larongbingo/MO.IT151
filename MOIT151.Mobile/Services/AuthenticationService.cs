using Auth0.OidcClient;

namespace MOIT151.Mobile.Services;

public class AuthenticationService(Auth0Client authClient) : IAuthenticationService
{
    public async Task<bool> AuthenticateAsync(CancellationToken ct = default)
    {
        var result = await authClient.LoginAsync(null, ct);
        if (result.IsError)
        {
            return false;
        }
        
        AccessToken = result.AccessToken;
        return true;
    }

    public string AccessToken { get; private set; }
}

public interface IAuthenticationService
{
    public Task<bool> AuthenticateAsync(CancellationToken ct = default);
    public string AccessToken { get; }
}