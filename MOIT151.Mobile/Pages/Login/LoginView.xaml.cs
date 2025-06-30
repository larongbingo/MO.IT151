using NewRelic.MAUI.Plugin;

namespace MOIT151.Mobile.Pages.Login;

public partial class LoginView
{
    public LoginView(LoginViewModel viewModel)
    {
        BindingContext = viewModel;
        InitializeComponent();
        CrossNewRelic.Current.TrackShellNavigatedEvents();
    }
}