using CommunityToolkit.Maui.Alerts;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using MOIT151.Application;
using MOIT151.Mobile.Services;
using NewRelic.MAUI.Plugin;
using Newtonsoft.Json;

namespace MOIT151.Mobile.Pages.Login.Register;

public partial class RegisterViewModel(
    IMoit151WebClient webClient, IAuthenticationService authenticationService, INavigationService navigationService) : ObservableObject
{
    [ObservableProperty] private string username = string.Empty;

    [RelayCommand]
    private async Task RegisterAsync(CancellationToken ct = default)
    {
        CrossNewRelic.Current.RecordMetric("First Time Login", "Authentication");
        var interactionId = CrossNewRelic.Current.StartInteraction("FirstTimeLogin");
        if (string.IsNullOrWhiteSpace(Username))
        {
            await Toast.Make("Username is required").Show(ct);
            return;
        }

        var result = await webClient.CreateAccountAsync(
            authenticationService.AccessToken, new IMoit151WebClient.CreateAccountDto(Username), ct);
        if (result.IsSuccessStatusCode)
        {
            var rawString = await result.Content.ReadAsStringAsync(ct);
            var responseObject = JsonConvert.DeserializeObject<CreateAccountResponse>(rawString);
            CrossNewRelic.Current.SetUserId(responseObject?.Id.ToString());
            await navigationService.GoToAsync("///FilesView");
        }
        else
        {
            await Toast.Make("Failed to register").Show(ct);
        }
        
        CrossNewRelic.Current.EndInteraction(interactionId);
    }

    internal record CreateAccountResponse(Guid Id);
}