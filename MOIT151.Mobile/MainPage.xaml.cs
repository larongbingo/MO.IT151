using Auth0.OidcClient;
using CommunityToolkit.Maui.Alerts;
using CommunityToolkit.Maui.Core;
using Newtonsoft.Json;

namespace MOIT151.Mobile;

public partial class MainPage : ContentPage
{
	private readonly Auth0Client _auth0Client;
	private readonly IMoit151WebClient _moit151WebClient;
	private string? _accessToken;
	
	public MainPage(Auth0Client auth0Client, IMoit151WebClient moit151WebClient)
	{
		_auth0Client = auth0Client;
		_moit151WebClient = moit151WebClient;
		InitializeComponent();
	}

	private async void OnCounterClicked(object sender, EventArgs e)
	{
		var loginResult = await _auth0Client.LoginAsync();

		if (loginResult.IsError)
		{
			Toast.Make(loginResult.ErrorDescription, ToastDuration.Long).Show();
			return;
		}
		
		Toast.Make("Login successful", ToastDuration.Long).Show();
		
		var userInfo = await _moit151WebClient.GetAccountAsync(loginResult.AccessToken);
		
		if (userInfo is null)
		{
			Toast.Make("Failed to get user info", ToastDuration.Long).Show();
			return;
		}
		
		var json = JsonConvert.SerializeObject(userInfo, Formatting.Indented);
		
		await DisplayAlert("User Info", json, "OK");
	}

	private async void OnSelectFileClicked(object? sender, EventArgs e)
	{
		var result = await FilePicker.PickAsync();

		if (result is null)
		{
			Toast.Make("No file selected", ToastDuration.Long);
			return;
		}
		
	}
}

