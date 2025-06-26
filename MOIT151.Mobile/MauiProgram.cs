using Microsoft.Extensions.Logging;

namespace MOIT151.Mobile;

public static class MauiProgram
{
	public static MauiApp CreateMauiApp()
	{
		var builder = MauiApp.CreateBuilder();
		builder
			.UseMauiApp<App>()
			.ConfigureFonts(fonts =>
			{
				fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
				fonts.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
			});

		builder.Services.AddSingleton<MainPage>();

		builder.Services.AddSingleton(new Auth0Client(new Auth0ClientOptions()
		{
			Domain = "ewan.au.auth0.com",
			ClientId = "h5m8clc3ztoWe0brx1qHZR9FDQ7GIltL",
			RedirectUri = "moit151://callback",
			PostLogoutRedirectUri = "moit151://callback",
			Scope = "openid",
		}));
		
#if DEBUG
		builder.Logging.AddDebug();
#endif

		return builder.Build();
	}
}
