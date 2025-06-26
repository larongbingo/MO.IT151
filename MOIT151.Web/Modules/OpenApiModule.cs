using Microsoft.AspNetCore.Authorization;
using Microsoft.OpenApi.Models;

using Scalar.AspNetCore;

namespace MOIT151.Web.Modules;

public static class OpenApiModule
{
    public static IServiceCollection AddMOIT151OpenApi(this IServiceCollection services)
    {
        services.AddOpenApi(options =>
        {
            options.AddDocumentTransformer(async (doc, ctx, ct) =>
            {
                doc.Info.Title = "FinMarkPrototype";
                doc.Info.Version = "v1";
                doc.Info.Description = "This is a prototype API for FinMark. FinMark is a capstone project for MOIT151.";

                doc.Components ??= new OpenApiComponents();
                doc.Components.SecuritySchemes = new Dictionary<string, OpenApiSecurityScheme>()
                {
                    ["Bearer"] = new()
                    {
                        Type = SecuritySchemeType.OAuth2,
                        In = ParameterLocation.Header,
                        Name = "Authorization",
                        Description = "JWT Token from Auth0",
                        Flows = new OpenApiOAuthFlows()
                        {
                            Implicit = new OpenApiOAuthFlow()
                            {
                                AuthorizationUrl = new Uri("https://ewan.au.auth0.com/authorize?audience=https://ewan/api"),
                                Scopes = new Dictionary<string, string>()
                                {
                                    ["openid"] = "Open Id"
                                }
                            }
                        },
                    }
                };
            });

            options.AddOperationTransformer(async (op, ctx, ct) =>
            {
                var hasAuthorizeAttribute = ctx.Description.ActionDescriptor.EndpointMetadata.OfType<AuthorizeAttribute>().Any();
                if (hasAuthorizeAttribute)
                {
                    op.Security ??= new List<OpenApiSecurityRequirement>();
                    op.Security.Add(new OpenApiSecurityRequirement()
                    {
                        {
                            new OpenApiSecurityScheme()
                            {
                                Reference = new OpenApiReference()
                                {
                                    Type = ReferenceType.SecurityScheme, Id = "Bearer"
                                }
                            }, 
                            ["openapi"]
                        }
                    });
                }
            });

            options.AddOperationTransformer(async (op, ctx, ct) =>
            {
                var displayName = ctx.Description.ActionDescriptor.DisplayName;
                if (string.IsNullOrEmpty(displayName))
                    return;
                
                op.Summary = displayName;
            });
            
            options.AddScalarTransformers();
        });

        return services;
    }

}