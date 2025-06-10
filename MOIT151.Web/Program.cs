using System.Security.Claims;
using Amazon.S3;
using Amazon.S3.Model;
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

builder.Services.AddCreateFileUploadUseCases();

builder.Services.AddValidateFileUpload();

builder.Services.AddGetFilePresignedUrl();

var app = builder.Build();

app.MapOpenApi();

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapGet("/weatherforecast", () =>
    {
        string[] summaries =
        [
            "Freezing", 
            "Bracing", 
            "Chilly", 
            "Cool", 
            "Mild", 
            "Warm", 
            "Balmy", 
            "Hot", 
            "Sweltering", 
            "Scorching"
        ];

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
    .RequireAuthorization(nameof(NoopAuthorizationRequirement))
    .WithTags("User")
    .WithName("CreateUser");

api.MapGet("/user", async Task<IResult> ([FromServices] IExternalIdentityService identityService, 
        [FromServices]IUserRepository repository) =>
    {
        var externalId = identityService.GetExternalUserId();
        if (externalId is null)
            return Results.Unauthorized();
        var user = await repository.GetByExternalIdAsync(externalId);
        if (user is null)
            return Results.NotFound();
        return Results.Ok(user);
    })
    .RequireAuthorization(nameof(NoopAuthorizationRequirement))
    .WithTags("User")
    .WithName("GetUser");

api.MapPost("/file", async Task<IResult> ([FromServices] IExternalIdentityService identityService, 
    [FromServices] IMediator mediator, CancellationToken ct) =>
    {
        var user = await identityService.GetUserAsync(ct);
        if (user is null)
            return Results.Unauthorized();

        var command = new CreateFileUpload.Request(user.Id);
        
        var result = await mediator.Send(command, ct);
        
        if (!result.IsSuccess)
            return Results.BadRequest(result.ErrorMessages);
        return Results.Created("/api/file", result.Value);
    })
    .RequireAuthorization()
    .WithTags("File")
    .WithName("CreateFileUpload");

api.MapPut("/file", async Task<IResult> ([FromQuery] Guid fileId, 
        [FromServices] IExternalIdentityService identityService, [FromServices] IMediator mediator, 
        CancellationToken ct) =>
    {
        var user = await identityService.GetUserAsync(ct);
        if (user is null)
            return Results.Unauthorized();

        var command = new ValidateFileUpload.Request(user.Id, fileId);

        var result = await mediator.Send(command, ct);

        if (!result.IsSuccess)
            return Results.BadRequest(result.ErrorMessages);
        return Results.Ok(result.Value);
    })
    .RequireAuthorization()
    .WithTags("File")
    .WithName("ValidateUpload");

api.MapGet("/file", async Task<IResult> ([FromServices] IExternalIdentityService identityService, 
        [FromServices] IFileRepository fileRepository, CancellationToken ct) =>
    {
        var user = await identityService.GetUserAsync(ct);
        if (user is null)
            return Results.Unauthorized();

        var files = await fileRepository.GetListByUserIdAsync(user.Id, ct);
        return Results.Ok(files);
    })
    .RequireAuthorization()
    .WithTags("File")
    .WithName("GetFiles");

api.MapGet("/file/{fileId:guid}", async Task<IResult> ([FromRoute] Guid fileId, [FromServices] IMediator mediator, 
        [FromServices] IExternalIdentityService identityService, CancellationToken ct) =>
    {
        var user = await identityService.GetUserAsync(ct);
        if (user is null)
            return Results.Unauthorized();

        var command = new GetFilePresignedUrl.Request(user.Id, fileId);

        var result = await mediator.Send(command, ct);

        if (!result.IsSuccess)
            return Results.NotFound(result.ErrorMessages);
        return Results.Ok(result.Value);
    })
    .RequireAuthorization()
    .WithTags("File")
    .WithName("GetFileDownloadLink");

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