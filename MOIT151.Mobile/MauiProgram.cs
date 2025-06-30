using Auth0.OidcClient;
using CommunityToolkit.Maui;
using Microsoft.Extensions.Logging;
using Microsoft.Maui.LifecycleEvents;
using MOIT151.Mobile.Pages.Login;
using MOIT151.Mobile.Pages.Login.Register;
using MOIT151.Mobile.Services;
using NewRelic.MAUI.Plugin;
using Refit;

namespace MOIT151.Mobile;

public static class MauiProgram
{
	public static MauiApp CreateMauiApp()
	{
		var builder = MauiApp.CreateBuilder();
		builder
			.UseMauiApp<App>()
			.UseMauiCommunityToolkit()
			.ConfigureFonts(fonts =>
			{
				fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
				fonts.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
			});

		builder.Services.AddTransient<LoginView>();
		builder.Services.AddTransient<LoginViewModel>();
		builder.Services.AddTransientWithShellRoute<RegisterView, RegisterViewModel>("///LoginView/RegisterView");
		
		builder.Services.AddSingleton<INavigationService, NavigationService>();
		
		builder.Services.AddSingleton(new Auth0Client(new Auth0ClientOptions()
		{
			Domain = "ewan.au.auth0.com",
			ClientId = "h5m8clc3ztoWe0brx1qHZR9FDQ7GIltL",
			RedirectUri = "moit151://callback",
			PostLogoutRedirectUri = "moit151://callback",
			Scope = "openid",
		}));
		builder.Services.AddSingleton<IAuthenticationService, AuthenticationService>();

		builder.Services.AddRefitClient<IMoit151WebClient>(new RefitSettings()
			{
				ExceptionFactory = _ => Task.FromResult<Exception>(null),
				DeserializationExceptionFactory = (_, _) => Task.FromResult<Exception>(null),
			})
			.ConfigureHttpClient(c => c.BaseAddress = new Uri("https://moit151-webapi-v4sv3.ondigitalocean.app/"))
			.ConfigurePrimaryHttpMessageHandler(_ => CrossNewRelic.Current.GetHttpMessageHandler());
		
		builder.Logging.AddDebug();

		CrossNewRelic.Current.HandleUncaughtException();
		
#if ANDROID
		CrossNewRelic.Current.Start("AAb9fbd764a3f55c7a92d6ccc64ca5a305f575e81a-NRMA");
#endif

		return builder.Build();
	}
}


