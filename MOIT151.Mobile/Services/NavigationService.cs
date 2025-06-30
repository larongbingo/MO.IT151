namespace MOIT151.Mobile.Services;

public class NavigationService : INavigationService
{
    public async Task GoToAsync(string route)
        => await Shell.Current.GoToAsync(route);
}

public interface INavigationService
{
    Task GoToAsync(string route);
}