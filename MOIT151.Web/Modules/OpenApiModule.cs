using Microsoft.OpenApi.Models;
using NSwag;
using NSwag.Generation.Processors.Security;

namespace MOIT151.Web.Modules;

public static class OpenApiModule
{
    public static IServiceCollection AddMOIT151OpenApi(this IServiceCollection services)
    {
        services.AddOpenApiDocument(options =>
        {
            options.DocumentName = "FinMarkPrototype";
            options.Title = "FinMarkPrototype";
            options.Description = "This is a prototype API for FinMark. FinMark is a capstone project for MOIT151.";
            options.Version = "v1";
            options.OperationProcessors.Add(new OperationSecurityScopeProcessor("Bearer"));
            options.AddSecurity("Bearer", new NSwag.OpenApiSecurityScheme
                {
                    Description = "JWT Token from Auth0",
                    Name = "Authorization",
                    In = OpenApiSecurityApiKeyLocation.Header,

                    Type = OpenApiSecuritySchemeType.OAuth2,
                    Flows = new NSwag.OpenApiOAuthFlows()
                    {
                        Implicit = new NSwag.OpenApiOAuthFlow
                        {
                            Scopes = new Dictionary<string, string>
                            {
                                { "openid", "Open Id" }
                            },

                            AuthorizationUrl = "https://ewan.au.auth0.com/authorize?audience=https://ewan/api"
                        }
                    }
                }
            );
        });

        services.AddOpenApi();
        return services;
    }

}