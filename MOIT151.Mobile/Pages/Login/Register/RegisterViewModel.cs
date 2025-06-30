using CommunityToolkit.Maui.Alerts;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using MOIT151.Application;
using MOIT151.Mobile.Services;

namespace MOIT151.Mobile.Pages.Login.Register;

public partial class RegisterViewModel(
    IMoit151WebClient webClient, IAuthenticationService authenticationService, INavigationService navigationService) : ObservableObject
{
    [ObservableProperty] private string username = string.Empty;

    [RelayCommand]
    private async Task RegisterAsync(CancellationToken ct = default)
    {
        if (string.IsNullOrWhiteSpace(Username))
        {
            await Toast.Make("Username is required").Show(ct);
            return;
        }

        var result = await webClient.CreateAccountAsync(
            authenticationService.AccessToken, new IMoit151WebClient.CreateAccountDto(Username), ct);
        if (!result.IsSuccessful)
        {
            await Toast.Make("Failed to register").Show(ct);
            return;
        }
        else
        {
            await navigationService.GoToAsync("///FilesView");
        }
    }
}