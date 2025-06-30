using CommunityToolkit.Maui.Alerts;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using MOIT151.Mobile.Services;

namespace MOIT151.Mobile.Pages.Login;

public partial class LoginViewModel(
    IAuthenticationService authenticationService,
    IMoit151WebClient webClient,
    INavigationService navigationService) : ObservableObject
{
    [RelayCommand]
    private async Task LoginAsync(CancellationToken ct = default)
    {
        var isLoggedIn = await authenticationService.AuthenticateAsync(ct);

        if (!isLoggedIn)
        {
            await Toast.Make("Login Failed").Show(ct);
            return;
        }
        
        var user = await webClient.GetAccountAsync(authenticationService.AccessToken, ct);
        if (user is null)
        {
            await Toast.Make("First Logged In User, enter Username to proceed").Show(ct);
            await navigationService.GoToAsync("///LoginView/RegisterView");
            return;
        }
        
        await navigationService.GoToAsync("///FilesView");
    }
}