namespace MOIT151.Mobile.Pages.Files;

public partial class FilesView : ContentPage
{
    public FilesView(FilesViewModel viewModel)
    {
        BindingContext = viewModel;
        InitializeComponent();
    }
}