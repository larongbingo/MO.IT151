using Microsoft.AspNetCore.Authorization;

namespace MOIT151.Web.Authorization;

public static class AuthorizationModule
{
    public static IServiceCollection AddMOIT151Authorization(this IServiceCollection services)
    {
        services.AddAuthorization(options =>
        {
            options.AddPolicy(nameof(MOIT151AuthorizationRequirement), 
                policy => policy.Requirements.Add(new MOIT151AuthorizationRequirement()));
            options.AddPolicy(nameof(NoopAuthorizationRequirement), 
                policy => policy.Requirements.Add(new NoopAuthorizationRequirement()));
            options.DefaultPolicy = options.GetPolicy(nameof(MOIT151AuthorizationRequirement));
        });
        services.AddHttpContextAccessor();
        services.AddScoped<IExternalIdentityService, ExternalIdentityService>();
        services.AddScoped<IAuthorizationHandler, MOIT151AuthorizationHandler>();
        services.AddScoped<IAuthorizationHandler, NoopAuthorizationHandler>();
        return services;
    }
}