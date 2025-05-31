using System.Security.Claims;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using NSwag;
using NSwag.AspNetCore;
using NSwag.Generation.Processors.Security;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddAuthentication(options =>
    {
        options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
        options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
    })
    .AddJwtBearer(options =>
    {
        options.Authority = "https://ewan.au.auth0.com";
        options.Audience = "https://ewan/api";
        options.TokenValidationParameters = new TokenValidationParameters
        {
            NameClaimType = ClaimTypes.NameIdentifier
        };
    });


builder.Services.AddOpenApiDocument(options =>
{
    options.DocumentName = "CreditZone";
    options.Title = "CreditZone";
    options.Description = "CreditZone API";
    options.Version = "v1";
    options.DocumentProcessors.Add(new SecurityDefinitionAppender(
        "Bearer",
        new OpenApiSecurityScheme
        {
            Description = "JWT Token from Auth0",
            Name = "Authorization",
            In = OpenApiSecurityApiKeyLocation.Header,

            Type = OpenApiSecuritySchemeType.OAuth2,
            Flows = new OpenApiOAuthFlows()
            {
                Implicit = new OpenApiOAuthFlow
                {
                    Scopes = new Dictionary<string, string>
                    {
                        { "openid", "Open Id" }
                    },

                    AuthorizationUrl = "https://ewan.au.auth0.com/authorize?audience=https://ewan/api"
                }
            }
        })
    );
});

builder.Services.AddOpenApi();

var app = builder.Build();

app.MapOpenApi();

app.UseHttpsRedirection();


var summaries = new[]
{
    "Freezing", "Bracing", "Chilly", "Cool", "Mild", "Warm", "Balmy", "Hot", "Sweltering", "Scorching"
};

app.MapGet("/weatherforecast", () =>
    {
        var forecast = Enumerable.Range(1, 5).Select(index =>
                new WeatherForecast
                (
                    DateOnly.FromDateTime(DateTime.Now.AddDays(index)),
                    Random.Shared.Next(-20, 55),
                    summaries[Random.Shared.Next(summaries.Length)]
                ))
            .ToArray();
        return forecast;
    })
    .WithName("GetWeatherForecast");


app.UseOpenApi();

app.UseSwaggerUi(options =>
{
    options.OAuth2Client = new OAuth2ClientSettings
    {
        ClientId = "h5m8clc3ztoWe0brx1qHZR9FDQ7GIltL",
        AppName = "MOIT151",
    };
});


app.Run();

record WeatherForecast(DateOnly Date, int TemperatureC, string? Summary)
{
    public int TemperatureF => 32 + (int)(TemperatureC / 0.5556);
}
