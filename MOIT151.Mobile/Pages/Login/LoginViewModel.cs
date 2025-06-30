using CommunityToolkit.Maui.Alerts;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using MOIT151.Mobile.Services;
using NewRelic.MAUI.Plugin;

namespace MOIT151.Mobile.Pages.Login;

public partial class LoginViewModel(
    IAuthenticationService authenticationService,
    IMoit151WebClient webClient,
    INavigationService navigationService) : ObservableObject
{
    [RelayCommand]
    private async Task LoginAsync(CancellationToken ct = default)
    {
        var interactionId= CrossNewRelic.Current.StartInteraction("Login");
        var isLoggedIn = await authenticationService.AuthenticateAsync(ct);

        if (!isLoggedIn)
        {
            CrossNewRelic.Current.RecordMetric("Login Failed", "Authentication");
            await Toast.Make("Login Failed").Show(ct);
            return;
        }
        
        var user = await webClient.GetAccountAsync(authenticationService.AccessToken, ct);
        CrossNewRelic.Current.EndInteraction(interactionId);
        
        if (user is null)
        {
            await Toast.Make("First Logged In User, enter Username to proceed").Show(ct);
            await navigationService.GoToAsync("///LoginView/RegisterView");
            return;
        }
        
        CrossNewRelic.Current.RecordMetric("Login Success", "Authentication");
        CrossNewRelic.Current.SetUserId(user.Id.ToString());
        
        await navigationService.GoToAsync("///FilesView");
    }
}