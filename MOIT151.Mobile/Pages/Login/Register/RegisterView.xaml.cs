namespace MOIT151.Mobile.Pages.Login.Register;

public partial class RegisterView
{
    public RegisterView(RegisterViewModel viewModel)
    {
        BindingContext = viewModel;
        InitializeComponent();
    }
}