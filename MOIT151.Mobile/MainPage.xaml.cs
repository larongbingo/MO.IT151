using Auth0.OidcClient;
using CommunityToolkit.Maui.Alerts;
using CommunityToolkit.Maui.Core;
using Newtonsoft.Json;

namespace MOIT151.Mobile;

public partial class MainPage : ContentPage
{
	private readonly Auth0Client _auth0Client;
	
	public MainPage(Auth0Client auth0Client)
	{
		_auth0Client = auth0Client;
		InitializeComponent();
	}

	private async void OnCounterClicked(object sender, EventArgs e)
	{
		var loginResult = await _auth0Client.LoginAsync();
		
		Console.WriteLine(JsonConvert.SerializeObject(loginResult));
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

