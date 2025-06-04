using System.Security.Claims;
using Mediator;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using MOIT151.Application;
using MOIT151.Infrastructure.Data;
using MOIT151.Infrastructure.FileStorage;
using MOIT151.Web.Authorization;
using MOIT151.Web.Modules;
using NSwag;
using NSwag.AspNetCore;
using NSwag.Generation.Processors;
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

builder.Services.AddMediator(x => x.ServiceLifetime = ServiceLifetime.Transient);

builder.Services.AddMOIT151Authorization();

builder.Services.AddMOIT151Data();

builder.Services.AddMOIT151S3Storage();

builder.Services.AddMOIT151OpenApi();

builder.Services.AddCreateUserUseCase();

var app = builder.Build();

app.MapOpenApi();

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapGet("/weatherforecast", () =>
    {
        var summaries = new[]
        {
            "Freezing", "Bracing", "Chilly", "Cool", "Mild", "Warm", "Balmy", "Hot", "Sweltering", "Scorching"
        };

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
    .RequireAuthorization()
    .WithName("GetWeatherForecast");

var api = app.MapGroup("/api");
api.MapPost("/user", async Task<IResult> (
        [FromServices] IMediator mediator, [FromServices] IExternalIdentityService identityService,
        [FromBody] CreateUserRequest request) =>
    {
        var externalId = identityService.GetExternalUserId();
        if (externalId is null)
            return Results.Unauthorized();
        
        var command = new CreateUser.Request(externalId, request.Username);
        
        var result = await mediator.Send(command);

        if (result.IsSuccess)
            return Results.Created("/api/user", result.Value);
        return Results.BadRequest(result.ErrorMessages);
    })
    .RequireAuthorization()
    .WithTags("User")
    .WithName("CreateUser");

api.MapGet("/user", async Task<IResult> ([FromServices] IExternalIdentityService IExternalIdentityService, 
        [FromServices]IUserRepository repository) =>
    {
        var externalId = IExternalIdentityService.GetExternalUserId();
        if (externalId is null)
            return Results.Unauthorized();
        var user = await repository.GetByExternalIdAsync(externalId);
        return Results.Ok(user);
    })
    .RequireAuthorization()
    .WithTags("User")
    .WithName("GetUser");

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

public record CreateUserRequest(string Username);