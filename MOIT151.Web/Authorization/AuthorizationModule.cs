namespace MOIT151.Web.Authorization;

public static class AuthorizationModule
{
    public static IServiceCollection AddMOIT151Authorization(this IServiceCollection services)
    {
        services.AddAuthorization();
        services.AddHttpContextAccessor();
        services.AddTransient<IExternalIdentityService, ExternalIdentityService>();
        return services;
    }
}